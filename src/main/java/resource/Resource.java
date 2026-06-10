package resource;

public class Resource<T> {
    protected final String id;
    protected final AbstractResourceOrigin origin;
    protected final AbstractResourceType<T> type;
    protected boolean loaded;
    protected T data;

    public Resource(String id, AbstractResourceOrigin origin, T data, AbstractResourceType<T> type) {
        this.id = id;
        this.origin = origin;
        this.data = data;
        this.type = type;
        checkLoaded();
    }

    public String getId() {
        return id;
    }
    public AbstractResourceOrigin getOrigin() {
        return origin;
    }
    public boolean isLoaded() {
        return loaded;
    }
    public T getData() {
        return data;
    }
    public void setData(T data){
        this.data = data;
        checkLoaded();
    }

    public void unloadData(){
        if(loaded && data != null) {
            type.cleanUpData(data);
            data = null;
            loaded = false;
        }
    }
    public void reloadData(){
        data = type.makeData(origin);
        checkLoaded();
    }
    public void writeData(){
        origin.writeData(type, data);
    }

    void cleanUpData(){
        if(data != null){
            type.cleanUpData(data);
        }
    }

    private void checkLoaded(){
        loaded = data == null;
    }

    @Override
    public String toString() {
        return id;
    }
}