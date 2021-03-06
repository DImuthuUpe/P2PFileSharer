package udp;

import beans.JoinAck;
import beans.Message;
import beans.Node;
import beans.TransportAddress;
import beans.LeaveAck;
import p2p.Controller;
import rpc.node;
import udp.UDPClient;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class UDPServer implements Runnable{
    private Controller controller;
    private TransportAddress self;
    private String userName;

    public UDPServer(Controller controller,String address, int port,String userName){
        this.controller = controller;
        this.self = new TransportAddress(address,port);
        this.userName = userName;
    }


    @Override
    public void run() {
        try{
            DatagramSocket serverSocket = openSocket();
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            while(true)
            {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                byte data[] = receivePacket.getData();
                String query = new String(data,0,receivePacket.getLength() );
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                Message returnMessage = parseMessage(query);
                if(returnMessage!=null){
                    String newQuery = returnMessage.convertToQuery();
                    sendData = newQuery.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private String[] split(String str){
        List<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);
        while (m.find()){
            list.add(m.group(1).replaceAll("^\"|\"$", ""));
        }
        return list.toArray(new String[list.size()]);
    }

    public Message parseMessage(String message) throws IOException {
        String parts[] = split(message);
        if(parts[1].equals("JOIN")){
            //0027 JOIN 64.12.123.190 432
            //Need to add to IP table
            Node node = new Node(parts[2],Integer.parseInt(parts[3]),null);
            Node[] nodes ={node};
            controller.addToIpTable(nodes);
            JoinAck ack = new JoinAck();
            ack.setCode(0);
            return ack;
        }else if(parts[1].equals("SER")){ //at search query
            //System.out.println(message);

            String filename = parts[6];
            List<String> matchingWords = controller.getMatchingWords(filename);
            TransportAddress src = new TransportAddress(parts[2],Integer.parseInt(parts[3]));
            int hops = Integer.parseInt(parts[parts.length-1]);

            if(matchingWords.size()>0){ //if there are matching words
                //System.out.println("File found");
                UDPClient client = new UDPClient();
                client.responseFile(src,self,matchingWords,hops,filename);

            }else{
                //length SER src_ip src_port pred_ip pred_port file_name hops
                String predIp = parts[4];
                int predPort = Integer.parseInt(parts[5]);

                TransportAddress pred = new TransportAddress(predIp,predPort);
                Set<TransportAddress> ipTable = controller.getIpTable();
                List<TransportAddress> forwardingNodes = new ArrayList<TransportAddress>();
                Iterator<TransportAddress> it = ipTable.iterator();
                while (it.hasNext()){
                    TransportAddress tp = it.next();
                    if(!tp.equals(pred)){
                        forwardingNodes.add(tp);
                    }
                }

                TransportAddress[] targets = new TransportAddress[forwardingNodes.size()];
                targets = forwardingNodes.toArray(targets);
                UDPClient client = new UDPClient();
                client.requestFile(self,src,targets,filename,hops);
            }
        }else if(parts[1].equals("SEROK")){ //at search ok query
            //0049 SEROK 1 127.0.0.1 3002 hops original "file1_2"
            //System.out.println(message);

            String fileName = parts[6];
            if(controller.searchTable.containsKey(fileName)){
                Long currentTime = System.currentTimeMillis();
                Long latency = currentTime - controller.searchTable.get(fileName);
                controller.searchTable.remove(fileName);
                String[] fileList = new String[parts.length-7];
                System.arraycopy(parts,7,fileList,0,fileList.length);
                int hops = Integer.parseInt(parts[5]);

                TransportAddress src = new TransportAddress(parts[3],Integer.parseInt(parts[4]));
                UDPClient client = new UDPClient();
                client.publishResults(self,src,fileName,latency, Controller.MAX_HOPS-hops,fileList);

            }

        }else if(parts[1].equals("LEAVE")){
            Node node = new Node(parts[2],Integer.parseInt(parts[3]),null);
            controller.removeFromIpTable(node);
            System.out.println("Node "+node.getIp()+" "+node.getPort()+ " left network");
            LeaveAck ack = new LeaveAck();
            ack.setCode(0);
            return ack;
        }else if(parts[1].equals("CONSOLE")){ //
            //length CONSOLE LEAVE
            if(parts[2].equals("LEAVE")){
                System.out.println("Leaving the network");
                UDPClient client = new UDPClient();
                client.unRegister(new Node(self.getIp(),self.getPort(),userName));
                System.out.println("Un-registered from BS");
                Iterator<TransportAddress> it = controller.getIpTable().iterator();

                while(it.hasNext()){
                    TransportAddress node = it.next();
                    System.out.println("Leaving node "+node.getIp()+" "+node.getPort());
                    client.leave(self,node);
                }
                System.exit(0);
            }else if(parts[2].equals("SEARCH")){
                System.out.println("Searching for file : "+parts[3]);
                controller.searchFile(parts[3]);
            }else if(parts[2].equals("SEARCHRPC")){
            	Set<TransportAddress> ipTable = controller.getIpTable();
            	Iterator<TransportAddress> it = ipTable.iterator();
            	while (it.hasNext()){
                    TransportAddress tp = it.next();
                    
                    	try {
          			      TTransport transport;
          			     
          			      transport = new TSocket(tp.getIp(), tp.getPort());
          			      System.out.println(tp.getIp()+ " " + tp.getPort());
          			      transport.open();

          			      TProtocol protocol = new  TBinaryProtocol(transport);
          			      node.Client client = new node.Client(protocol);

          			      client.search(self.getIp(), self.getPort(), parts[3], Controller.MAX_HOPS);

          			      transport.close();
          			    } catch (TException x) {
          			      x.printStackTrace();
          			    }  
                    
                }
            }
        }
        return null;
    }

    public DatagramSocket openSocket(){
        try{
            DatagramSocket serverSocket = new DatagramSocket(self.getPort(), InetAddress.getByName(self.getIp()));
            System.out.println("Socket created ....... ip "+self.getIp()+" port "+self.getPort());
            return serverSocket;
        }catch (IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to open socket on port "+self.getPort());
            System.exit(0);
        }
        return null;
    }
}
