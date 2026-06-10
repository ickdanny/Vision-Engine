package resource;

import java.io.File;

public class ResourceController {
    private final ResourceManagerMap resourceManagerMap;
    private final ResourceLoader resourceLoader;

    private ResourceController(AbstractResourceType<?>[] resourceTypeArray){
        resourceManagerMap = new ResourceManagerMap(resourceTypeArray);
        resourceLoader = new ResourceLoader(resourceTypeArray, resourceManagerMap);
    }

    public static ResourceController makeResourceController(AbstractResourceType<?>[] resourceTypeArray){
        return new ResourceController(resourceTypeArray);
    }

    public void loadFile(String fileName){
        resourceLoader.parseFile(fileName);
    }

    public void loadFile(File file){
        resourceLoader.parseFile(file);
    }

    public <T> AbstractResourceManager<T> getResourceManager(AbstractResourceType<T> type){
        return resourceManagerMap.get(type);
    }

    public void cleanUp(){
        resourceManagerMap.cleanUp();
    }
}

//Resource Types are defined outside the package and passed in
//Resource Origins will require a new class each, as well as changes to Resource Types
//may want to create a new thread for non-essential resource operations to take place on