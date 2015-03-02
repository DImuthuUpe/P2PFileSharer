package beans;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class UnRegisterAck implements Message{
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String convertToQuery() {
        String query = "UNROK";

        query =query+" "+ code;

        int queryLength = query.length()+5;

        query = String.format("%04d", queryLength) + " " + query;
        return query;
    }

    @Override
    public boolean initialize(String query) {
        String parts[] = query.split(" ");
        if(parts.length<3){
            return false;
        }else{
            try {
                code = Integer.parseInt(parts[2]);
            }catch (NumberFormatException e){
                return false;
            }

            return true;
        }
    }
}
