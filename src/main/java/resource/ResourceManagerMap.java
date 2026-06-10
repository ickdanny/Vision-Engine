package resource;

import java.util.HashMap;
import java.util.Map;

class ResourceManagerMap {
    //we can probably do better than this - but getting the map is not an important operation
    private final Map<AbstractResourceType<?>, AbstractResourceManager<?>> innerResourceManagerMap;

    public ResourceManagerMap() {
        innerResourceManagerMap = new HashMap<>();
    }
    public ResourceManagerMap(AbstractResourceType<?>[] resourceTypeArray) {
        innerResourceManagerMap = new HashMap<>();
        for (AbstractResourceType<?> type : resourceTypeArray) {
            addNewType(type);
        }
    }

    //why did i separate these int two methods?
    public <T> void addNewType(AbstractResourceType<T> type) {
        put(type, new HashMapResourceManager<>());
    }
    private <T> void put(AbstractResourceType<T> type, AbstractResourceManager<T> manager) {
        innerResourceManagerMap.put(type, manager);
    }
    @SuppressWarnings("unchecked")
    public <T> AbstractResourceManager<T> get(AbstractResourceType<T> type) {
        return (AbstractResourceManager<T>) innerResourceManagerMap.get(type);
    }
    public void remove(AbstractResourceType<?> type) {
        innerResourceManagerMap.remove(type);
    }

    void cleanUp(){
        for(Map.Entry<AbstractResourceType<?>, AbstractResourceManager<?>> entry : innerResourceManagerMap.entrySet()){
            if(entry.getKey().requiresCleanUp()){
                entry.getValue().cleanUp();
            }
        }
    }
}
