import beans.BSAck;
import beans.JoinAck;
import beans.Node;
import beans.TransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class Controller {

    private Set<TransportAddress> ipTable = new HashSet<TransportAddress>();
    private Node myNode;
    private Communicator communicator;

    public void addToIpTable(Node nodes[]){
        System.out.println("Adding nodes to "+myNode.getIp()+" "+myNode.getPort()+" "+myNode.getUserName());
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
        communicator = new Communicator();
        myNode = new Node("127.0.0.1",3009,"User 2");
        try{
            Thread server = new Thread(new UDPServer(this, InetAddress.getByName("localhost"),myNode.getPort()));
            server.start();

            BSAck ack = communicator.register(myNode);
            addToIpTable(ack.getNodes());

            for(int i=0;i<ack.getNodes().length;i++){ // sending join requests to nodes
                JoinAck joinAck =communicator.join(myNode, ack.getNodes()[i]);
                System.out.println(joinAck.convertToQuery());
            }

        }catch (Exception ex){
            ex.printStackTrace();
            System.exit(0);
        }


    }
}
