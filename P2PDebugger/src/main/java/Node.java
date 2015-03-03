/**
 * Created by Chamika on 3/4/2015.
 */
public class Node {

    private String IP;
    private int PORT;
    private int messagesReceived;
    private int messagesForwarded;
    private int messagesAnswered;
    private int nodeDegree;
    private double nodeCost;


    public Node() {
        nodeDegree = 0;
    }

    public Node(String IP, int PORT, int messagesReceived, int messagesForwarded, int messagesAnswered, int nodeDegree, double nodeCost) {
        this.IP = IP;
        this.PORT = PORT;
        this.messagesReceived = messagesReceived;
        this.messagesForwarded = messagesForwarded;
        this.messagesAnswered = messagesAnswered;
        this.nodeDegree = nodeDegree;
        this.nodeCost = nodeCost;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public int getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(int messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    public int getMessagesForwarded() {
        return messagesForwarded;
    }

    public void setMessagesForwarded(int messagesForwarded) {
        this.messagesForwarded = messagesForwarded;
    }

    public int getMessagesAnswered() {
        return messagesAnswered;
    }

    public void setMessagesAnswered(int messagesAnswered) {
        this.messagesAnswered = messagesAnswered;
    }

    public int getNodeDegree() {
        return nodeDegree;
    }

    public void setNodeDegree(int nodeDegree) {
        this.nodeDegree = nodeDegree;
    }

    public double getNodeCost() {
        return nodeCost;
    }

    public void setNodeCost(double nodeCost) {
        this.nodeCost = nodeCost;
    }

    public void incrementDegree(){
        nodeDegree += 1;
    }

    public void decrementDegree(){
        if(nodeDegree > 0){
            nodeDegree -= 1;
        }
    }
}
