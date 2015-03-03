import java.lang.Runnable;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chamika on 3/3/2015.
 */


public class DebugServer implements Runnable{

    List<SuccessQuery> successQueryList = new ArrayList<SuccessQuery>();
    List<NodeQuery> nodeQueryList = new ArrayList<NodeQuery>();

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
                System.out.println("");
                System.out.println("########################################");
                System.out.println("RECEIVED QUERY : " + sentence);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                System.out.println("RECEIVED PORT : "+port);
                //This is used to Decompose the input query in to relevant category.
                queyDecomposer(sentence);


                System.out.println("########################################");

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

        if(stringList.contains("INFO")){

            System.out.println("Query TYPE : INFO");
            NodeQuery tempNQ = new NodeQuery();
            tempNQ.setSourceIP(splittedStrings[0]);
            tempNQ.setSourcePort(Integer.parseInt(splittedStrings[1]));
            tempNQ.setFileName(splittedStrings[3]);
            tempNQ.setHops(Integer.parseInt(splittedStrings[4]));
            tempNQ.setIP(splittedStrings[5]);
            tempNQ.setPort(Integer.parseInt(splittedStrings[6]));
            tempNQ.setFileAvaillable(Boolean.parseBoolean(splittedStrings[7]));
            tempNQ.setNoOfForwardings(Integer.parseInt(splittedStrings[8]));

            nodeQueryList.add(tempNQ);

            System.out.println(tempNQ);

        }else if(stringList.contains("LATE")){

            System.out.println("Query TYPE : LATE");
            SuccessQuery tempSQ = new SuccessQuery();
            tempSQ.setSourceIP(splittedStrings[0]);
            tempSQ.setSourcePort(Integer.parseInt(splittedStrings[1]));
            tempSQ.setFileName(splittedStrings[3]);
            tempSQ.setLatency(Double.parseDouble(splittedStrings[4]));
            tempSQ.setHops(Integer.parseInt(splittedStrings[5]));

            successQueryList.add(tempSQ);

            System.out.println(tempSQ);

        }else{

            System.out.println("Not a Valid Query!");
            //Error Code Return

        }

    }


}
