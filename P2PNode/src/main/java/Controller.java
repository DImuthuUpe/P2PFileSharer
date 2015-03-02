import beans.BSAck;
import beans.Node;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class Controller {
    public static void main(String args[]){
        BSAck ack = new BSAck();

        Node node1 = new Node("127.0.0.1",300,"User 1");
        Node node2 = new Node("127.0.0.2",400,"User 2");

        Node[] nodes = {node1,node2};

        ack.setCode(2);
        ack.setNodes(nodes);

        System.out.println(ack.convertToQuery());
    }
}
