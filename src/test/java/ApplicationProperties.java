import org.apache.logging.log4j.core.util.Loader;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class ApplicationProperties {

    private static Properties instance = null;
    private static final String APPLICATION_PREFIX = "application";
    private static final String APPLICATION_SUFLIX = "properties";

    public static synchronized Properties getInstance(){
        if (instance == null){
            instance = loadPropertiesFile();
        }
        return instance;
    }

    private ApplicationProperties(){

    }

    private static Properties loadPropertiesFile(){
        String enviroment = Optional.ofNullable(System.getenv("env"))
                .orElse("dev");

        String filename = String.format("%s-%s.%s",APPLICATION_PREFIX,enviroment,APPLICATION_SUFLIX);

        Properties prop = new Properties();
        try {
            prop.load(Loader.getClassLoader().getResourceAsStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prop;
    }
}
