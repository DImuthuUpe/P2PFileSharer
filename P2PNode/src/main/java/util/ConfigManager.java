package util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by dimuthuupeksha on 4/11/15.
 */
public class ConfigManager {
    public final static String BS_SERVER_IP = "bsaddress";
    public final static String BS_PORT = "bsport";
    public final static String DS_SERVER_IP = "dsaddress";
    public final static String DS_PORT = "dsport";

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
