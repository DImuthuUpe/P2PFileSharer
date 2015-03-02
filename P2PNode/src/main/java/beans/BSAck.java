package beans;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class BSAck implements Message{
    private int code;
    private Node[] nodes;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    @Override
    public String convertToQuery() {
        String query = "REGOK";
        if(code<=9999 && code>=9996){
            //error code
        }else{
            query =query+" "+code;
            if(nodes!=null){
                for(int i=0;i<nodes.length;i++){
                    query+= " "+nodes[i].getIp()+" "+nodes[i].getPort();
                }
            }
        }

        String queryLength = (query.length()+5)+"";
        for(int i=queryLength.length();i<4;i++){
            queryLength = "0"+queryLength;
        }

        query = queryLength+ " "+query;
        return query;
    }

    @Override
    public boolean initialize(String query) {
        String parts[] = query.split(" ");
        if(parts.length<3){
            //return false;
        }else{
            int code = Integer.parseInt(parts[2]);
            //if((parts.length-3))
        }

        return false;
    }
}
