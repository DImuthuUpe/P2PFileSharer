import beans.BSAck;
import beans.JoinAck;
import beans.Node;
import beans.TransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class Controller {

    private int offset = 5;
    private Set<TransportAddress> ipTable = new HashSet<TransportAddress>();
    private Node myNode;
    private Communicator communicator;
    private String[] fileList={"file1_"+offset,"file1_"+offset+" hoo","file2_"+offset,"file3_"+offset,};
    Map<String,Long> searchTable = new HashMap<String, Long>();


    public Set<TransportAddress> getIpTable() {
        return ipTable;
    }

    public List<String> getMatchingWords(String word){
        List<String> matchingWords = new ArrayList<String>();
        for(int i=0;i<fileList.length;i++){
            if(fileList[i].contains(word)){
                matchingWords.add(fileList[i]);
            }
        }
        return matchingWords;
    }


    public void addToIpTable(Node nodes[]){
        //System.out.println("Adding nodes to "+myNode.getIp()+" "+myNode.getPort()+" "+myNode.getUserName());
        for(int i=0;i<nodes.length;i++){
            TransportAddress tpAddress = new TransportAddress(nodes[i].getIp(),nodes[i].getPort());
            ipTable.add(tpAddress);
            System.out.println("Added node "+tpAddress.getIp()+ " "+tpAddress.getPort());
        }
    }

    public static void main(String args[]){
        new Controller();
    }

    public Controller(){
        System.out.println("File List");
        for (int i=0;i<fileList.length;i++){
            System.out.println(fileList[i]);
        }
        System.out.println();

        communicator = new Communicator();
        myNode = new Node("127.0.0.1",3000+offset,"User "+offset);
        try{
            Thread server = new Thread(new UDPServer(this, InetAddress.getByName("localhost"),myNode.getPort()));
            server.start();

            BSAck ack = communicator.register(myNode);
            addToIpTable(ack.getNodes());

            for(int i=0;i<ack.getNodes().length;i++){ // sending join requests to nodes
                JoinAck joinAck =communicator.join(myNode, ack.getNodes()[i]);
                //System.out.println(joinAck.convertToQuery());
            }

            TransportAddress self = new TransportAddress(myNode.getIp(),myNode.getPort());
            TransportAddress targets[] = new TransportAddress[ipTable.size()];
            targets = ipTable.toArray(targets);

            String searchQuery="file1_2";
            Long currentTime = System.currentTimeMillis();
            searchTable.put(searchQuery,currentTime);
            communicator.requestFile(self,self,targets,"\""+searchQuery+"\"",2);

        }catch (Exception ex){
            ex.printStackTrace();
            System.exit(0);
        }

    }
}
