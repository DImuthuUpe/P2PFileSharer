package rpc;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


public class Client {

	  public static void main(String [] args) {

	   
	    try {
	      TTransport transport;
	     
	      transport = new TSocket("localhost", 9090);
	      transport.open();

	      TProtocol protocol = new  TBinaryProtocol(transport);
	      node.Client client = new node.Client(protocol);

	      perform(client);

	      transport.close();
	    } catch (TException x) {
	      x.printStackTrace();
	    } 
	  }

	  private static void perform(node.Client client) throws TException
	  {
		  List<String> files = new ArrayList<String>();
		  files.add("Lord of The Ring");
		  files.add("Lord Pakeer");
		  //client.search("1.1.1.1", 9000, "Lord", 5);
		  client.searchResponse("1.1.1.1", 9000, "Lord", 5, files);
	    //System.out.println("24+31=" + product);
	  }
}
