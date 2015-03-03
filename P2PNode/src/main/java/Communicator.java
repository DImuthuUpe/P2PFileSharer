import beans.*;

import java.io.IOException;
import java.net.*;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class Communicator {

    private String bsServer= "127.0.0.1";
    private int bsPort = 5000;

    public BSAck register(Node self) throws SocketException,UnknownHostException,IOException{

        String query = "REG "+self.getIp()+" "+self.getPort()+" "+self.getUserName();
        int queryLength = query.length()+5;
        query = String.format("%04d", queryLength) + " " + query;

        String newQuery = sendQuery(query,bsServer,bsPort);
        BSAck ack = new BSAck();
        ack.initialize(newQuery);

        return ack;
    }

    public JoinAck join(Node self,Node node) throws SocketException,UnknownHostException,IOException{

        String query = "JOIN "+ self.getIp()+" "+self.getPort();
        int queryLength = query.length()+5;
        query = String.format("%04d", queryLength) + " " + query;

        String newQuery = sendQuery(query,node.getIp(),node.getPort());
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

    public BSAck requestNewNodes(){
        return null;
    }

    public RequestFileAck requestFile(Node self, Node target, String fileName, int hops){
        return null;
    }

    public String sendQuery(String query,String ip, int port)throws SocketException,UnknownHostException,IOException{
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
}
