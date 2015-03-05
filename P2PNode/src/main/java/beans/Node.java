package beans;

public class Node {
    private String ip;
    private int port;
    private String userName;


    public Node(){

    }

    public Node(String ip,int port,String userName){
        this.ip = ip;
        this.port = port;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
