package resource;

import util.file.FileUtil;

import java.io.File;

public class FileOrigin implements AbstractResourceOrigin {
    private final File file;

    public FileOrigin(File file) {
        this.file = file;
    }

    @Override
    public <T> T makeData(AbstractResourceType<T> type) {
        return type.makeDataFromFile(this);
    }
    @Override
    public <T> T makeData(AbstractResourceType<T> type, ResourceLoader loader){
        return type.makeDataFromFile(this, loader);
    }

    @Override
    public <T> void writeData(AbstractResourceType<T> type, T data) {
        type.writeDataToFile(this, data);
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getId(){
        return FileUtil.getFileName(file);
    }
}