package p2p;

import beans.BSAck;
import beans.JoinAck;
import beans.Node;
import beans.TransportAddress;
import udp.UDPClient;
import udp.UDPServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class Controller {

    private int offset = 6;
    private Set<TransportAddress> ipTable = new HashSet<TransportAddress>();
    private Node myNode;
    private UDPClient client;
    private String[] fileList;
    public Map<String,Long> searchTable = new HashMap<String, Long>();
    public static final int MAX_HOPS = 2;

    public void initiateFileList(){
        InputStream is= getClass().getResourceAsStream("/FileNames.txt");
        Scanner sc = new Scanner(is);
        List<String> files = new ArrayList<String>();
        while(sc.hasNext()){
            files.add(sc.nextLine());
        }
        Random random = new Random();
        int fileCount = 5-random.nextInt(3);

        fileList = new String[fileCount];
        for(int i=0;i<fileCount;i++){
            int index = random.nextInt(files.size());
            fileList[i] = files.get(index);
            files.remove(index);
        }
    }

    public Set<TransportAddress> getIpTable() {
        return ipTable;
    }

    public List<String> getMatchingWords(String word){
        String splitRegex = "[\\s:_]";

        String[] splitedStr = word.split(splitRegex);
        ArrayList<String> splitWords;
        ArrayList<String> matchedFiles = new ArrayList<String>();

        for (int j = 0; j < fileList.length; j++) {
            splitWords = new ArrayList<String>(Arrays.asList(fileList[j].split(splitRegex)));

            boolean matched = true;

            for (int i=0; i<splitedStr.length; i++){
                int index = splitWords.indexOf(splitedStr[i]);
                if (index == -1){
                    matched = false;
                    break;
                }
                else{
                    splitWords.remove(index);
                }

            }
            if(matched){
                matchedFiles.add(fileList[j]);
            }
        }

        return matchedFiles;
    }


    public void addToIpTable(Node nodes[]){
        //System.out.println("Adding nodes to "+myNode.getIp()+" "+myNode.getPort()+" "+myNode.getUserName());
        for(int i=0;i<nodes.length;i++){
            TransportAddress tpAddress = new TransportAddress(nodes[i].getIp(),nodes[i].getPort());
            ipTable.add(tpAddress);
            System.out.println("Added node "+tpAddress.getIp()+ " "+tpAddress.getPort());
        }
    }

    public void removeFromIpTable(Node node){
        TransportAddress tpAddress = new TransportAddress(node.getIp(),node.getPort());
        ipTable.remove(tpAddress);
        System.out.println("Removed Node "+tpAddress.getIp()+ " "+tpAddress.getPort());
    }

    public void searchFile(String searchQuery) throws IOException {
        TransportAddress self = new TransportAddress(myNode.getIp(),myNode.getPort());
        TransportAddress targets[] = new TransportAddress[ipTable.size()];
        targets = ipTable.toArray(targets);
        Long currentTime = System.currentTimeMillis();
        searchTable.put(searchQuery,currentTime);
        client.requestFile(self,self,targets,searchQuery,MAX_HOPS);
    }

    public Controller(String ip,int port,String username){
        initiateFileList();
        System.out.println("File List .........");
        for (int i=0;i<fileList.length;i++){
            System.out.println(fileList[i]);
        }
        System.out.println("..................\n");

        client = new UDPClient();
        myNode = new Node(ip,port,username);
        try{
            Thread server = new Thread(new UDPServer(this, myNode.getIp(),myNode.getPort(),username));
            server.start();

            BSAck ack = client.register(myNode);
            addToIpTable(ack.getNodes());

            for(int i=0;i<ack.getNodes().length;i++){ // sending join requests to nodes
                JoinAck joinAck =client.join(myNode, ack.getNodes()[i]);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            System.exit(0);
        }

    }

    public static void main(String args[]){
        int port=0;
        String ip=null;
        String user=null;
        if(args!=null){
            for(int i=0;i<args.length;i++){
                if(args[i].equals("-p")){
                    port = Integer.parseInt(args[i+1]);
                    i++;
                }else if(args[i].equals("-u")){
                    user = args[i+1];
                    i++;
                }else if(args[i].equals("-h")){
                    ip = args[i+1];
                    i++;
                }
            }
        }

        if(port==0||ip==null||user==null){
            System.out.println("Can not initiate node. Input parameters are invalid");
            System.exit(0);
        }else{
            new Controller(ip,port,user);
        }
    }
}
