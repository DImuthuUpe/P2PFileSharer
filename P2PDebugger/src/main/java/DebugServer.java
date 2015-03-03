import java.lang.Runnable;
import java.net.*;

/**
 * Created by Chamika on 3/3/2015.
 */


public class DebugServer implements Runnable{

    @Override
    public void run() {
        try{
            DatagramSocket serverSocket = openSocket(5000);
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            while(true)
            {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                byte data[] = receivePacket.getData();
                String sentence = new String(data,0,receivePacket.getLength() );
                System.out.println("RECEIVED: " + sentence);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                System.out.println("PORT: "+port);
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);

            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void parseMessage(String message){

    }

    public DatagramSocket openSocket(int portNo){
        try{
            DatagramSocket serverSocket = new DatagramSocket(portNo, InetAddress.getByName("localhost"));
            System.out.println("Socket created .......");
            return serverSocket;
        }catch (UnknownHostException ex){
            ex.printStackTrace();
            System.out.println("Could Not fetch the ip to create the socket");
            System.exit(0);
        }catch (SocketException ex){
            ex.printStackTrace();
            System.out.println("Unable to open socket on port "+portNo);
            System.exit(0);
        }
        return null;
    }


}
