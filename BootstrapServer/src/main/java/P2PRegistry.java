import java.net.*;

/**
 * Created by dimuthuupeksha & cdwijayarathna on 3/2/15.
 */
public class P2PRegistry {
	
	private DatagramSocket serverSocket;
	private byte[] receiveData;
	private byte[] sendData;
	
    public P2PRegistry(){
        try{
            serverSocket = openSocket(Integer.parseInt(ConfigManager.getProperty(ConfigManager.PORT)));
            receiveData = new byte[1024];
            sendData = new byte[1024];
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

	public void accept() {
		
		BootstrapServer server = new BootstrapServer();
		String inputData;
		String[] inputInfo;
		String output = null;
		
		while (true) {
			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				serverSocket.receive(receivePacket);
				byte data[] = receivePacket.getData();
				inputData = new String(data, 0, receivePacket.getLength());
                //System.out.println(inputData);
                inputInfo = inputData.split(" ");
				try {
					if (inputInfo[1].equals("REG")) {
						output = server.registerNode(inputInfo[2],
								Integer.parseInt(inputInfo[3].trim()),
								inputInfo[4]);
					} else {
						output = "0015 REGOK 9999";
					}
				} catch (Exception e) {
					output = "0015 REGOK 9999";
				}
				
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();

				sendData = output.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			} catch (Exception e) {

			}
		}
	}
    
    public DatagramSocket openSocket(int portNo){
        try{
            DatagramSocket serverSocket = new DatagramSocket(portNo,InetAddress.getByName(ConfigManager.getProperty(ConfigManager.SERVER_IP)));
            System.out.println("Socket created .......");
            return serverSocket;
        }catch (UnknownHostException ex){
            ex.printStackTrace();
            System.out.println("Could Not fetch the ip to create the socket");
            System.exit(0);
        }catch (SocketException ex){
            ex.printStackTrace();
            System.out.println("Unable to open socket on port "+portNo);
            System.exit(0);
        }
        return null;
    }
    
    public static void main(String as[]){
    	P2PRegistry server = new P2PRegistry();
    	server.accept();
    	
    }

}
