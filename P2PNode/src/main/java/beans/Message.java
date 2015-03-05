package beans;

public interface Message {
    public String convertToQuery();
    public boolean initialize(String query);
}
