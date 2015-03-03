import beans.JoinAck;
import beans.Message;
import beans.Node;

import java.net.*;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class UDPServer implements Runnable{
    private Controller controller;
    private InetAddress address;
    private int port;

    public UDPServer(Controller controller,InetAddress address, int port){
        this.controller = controller;
        this.address = address;
        this.port = port;
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
                System.out.println("RECEIVED: " + query);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String newQuery = parseMessage(query).convertToQuery();
                sendData = newQuery.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public Message parseMessage(String message){
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
        }
        return null;
    }

    public DatagramSocket openSocket(){
        try{
            DatagramSocket serverSocket = new DatagramSocket(port, address);
            System.out.println("Socket created .......");
            return serverSocket;
        }catch (SocketException ex){
            ex.printStackTrace();
            System.out.println("Unable to open socket on port "+port);
            System.exit(0);
        }
        return null;
    }
}
