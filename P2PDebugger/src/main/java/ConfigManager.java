import java.io.IOException;
import java.util.Properties;

/**
 * Created by dimuthuupeksha on 4/11/15.
 */
public class ConfigManager {
    public final static String SERVER_IP = "serverip";
    public final static String PORT = "port";

    public static String getProperty(String key){
        Properties prop = new Properties();
        try {
            prop.load(ConfigManager.class.getClassLoader().getResourceAsStream("config.properties"));
            return prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
