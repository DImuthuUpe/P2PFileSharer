import beans.*;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class Communicator {

    private String bsServer= "127.0.0.1";
    private int bsPort = 5000;

    public BSAck register(Node self){
        return null;
    }

    public JoinAck join(Node node){
        return null;
    }

    public UnRegisterAck unRegister(Node self){
        return null;
    }

    public LeaveAck leave(Node self, Node remote){
        return null;
    }

    public BSAck requestNewNodes(){
        return null;
    }

    public RequestFileAck requestFile(Node self, Node target, String fileName, int hops){
        return null;
    }
}
