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
        messagesReceived = 0;
        messagesForwarded = 0;
        nodeDegree = 0;
        nodeCost = 0;
    }

    public Node(String IP, int PORT) {
        this.IP = IP;
        this.PORT = PORT;
        nodeDegree = 0;
        messagesReceived = 0;
        messagesForwarded = 0;
        nodeDegree = 0;
        nodeCost = 0;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (!this.IP.equals(other.IP)) {
            return false;
        }
        if (this.PORT != other.PORT) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return (new String(this.IP + ":" + this.PORT)).hashCode();
    }

    @Override
    public String toString() {
//        private String IP;
//        private int PORT;
//        private int messagesReceived;
//        private int messagesForwarded;
//        private int messagesAnswered;
//        private int nodeDegree;
//        private double nodeCost;
        return "IP : "+IP+" PORT : "+PORT+" Messages Received : "+messagesReceived+" Messages Forwarded : "+messagesForwarded
                +" Messages Answered : "+messagesAnswered+" Node Degree : "+nodeDegree+" Node Cost : "+nodeCost;
    }
}
