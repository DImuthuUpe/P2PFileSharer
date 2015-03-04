package udp;

import beans.*;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class UDPClient {

    private String bsServer= "127.0.0.1";
    private int bsPort = 5000;
    private String debugServer = "127.0.0.1";
    private int debugPort=6000;

    private boolean debug=true;

    public BSAck register(Node self) throws SocketException,UnknownHostException,IOException{

        String query = "REG "+self.getIp()+" "+self.getPort()+" "+self.getUserName();
        int queryLength = query.length()+5;
        query = String.format("%04d", queryLength) + " " + query;

        String newQuery = sendAndReceiveQuery(query, bsServer, bsPort);
        BSAck ack = new BSAck();
        ack.initialize(newQuery);

        return ack;
    }

    public JoinAck join(Node self,Node node) throws IOException{

        String query = "JOIN "+ self.getIp()+" "+self.getPort();
        int queryLength = query.length()+5;
        query = String.format("%04d", queryLength) + " " + query;

        String newQuery = sendAndReceiveQuery(query, node.getIp(), node.getPort());
        JoinAck ack = new JoinAck();
        ack.initialize(newQuery);
        return ack;
    }

    public UnRegisterAck unRegister(Node self){
        return null;
    }

    public LeaveAck leave(Node self, Node remote){
        return null;
    }

    public void requestFile(TransportAddress self,TransportAddress src, TransportAddress[] targets, String fileName, int hops) throws IOException {
        //length SER src_ip src_port pred_ip pred_port file_name hops
        hops --;
        if(hops>=0){
            String query = "SER "+src.getIp()+" "+src.getPort()+" "+self.getIp()+" "+self.getPort()+" \""+fileName+"\" "+hops;
            int queryLength = query.length()+5;
            query = String.format("%04d", queryLength) + " " + query;

            for(int i=0;i<targets.length;i++){
                fireAndForgetQuery(query,targets[i].getIp(),targets[i].getPort());
            }
        }

        if(debug){
            String query = "INFO "+src.getIp()+" "+src.getPort()+" \""+fileName+"\" "+hops+" "+self.getIp()+" "+self.getPort()+" false "+targets.length;
            fireAndForgetQuery(query,debugServer,debugPort);
        }
    }

    public void publishResults(TransportAddress self,TransportAddress src,String fileName,Long latency,int hops,String[] fileList) throws IOException {

        System.out.println("File : "+fileName +" is in "+src.getIp()+":"+src.getPort());
        System.out.println("Latency "+latency);
        System.out.println("Hopes "+hops);
        System.out.println("File List.....");
        for (int i=0;i<fileList.length;i++){
            System.out.println(fileList[i]);
        }

        if(debug){
            String query = "LATE "+self.getIp()+" "+self.getPort()+" \""+fileName+"\" "+latency+" "+hops;
            fireAndForgetQuery(query,debugServer,debugPort);
        }
    }

    public void responseFile(TransportAddress src,TransportAddress self,List<String> fileList,int hops,String fileName) throws IOException {
        //length SEROK no_files IP port hops original filename1 filename2
        String query = "SEROK "+fileList.size()+" "+self.getIp()+" "+self.getPort()+" "+hops+" \""+fileName+"\"";
        for(int i=0;i<fileList.size();i++){
            query+= " \""+fileList.get(i)+"\"";
        }
        int queryLength = query.length()+5;
        query = String.format("%04d", queryLength) + " " + query;

        fireAndForgetQuery(query,src.getIp(),src.getPort());

        if(debug){
            query = "INFO "+src.getIp()+" "+src.getPort()+" \""+fileName+"\" "+hops+" "+self.getIp()+" "+self.getPort()+" true 0";
            fireAndForgetQuery(query,debugServer,debugPort);
        }
    }

    public String sendAndReceiveQuery(String query,String ip, int port)throws SocketException,UnknownHostException,IOException{
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(ip);
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        sendData = query.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String newQuery =  new String(receivePacket.getData(), 0, receivePacket.getLength());
        return newQuery;
    }

    public void fireAndForgetQuery(String query,String ip, int port)throws SocketException,UnknownHostException,IOException{
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(ip);
        byte[] sendData = new byte[1024];
        sendData = query.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
    }
}
