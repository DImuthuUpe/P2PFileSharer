/**
 * Created by Chamika on 3/3/2015.
 */
public class NodeQuery {

    private String sourceIP;
    private int sourcePort;
    private String IP;
    private int port;
    private String fileName;
    private int hops;
    private boolean fileAvaillable;
    private int noOfForwardings;

    public NodeQuery() {

    }

    NodeQuery(String sourceIP, int sourcePort, String IP, int port, String fileName, int hops, boolean fileAvaillable, int noOfForwardings){
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.IP = IP;
        this.port = port;
        this.fileName = fileName;
        this.hops = hops;
        this.fileAvaillable = fileAvaillable;
        this.noOfForwardings = noOfForwardings;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public String getFileName() {
        return fileName;
    }

    public int getHops() {
        return hops;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public boolean isFileAvaillable() {
        return fileAvaillable;
    }

    public int getNoOfForwardings() {
        return noOfForwardings;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public void setFileAvaillable(boolean fileAvaillable) {
        this.fileAvaillable = fileAvaillable;
    }

    public void setNoOfForwardings(int noOfForwardings) {
        this.noOfForwardings = noOfForwardings;
    }


    @Override
    public String toString() {
        return "Source IP : "+sourceIP+"\nSource PORT : "+sourcePort+"\nFile Name : "+fileName+"\nHOPES : "+hops+ "\nIP : "
                +IP+"\nPORT : "+port+"\nFile Availability : "+fileAvaillable+"\nNo of Forwarding : "+noOfForwardings;
    }
}
