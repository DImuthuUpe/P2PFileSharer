package beans;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class RequestFileAck implements Message{
    private int fileCount;
    private String ip;
    private int port;
    private int hops;
    private String[] files;

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

    @Override
    public String convertToQuery() {
        return null;
    }

    @Override
    public boolean initialize(String query) {
        return false;
    }
}
