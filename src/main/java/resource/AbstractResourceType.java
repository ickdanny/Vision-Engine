package resource;

public abstract class AbstractResourceType<T> {
    protected String[] getAcceptableFileTypes(){
        return new String[0];
    }
    protected String[] getAcceptableManifestPrefixes(){
        return new String[0];
    }
    Resource<T> makeResource(AbstractResourceOrigin origin){
        T data = makeData(origin);
        if(data == null){
            return null;
        }
        String id = origin.getId();
        return constructResource(id, origin, data);
    }
    Resource<T> makeResource(AbstractResourceOrigin origin, ResourceLoader loader){
        throwIfNotAcceptingLoader();
        T data = makeData(origin, loader);
        if(data == null){
            return null;
        }
        String id = origin.getId();
        return constructResource(id, origin, data);
    }
    T makeData(AbstractResourceOrigin origin){
        return origin.makeData(this);
    }
    T makeData(AbstractResourceOrigin origin, ResourceLoader loader){
        throwIfNotAcceptingLoader();
        return origin.makeData(this, loader);
    }

    protected T makeDataFromFile(FileOrigin origin){
        throw new UnsupportedOperationException(this.getClass().getName() + " unable to parse FileOrigin");
    }
    protected T makeDataFromFile(FileOrigin origin, ResourceLoader loader){
        throw new UnsupportedOperationException(this.getClass().getName() + " unable to parse FileOrigin accepting Loader");
    }
    protected T makeDataFromManifest(ManifestOrigin origin){
        throw new UnsupportedOperationException(this.getClass().getName() + "unable to parse ManifestOrigin");
    }
    protected T makeDataFromManifest(ManifestOrigin origin, ResourceLoader loader){
        throw new UnsupportedOperationException(this.getClass().getName() + " unable to parse ManifestOrigin accepting Loader");
    }
    protected Resource<T> constructResource(String id, AbstractResourceOrigin origin, T data){
        return new Resource<>(id, origin, data, this);
    }

    protected void writeDataToFile(FileOrigin origin, T data){
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support writing to FileOrigin");
    }

    protected void cleanUpData(T data){}

    private void throwIfNotAcceptingLoader(){
        if(!acceptsLoader()){
            throw new UnsupportedOperationException(this.getClass().getName() + " does not accept Loader");
        }
    }
    protected boolean acceptsLoader(){
        return false;
    }

    protected boolean requiresCleanUp(){
        return false;
    }
}