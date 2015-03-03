import beans.JoinAck;
import beans.Message;
import beans.Node;
import beans.TransportAddress;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class UDPServer implements Runnable{
    private Controller controller;
    private TransportAddress self;
    private InetAddress address;
    private int port;

    public UDPServer(Controller controller,InetAddress address, int port){
        this.controller = controller;
        this.address = address;
        this.port = port;
        this.self = new TransportAddress(address.getHostAddress(),port);
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
                //System.out.println("RECEIVED: " + query);
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


    public Message parseMessage(String message) throws IOException {
        String parts[] = message.split(" ");
        if(parts[1].equals("JOIN")){
            //0027 JOIN 64.12.123.190 432
            //Need to add to IP table
            Node node = new Node(parts[2],Integer.parseInt(parts[3]),null);
            Node[] nodes ={node};
            controller.addToIpTable(nodes);
            JoinAck ack = new JoinAck();
            ack.setCode(0);
            return ack;
        }else if(parts[1].equals("SER")){
            System.out.println(message);
            String filename = parts[6];
            List<String> matchingWords = controller.getMatchingWords(filename);
            TransportAddress src = new TransportAddress(parts[2],Integer.parseInt(parts[3]));
            int hops = Integer.parseInt(parts[parts.length-1]);
            if(matchingWords.size()>0){ //if there are matching words
                System.out.println("File found");
                Communicator communicator = new Communicator();
                communicator.responseFile(src,self,matchingWords,hops,filename);
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
                Communicator communicator = new Communicator();
                communicator.requestFile(self,src,targets,filename,hops);
            }
        }else if(parts[1].equals("SEROK")){
            System.out.println(message);
        }
        return null;
    }

    public DatagramSocket openSocket(){
        try{
            DatagramSocket serverSocket = new DatagramSocket(port, address);
            System.out.println("Socket created ....... ip "+address.getHostName()+" port "+port);
            return serverSocket;
        }catch (SocketException ex){
            ex.printStackTrace();
            System.out.println("Unable to open socket on port "+port);
            System.exit(0);
        }
        return null;
    }
}
