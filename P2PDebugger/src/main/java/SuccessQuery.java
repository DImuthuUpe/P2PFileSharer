/**
 * Created by Chamika on 3/3/2015.
 */
public class SuccessQuery {

    private String sourceIP;
    private int sourcePort;
    private String fileName;
    private double latency;
    private int hops;

    public SuccessQuery() {
    }

    public SuccessQuery(String sourceIP, int sourcePort, String fileName, double latency, int hops) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.fileName = fileName;
        this.latency = latency;
        this.hops = hops;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    @Override
    public String toString() {
        return "Source IP "+sourceIP+"\nSource PORT : "+sourcePort+"\nFile Name : "
                +fileName+"\nLatency : "+latency+"\nHOPS : "+hops;
    }
}
