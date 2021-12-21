package internalconfig;

import resource.AbstractResourceManager;
import resource.AbstractResourceOrigin;
import resource.FileOrigin;
import resource.Resource;

import java.io.File;
import java.util.Objects;
import java.util.Properties;

import static internalconfig.ResourceTypes.PROPERTIES;
import static internalconfig.MainConfig.*;

public class PropertiesInitializer {

    private final Resource<Properties> propertiesResource;
    private boolean hasChangedProperties;

    public PropertiesInitializer(AbstractResourceManager<Properties> propertiesManager){
        Resource<Properties> testIfNull = propertiesManager.getResource("properties");
        if(Objects.isNull(testIfNull)){
            AbstractResourceOrigin origin = new FileOrigin(new File(PROPERTIES_FILE));
            propertiesResource = new Resource<>("properties", origin, new Properties(), PROPERTIES);
        }
        else {
            propertiesResource = testIfNull;
        }
    }

    public Properties init(){
        Properties properties = propertiesResource.getData();
        for(InternalProperties property : InternalProperties.values()){
            initProperty(properties, property.getPropertyName(), property.getDefaultValue());
        }
        if(hasChangedProperties){
            propertiesResource.writeData();
        }
        return properties;
    }

    private void initProperty(Properties properties, String propertyName, String defaultValue){
        if(Objects.isNull(properties.getProperty(propertyName))){
            properties.put(propertyName, defaultValue);
            hasChangedProperties = true;
        }
    }
}
