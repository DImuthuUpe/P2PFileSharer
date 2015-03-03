import java.lang.Runnable;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                //This is used to Decompose the input query in to relevant category. 
                queyDecomposer(sentence);

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

    public void queyDecomposer(String queryString){

        String[] splittedStrings = queryString.trim().split(" ");
        List<String> stringList = Arrays.asList(splittedStrings);

        if(stringList.contains("HOPS")){
            System.out.println("Contains HOPS");
        }else if(stringList.contains("LATE")){
            System.out.println("Contains LATE");
        }else if(stringList.contains("MSGS")) {
            System.out.println("Contains MSGS");
        }else if(stringList.contains("DEGS")){
            System.out.println("Contains DEGS");
        }else{
            System.out.println("Not a Valid Query!");
            //Error Code Return
        }

    }


}
