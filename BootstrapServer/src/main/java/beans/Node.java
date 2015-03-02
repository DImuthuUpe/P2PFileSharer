package beans;

public class Node {
	
	private String ip;
	private int port;
	private String username;
	
	public Node(String ip, int port, String username){
		this.ip = ip;
		this.port = port;
		this.username = username;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
