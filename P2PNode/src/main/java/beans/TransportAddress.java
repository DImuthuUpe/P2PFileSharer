package beans;

public class TransportAddress {
    private String ip;
    private int port;

    public TransportAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TransportAddress other = (TransportAddress) obj;
        if (!this.ip.equals(other.ip)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return (new String(this.ip + ":" + this.port)).hashCode();
    }
    
}
