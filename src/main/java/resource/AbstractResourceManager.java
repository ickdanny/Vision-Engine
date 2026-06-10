package resource;

@SuppressWarnings("unused")
public interface AbstractResourceManager<T> {
    Resource<T> getResource(String id);
    void loadResource(Resource<T> resource);
    void reloadResource(String id);
    void unloadResource(String id);
    void removeResource(String id);
    void cleanUp();
}