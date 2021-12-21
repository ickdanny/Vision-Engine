package internalconfig;

import java.util.Objects;
import java.util.Properties;

public class PropertiesUtil {
    private PropertiesUtil() {
    }

    public static boolean getBooleanProperty(Properties properties, String propertyName) {
        String value = properties.getProperty(propertyName);
        if (Objects.isNull(value)) {
            throw makeCannotFindPropertyException(propertyName);
        }
        if(value.equalsIgnoreCase("true")){
            return true;
        }
        else if(value.equalsIgnoreCase("false")){
            return false;
        }
        throw new InternalPropertyException("Property " + propertyName + " " + value + " cannot be parsed as boolean!");
    }

    public static boolean toggleBooleanProperty(Properties properties, String propertyName){
        boolean newValue = !getBooleanProperty(properties, propertyName);
        properties.put(propertyName, "" + newValue);
        return newValue;
    }

    public static void setBooleanProperty(Properties properties, String propertyName, boolean newValue){
        properties.put(propertyName, "" + newValue);
    }

    private static InternalPropertyException makeCannotFindPropertyException(String propertyName){
        return new InternalPropertyException("Cannot find property with name: " + propertyName);
    }
}
