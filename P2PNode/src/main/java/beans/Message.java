package beans;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public interface Message {
    public String convertToQuery();
    public boolean initialize(String query);
}
