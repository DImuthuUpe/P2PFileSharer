package beans;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class JoinAck implements Message{
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String convertToQuery() {
        return null;
    }

    @Override
    public boolean initialize(String query) {
        return false;
    }
}
