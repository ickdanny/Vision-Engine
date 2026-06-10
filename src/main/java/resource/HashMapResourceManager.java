package resource;

import java.util.HashMap;
import java.util.Map;

class HashMapResourceManager<T> implements AbstractResourceManager<T>{
    private final Map<String, Resource<T>> resourceMap;
    public HashMapResourceManager(){
        resourceMap = new HashMap<>();
    }

    @Override
    public Resource<T> getResource(String id) {
        return resourceMap.get(id);
    }

    @Override
    public void loadResource(Resource<T> resource) {
        resourceMap.put(resource.getId(), resource);
    }

    @Override
    public void reloadResource(String id) {
        if(!resourceMap.containsKey(id)){
            return;
        }
        resourceMap.get(id).reloadData();
    }

    @Override
    public void unloadResource(String id) {
        resourceMap.get(id).unloadData();
    }

    @Override
    public void removeResource(String id) {
        resourceMap.remove(id);
    }

    @Override
    public void cleanUp() {
        for(Resource<T> resource : resourceMap.values()){
            resource.cleanUpData();
        }
    }
}
