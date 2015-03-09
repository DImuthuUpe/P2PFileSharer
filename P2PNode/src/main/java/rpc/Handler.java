package rpc;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import beans.TransportAddress;
import p2p.Controller;
import udp.UDPClient;

public class Handler implements node.Iface {

	Controller controller;
	String myIp;
	int myPort;
	
	public Handler(Controller controller, String ip, int port){
		this.controller = controller;
		this.myIp = ip;
		this.myPort = port;
	}

	@Override
	public void search(String ip, int port, String query, int hops)
			throws TException {
		List<String> matchingWords = controller.getMatchingWords(query);
		System.out.println("Search " + ip +" " + port);
		if(matchingWords.size()>0){ //if there are matching words
			try {
			      TTransport transport;
			     
			      transport = new TSocket(ip, port);
			      transport.open();

			      TProtocol protocol = new  TBinaryProtocol(transport);
			      node.Client client = new node.Client(protocol);

			      client.searchResponse(myIp, myPort, query, hops-1, matchingWords);

			      transport.close();
			    } catch (TException x) {
			      x.printStackTrace();
			    } 

        }else if(hops>0){
        	System.out.println("Search Started" + hops);
        	Set<TransportAddress> ipTable = controller.getIpTable();
        	TransportAddress pred = new TransportAddress(ip,port);
        	Iterator<TransportAddress> it = ipTable.iterator();
            while (it.hasNext()){
                TransportAddress tp = it.next();
                if(!tp.equals(pred)){
                	try {
      			      TTransport transport;
      			      System.out.println("Sending to " + tp.getIp() +" " + tp.getPort());
      			      transport = new TSocket(tp.getIp(), tp.getPort());
      			      transport.open();

      			      TProtocol protocol = new  TBinaryProtocol(transport);
      			      node.Client client = new node.Client(protocol);

      			      client.search(ip, port, query, hops-1);

      			      transport.close();
      			    } catch (TException x) {
      			      x.printStackTrace();
      			    }  
                }
            }
        }
		
	}

	@Override
	public void searchResponse(String ip, int port, String query, int hops,
			List<String> files) throws TException {
		System.out.println("Received from" + ip + " " + port);
		controller.searchTable.remove(query);
		
	}

}
