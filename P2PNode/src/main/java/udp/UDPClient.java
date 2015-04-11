package udp;

import beans.*;
import util.ConfigManager;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class UDPClient {

    private String bsServer;
    private int bsPort;
    private String debugServer;
    private int debugPort;

    private boolean debug=true;

    public UDPClient(){
        bsServer = ConfigManager.getProperty(ConfigManager.BS_SERVER_IP);
        bsPort = Integer.parseInt(ConfigManager.getProperty(ConfigManager.BS_PORT));
        debugServer = ConfigManager.getProperty(ConfigManager.DS_SERVER_IP);
        debugPort = Integer.parseInt(ConfigManager.getProperty(ConfigManager.DS_PORT));
    }

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

    public UnRegisterAck unRegister(Node self) throws IOException {
        String query = "UNREG "+self.getIp()+" "+self.getPort()+" "+self.getUserName();
        int queryLength = query.length()+5;
        query = String.format("%04d", queryLength) + " " + query;

        String newQuery = sendAndReceiveQuery(query, bsServer, bsPort);
        UnRegisterAck ack = new UnRegisterAck();
        ack.initialize(newQuery);

        return ack;
    }

    public LeaveAck leave(TransportAddress self, TransportAddress remote) throws IOException {
        String query = "LEAVE "+ self.getIp()+" "+self.getPort();
        int queryLength = query.length()+5;
        query = String.format("%04d", queryLength) + " " + query;

        String newQuery = sendAndReceiveQuery(query, remote.getIp(), remote.getPort());
        LeaveAck ack = new LeaveAck();
        ack.initialize(newQuery);
        return ack;
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
        //System.out.println("sent");
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        //System.out.println("received");
        String newQuery =  new String(receivePacket.getData(), 0, receivePacket.getLength());
        //System.out.println(newQuery);
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
