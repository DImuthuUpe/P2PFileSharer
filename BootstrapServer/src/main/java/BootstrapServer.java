import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import beans.Node;
import beans.TransportAddress;


public class BootstrapServer {
	
	private Hashtable<TransportAddress, Node> nodeTable =  new Hashtable<TransportAddress, Node>();
	private Random random = new Random();
	
	
	public String registerNode(String ip, int port, String uname){
		
		TransportAddress key = new TransportAddress(ip, port);
		Node value = new Node(ip,port,uname);
		if(nodeTable.containsKey(key)){
			if(nodeTable.get(key).getUsername().equals(uname)){
				return "0015 REGOK 9998";
			}
			else{
				return "0015 REGOK 9997";
			}
			
		}
		else{
			int currentNodes = nodeTable.size();
			String returnString;
			if(currentNodes >= 100){
				return "0015 REGOK 9996";
			}
			if(currentNodes == 0){
				returnString = "0012 REGOK 0";
			}else if(currentNodes == 1){
				Enumeration<Node> e = nodeTable.elements();
				Node pointingNode1 = e.nextElement();
				returnString = " REGOK 1 " + pointingNode1.getIp() + " " + pointingNode1.getPort();
				String len = String.format("%04d", returnString.length() + 4);
				returnString = len + returnString;
			}else{
				int num1 = random.nextInt(currentNodes);
				int num2 = random.nextInt(currentNodes);
				while(num1==num2){
					num2 = random.nextInt(currentNodes);
				}
				returnString = " REGOK 2";
				Node pointingNode1;
				Enumeration<Node> e = nodeTable.elements();
				for(int i=0;i<=Math.max(num1, num2);i++){
					pointingNode1 = e.nextElement();
					if(i==num1 || i==num2){
						returnString = returnString + " " + pointingNode1.getIp() + " " + pointingNode1.getPort();
					}
				}
				String len = String.format("%04d", returnString.length() + 4);
				returnString = len + returnString;
			}
			nodeTable.put(key, value);
			return returnString;
		}
	}
	
	public static void main(String[] args){
//		TransportAddress key = new TransportAddress("0.0.0.0", 5000);
//		Node value = new Node("0.0.0.0",5000,"cdwijayarathna");
//		BootstrapServer s = new BootstrapServer();
//		System.out.println(s.registerNode("127.0.0.1", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.1", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.1", 1000, "amila"));
//		System.out.println(s.registerNode("127.0.0.2", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.3", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.4", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.5", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.6", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.7", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.8", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.9", 1000, "chamila"));
//		System.out.println(s.registerNode("127.0.0.10", 1000, "chamila"));
		
	}

}
