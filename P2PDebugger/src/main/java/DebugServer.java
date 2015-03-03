import java.lang.Runnable;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chamika on 3/3/2015.
 */


public class DebugServer implements Runnable{

    List<SuccessQuery> successQueryList = new ArrayList<SuccessQuery>();
    List<NodeQuery> nodeQueryList = new ArrayList<NodeQuery>();

    @Override
    public void run() {
        try{
            DatagramSocket serverSocket = openSocket(6000);
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
            System.out.println("Socket Created .......");
            return serverSocket;
        }catch (UnknownHostException ex){
            ex.printStackTrace();
            System.out.println("Could Not fetch the IP to create the Socket");
            System.exit(0);
        }catch (SocketException ex){
            ex.printStackTrace();
            System.out.println("Unable to open Socket on Port "+portNo);
            System.exit(0);
        }
        return null;
    }

    public void queyDecomposer(String queryString){

        List<String> stringList = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(queryString);
        while (m.find()) {
            stringList.add(m.group(1).replaceAll("^\"|\"$", ""));
        }

        if(stringList.contains("INFO")){

            System.out.println("Query TYPE : INFO");
            NodeQuery tempNQ = new NodeQuery();
            tempNQ.setSourceIP(stringList.get(1));
            tempNQ.setSourcePort(Integer.parseInt(stringList.get(2)));
            tempNQ.setFileName(stringList.get(3));
            tempNQ.setHops(Integer.parseInt(stringList.get(4)));
            tempNQ.setIP(stringList.get(5));
            tempNQ.setPort(Integer.parseInt(stringList.get(6)));
            tempNQ.setFileAvaillable(Boolean.parseBoolean(stringList.get(7)));
            tempNQ.setNoOfForwardings(Integer.parseInt(stringList.get(8)));

            //Query is added to List
            nodeQueryList.add(tempNQ);
            System.out.println(tempNQ);

            //Nodes will update
            Node test = new Node();


        }else if(stringList.contains("LATE")){

            System.out.println("Query TYPE : LATE");
            SuccessQuery tempSQ = new SuccessQuery();
            tempSQ.setSourceIP(stringList.get(1));
            tempSQ.setSourcePort(Integer.parseInt(stringList.get(2)));
            tempSQ.setFileName(stringList.get(3));
            tempSQ.setLatency(Double.parseDouble(stringList.get(4)));
            tempSQ.setHops(Integer.parseInt(stringList.get(5)));

            //Success Query is added
            successQueryList.add(tempSQ);
            System.out.println(tempSQ);

            //Nodes will update



        }else{

            System.out.println("Not a Valid Query!");
            //Error Code Return
            System.out.println("Success Query List : ");
            System.out.println(successQueryList);
            System.out.println("###############################");

            System.out.println("Node Query List : ");
            System.out.println(nodeQueryList);
            System.out.println("###############################");

        }

    }


}
