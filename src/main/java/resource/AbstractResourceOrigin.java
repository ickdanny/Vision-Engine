package resource;

public interface AbstractResourceOrigin {
    <T> T makeData(AbstractResourceType<T> type);
    <T> T makeData(AbstractResourceType<T> type, ResourceLoader loader);
    default <T> void writeData(AbstractResourceType<T> type, T data){
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support writing.");
    }
    String getId();
}