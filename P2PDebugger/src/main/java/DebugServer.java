import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Runnable;
import java.net.*;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chamika on 3/3/2015.
 */

public class DebugServer implements Runnable{

    private static int version = 1;
    List<SuccessQuery> successQueryList = new ArrayList<SuccessQuery>();
    List<NodeQuery> nodeQueryList = new ArrayList<NodeQuery>();
    Hashtable<String, Node> nodeTable = new Hashtable<String, Node>();

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
                queryDecompose(sentence);

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

    /**
     * This method is used to create a SOCKET on a given PORT.
     * @param portNo - PORT number to create a socket
     * @return - created socket of DatagramSocket type
     */
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

    /**
     * This method is used to Decompose the input query to process with the process.
     * @param queryString - Input query string
     */
    public void queryDecompose(String queryString){

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
            int answered = 0;
            if(stringList.get(7).equalsIgnoreCase("true")){
                answered = 1;
            }

            String nodeKey = stringList.get(5)+":"+stringList.get(6);
            if(nodeTable.containsKey(nodeKey)){
                Node temp = nodeTable.get(nodeKey);
                temp.setMessagesReceived(temp.getMessagesReceived() + 1);
                temp.setMessagesForwarded(temp.getMessagesForwarded() + Integer.parseInt(stringList.get(8)));
                temp.setMessagesAnswered(temp.getMessagesAnswered() + answered);
                temp.setNodeCost(temp.getNodeCost() + 4.0);
                System.out.println("Node : "+nodeKey+" Updated - via INFO");
            }else{
                Node temp = new Node(stringList.get(5),Integer.parseInt(stringList.get(6)));
                temp.setMessagesForwarded(Integer.parseInt(stringList.get(8)));
                temp.setMessagesReceived(1);
                temp.setMessagesAnswered(answered);
                temp.setNodeDegree(1 + Integer.parseInt(stringList.get(8)));
                temp.setNodeCost(3.0);
                nodeTable.put(nodeKey,temp);
                System.out.println("Node : "+nodeKey+" Added - via INFO");
            }


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
            String nodeKey = stringList.get(1)+":"+stringList.get(2);
            if(nodeTable.containsKey(nodeKey)){
                Node temp = nodeTable.get(nodeKey);
                temp.setMessagesReceived(temp.getMessagesReceived() + 1);
                System.out.println("Node : "+nodeKey+" Updated - via LATE");
            }else{
                Node temp = new Node(stringList.get(1),Integer.parseInt(stringList.get(2)));
                temp.setMessagesReceived(1);
                temp.setMessagesAnswered(1);
                temp.setNodeCost(3.0);
                nodeTable.put(nodeKey, temp);
                System.out.println("Node : "+nodeKey+" Added - via LATE");
            }


        }else if(stringList.contains("SAVE")){
            //Save data to files
            saveSuccessQueries();
            saveQueryDetails();
            saveNodeDetails();

        }else if(stringList.contains("STAT")){
            //Show statistics

            //Hopes Statistics
            int minHopes = getMinHopes();
            int maxHopes = getMaxHopes();
            double averageHopes = getAverageHopes();
            double standardDeviationHopes = getStandardDeviationHopes(averageHopes);

            //Latency Statistics
            double minLatency = getMinLatency();
            double maxLatency = getMaxLatency();
            double averageLatency = getAverageLatency();
            double standardDeviationLatency = getStandardDeviationLatency(averageLatency);

            //Messages per node Statistics
            int minMessagesPerNode = getMinMessagesPerNode();
            int maxMessagesPerNode = getMaxMessagesPerNode();
            double averageMessagesPerNode = getAverageMessagesPerNode();
            double standardDeviationMessagesPerNode = getStandardDeviationMessagesPerNode(averageMessagesPerNode);

            //Node degree Statistics
            int minNodeDegree = getMinNodeDegree();
            int maxNodeDegree = getMaxNodeDegree();
            double averageNodeDegree = getAverageNodeDegree();
            double standardDeviationNodeDegree = getStandardDeviationNodeDegree(averageNodeDegree);

            try {
                System.out.println("File Write for Statistics is Started!");
                File file = new File("./src/Files/Statistics.txt");
                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("VERSION : "+version);
                version++;

                bw.newLine();
                bw.write("Min Hopes : "+minHopes);
                bw.newLine();
                bw.write("Max Hopes : "+maxHopes);
                bw.newLine();
                bw.write("Average Hopes : "+averageHopes);
                bw.newLine();
                bw.write("Standard Deviation of Hopes : " + standardDeviationHopes);
                bw.newLine();

                bw.newLine();
                bw.write("Min Latency : "+minLatency);
                bw.newLine();
                bw.write("Max Latency : "+maxLatency);
                bw.newLine();
                bw.write("Average Latency : "+averageLatency);
                bw.newLine();
                bw.write("Standard Deviation of Latency : " +standardDeviationLatency);
                bw.newLine();

                bw.newLine();
                bw.write("Min Messages per Node : "+minMessagesPerNode);
                bw.newLine();
                bw.write("Max Messages per Node : "+maxMessagesPerNode);
                bw.newLine();
                bw.write("Average Messages per Node : "+averageMessagesPerNode);
                bw.newLine();
                bw.write("Standard Deviation of Messages per Node : "+standardDeviationMessagesPerNode);
                bw.newLine();

                bw.newLine();
                bw.write("Min Node Degree : "+minNodeDegree);
                bw.newLine();
                bw.write("Max Node Degree : "+maxNodeDegree);
                bw.newLine();
                bw.write("Average Node Degree : "+averageNodeDegree);
                bw.newLine();
                bw.write("Standard Deviation of Node Degree : "+standardDeviationNodeDegree);
                bw.newLine();

                bw.write("##########################################");

                bw.close();

                System.out.println("File Write for Statistics is Done!");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{

            System.out.println("Not a Valid Query!");
            //Error Code Return
            System.out.println("Success Query List : ");
            System.out.println(successQueryList);
            System.out.println("###############################");

            System.out.println("Node Query List : ");
            System.out.println(nodeQueryList);
            System.out.println("###############################");

            System.out.println("Node HashTable");
            System.out.println(nodeTable);
            System.out.println("###############################");

        }

    }

    /**
     * This method is used to save the Success Queries on a File.
     */
    public void saveSuccessQueries(){
        try {
            System.out.println("File Write for Success Queries is Started!");
            File file = new File("./src/Files/SuccessQueries.txt");
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for(SuccessQuery sq : successQueryList){
                bw.newLine();
                bw.write(sq.getSourceIP());
                bw.newLine();
                bw.write(String.valueOf(sq.getSourcePort()));
                bw.newLine();
                bw.write(sq.getFileName());
                bw.newLine();
                bw.write(String.valueOf(sq.getLatency()));
                bw.newLine();
                bw.write(String.valueOf(sq.getHops()));
                bw.newLine();
                bw.write("##########################################");
            }
            bw.close();

            System.out.println("File Write for Success Queries is Done!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to save all the Query details on a File.
     */
    public void saveQueryDetails(){
        try {
            System.out.println("File Write for Node Queries is Started!");
            File file = new File("./src/Files/NodeQueries.txt");
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for(NodeQuery nq : nodeQueryList){
                bw.newLine();
                bw.write(nq.getSourceIP());
                bw.newLine();
                bw.write(String.valueOf(nq.getSourcePort()));
                bw.newLine();
                bw.write(nq.getIP());
                bw.newLine();
                bw.write(String.valueOf(nq.getPort()));
                bw.newLine();
                bw.write(nq.getFileName());
                bw.newLine();
                bw.write(String.valueOf(nq.getHops()));
                bw.newLine();
                bw.write(String.valueOf(nq.isFileAvaillable()));
                bw.newLine();
                bw.write(String.valueOf(nq.getNoOfForwardings()));
                bw.newLine();
                bw.write("##########################################");
            }
            bw.close();

            System.out.println("File Write for Node Queries is Done!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to store all the details about the Nodes in a File.
     */
    public void saveNodeDetails(){
        try {
            System.out.println("File Write for Node Details is Started!");
            File file = new File("./src/Files/NodeDetails.txt");
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            Set<String> keys = nodeTable.keySet();
            for(String key : keys){
                Node temp = nodeTable.get(key);
                bw.newLine();
                bw.write(temp.getIP());
                bw.newLine();
                bw.write(String.valueOf(temp.getPORT()));
                bw.newLine();
                bw.write(String.valueOf(temp.getMessagesReceived()));
                bw.newLine();
                bw.write(String.valueOf(temp.getMessagesForwarded()));
                bw.newLine();
                bw.write(String.valueOf(temp.getMessagesAnswered()));
                bw.newLine();
                bw.write(String.valueOf(temp.getNodeDegree()));
                bw.newLine();
                bw.write(String.valueOf(temp.getNodeCost()));
                bw.newLine();
                bw.write("##########################################");
            }

            bw.close();

            System.out.println("File Write for Node Details is Done!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMinHopes(){
        int minHopes = Integer.MAX_VALUE;

        for(SuccessQuery sq : successQueryList){
            int tempHops = sq.getHops();
            if(tempHops<minHopes){
                minHopes = tempHops;
            }
        }

        return minHopes;
    }

    public int getMaxHopes(){
        int maxHopes = Integer.MIN_VALUE;

        for(SuccessQuery sq : successQueryList){
            int tempHops = sq.getHops();
            if(tempHops>maxHopes){
                maxHopes = tempHops;
            }
        }
        return maxHopes;

    }

    public double getAverageHopes(){
        double averageHopes = 0.0;

        for(SuccessQuery sq : successQueryList){
            int tempHops = sq.getHops();
            averageHopes =+tempHops;
        }

        return averageHopes/successQueryList.size();
    }

    public double getStandardDeviationHopes(double mean){
        double averageHopes = 0.0;

        for(SuccessQuery sq : successQueryList){
            averageHopes =+Math.pow((sq.getHops() - mean),2);
        }

        return Math.sqrt(averageHopes/successQueryList.size());
    }

    public double getMinLatency(){
        double minLatency = Integer.MAX_VALUE;

        for(SuccessQuery sq : successQueryList){
            double tempLatency = sq.getLatency();
            if(tempLatency<minLatency){
                minLatency = tempLatency;
            }
        }

        return minLatency;
    }

    public double getMaxLatency(){
        double maxLatency = Integer.MIN_VALUE;

        for(SuccessQuery sq : successQueryList){
            double tempLatency = sq.getLatency();
            if(tempLatency>maxLatency){
                maxLatency = tempLatency;
            }
        }

        return maxLatency;
    }

    public double getAverageLatency(){
        double averageLatency = 0.0;

        for(SuccessQuery sq : successQueryList){
            double tempLatency = sq.getLatency();
            averageLatency =+tempLatency;
        }

        return averageLatency/successQueryList.size();
    }

    public double getStandardDeviationLatency(double mean){
        double averageLatency = 0.0;

        for(SuccessQuery sq : successQueryList){
            averageLatency =+Math.pow((sq.getLatency() - mean),2);
        }

        return Math.sqrt(averageLatency/successQueryList.size());
    }

    public int getMinMessagesPerNode(){
        int minMessagesPerNode = Integer.MAX_VALUE;

        Set<String> keys = nodeTable.keySet();
        for(String key : keys) {
            Node temp = nodeTable.get(key);
            int totalMessages = temp.getMessagesForwarded()+temp.getMessagesReceived();
            if(totalMessages<minMessagesPerNode){
                    minMessagesPerNode = totalMessages;
            }
        }

        return minMessagesPerNode;

    }

    public int getMaxMessagesPerNode(){
        int maxMessagesPerNode = Integer.MIN_VALUE;

        Set<String> keys = nodeTable.keySet();
        for(String key : keys) {
            Node temp = nodeTable.get(key);
            int totalMessages = temp.getMessagesForwarded()+temp.getMessagesReceived();
            if(totalMessages>maxMessagesPerNode){
                maxMessagesPerNode = totalMessages;
            }
        }

        return maxMessagesPerNode;
    }

    public double getAverageMessagesPerNode(){

        double averageMessagesPerNode = 0.0;

        Set<String> keys = nodeTable.keySet();
        for(String key : keys) {
            Node temp = nodeTable.get(key);
            averageMessagesPerNode += temp.getMessagesForwarded()+temp.getMessagesReceived();
        }

        return averageMessagesPerNode/nodeTable.size();

    }

    public double getStandardDeviationMessagesPerNode(double mean){
        double averageMessagesPerNode = 0.0;

        Set<String> keys = nodeTable.keySet();
        for(String key : keys){
            Node temp = nodeTable.get(key);
            averageMessagesPerNode =+Math.pow((temp.getMessagesForwarded()+temp.getMessagesReceived() - mean),2);
        }

        return Math.sqrt(averageMessagesPerNode/nodeTable.size());
    }

    public int getMinNodeDegree(){
        int minNodeDegree = Integer.MAX_VALUE;

        Set<String> keys = nodeTable.keySet();
        for(String key : keys) {
            Node temp = nodeTable.get(key);
            int tempNodeDegree = temp.getNodeDegree();
            if(tempNodeDegree<minNodeDegree){
                minNodeDegree = tempNodeDegree;
            }
        }

        return minNodeDegree;
    }

    public int getMaxNodeDegree(){
        int maxNodeDegree = Integer.MIN_VALUE;

        Set<String> keys = nodeTable.keySet();
        for(String key : keys) {
            Node temp = nodeTable.get(key);
            int tempNodeDegree = temp.getNodeDegree();
            if(tempNodeDegree>maxNodeDegree){
                maxNodeDegree = tempNodeDegree;
            }
        }

        return maxNodeDegree;
    }

    public double getAverageNodeDegree(){
        double averageNodeDegree = 0.0;

        Set<String> keys = nodeTable.keySet();
        for(String key : keys) {
            Node temp = nodeTable.get(key);
            averageNodeDegree += temp.getNodeDegree();
        }

        return averageNodeDegree/nodeTable.size();
    }

    public double getStandardDeviationNodeDegree(double mean){
        double averageNodeDegree = 0.0;

        Set<String> keys = nodeTable.keySet();
        for(String key : keys){
            averageNodeDegree =+Math.pow((nodeTable.get(key).getNodeDegree() - mean),2);
        }

        return Math.sqrt(averageNodeDegree/nodeTable.size());
    }





}
