package resource;

public class ManifestOrigin implements AbstractResourceOrigin {

    private final String[] metadata;

    public ManifestOrigin(String[] metadata) {
        this.metadata = metadata;
    }

    @Override
    public <T> T makeData(AbstractResourceType<T> type) {
        return type.makeDataFromManifest(this);
    }

    @Override
    public <T> T makeData(AbstractResourceType<T> type, ResourceLoader loader) {
        return type.makeDataFromManifest(this, loader);
    }

    public String[] getMetadata() {
        return metadata;
    }

    @Override
    public String getId() {
        return metadata[1];
    }
}