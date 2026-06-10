package resource;

import util.file.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
    private final FileResourceTypeChooser fileResourceTypeChooser;
    private final ManifestResourceTypeChooser manifestResourceTypeChooser;
    private final ResourceManagerMap resourceManagerMap;

    public ResourceLoader(AbstractResourceType<?>[] resourceTypeArray, ResourceManagerMap resourceManagerMap){
        fileResourceTypeChooser = new FileResourceTypeChooser(resourceTypeArray);
        manifestResourceTypeChooser = new ManifestResourceTypeChooser(resourceTypeArray);
        this.resourceManagerMap = resourceManagerMap;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Resource<?> parseFile(String fileName){
        File file = new File(fileName);
        return parseFile(file);
    }
    public Resource<?> parseFile(File file){
        String fileExtension = FileUtil.getFileExtension(file);
        if(fileResourceTypeChooser.hasMatchingFileExtension(fileExtension)) {
            AbstractResourceType<?> type = fileResourceTypeChooser.getResourceTypeFromFileExtension(fileExtension);
            return addResourceFromFile(type, file);
        }
        return null;
    }
    private <T> Resource<T> addResourceFromFile(AbstractResourceType<T> type, File file){
        Resource<T> resource;
        if(type.acceptsLoader()) {
            resource = type.makeResource(makeFileOrigin(file), this);
        }else{
            resource = type.makeResource(makeFileOrigin(file));
        }
        if(resource == null){
            return null;
        }
        resourceManagerMap.get(type).loadResource(resource);
        return resource;
    }

    private FileOrigin makeFileOrigin(File file){
        return new FileOrigin(file);
    }

    public Resource<?> parseManifestLine(String[] metadata){
        String prefix = metadata[0];
        if(manifestResourceTypeChooser.hasMatchingManifestPrefix(prefix)){
            AbstractResourceType<?> type = manifestResourceTypeChooser.getResourceTypeFromManifestPrefix(prefix);
            return addResourceFromManifest(type, metadata);
        }
        return null;
    }

    private <T> Resource<T> addResourceFromManifest(AbstractResourceType<T> type, String[] metadata){
        Resource<T> resource;
        if(type.acceptsLoader()) {
            resource = type.makeResource(makeManifestOrigin(metadata), this);
        }else{
            resource = type.makeResource(makeManifestOrigin(metadata));
        }
        if(resource == null){
            return null;
        }
        resourceManagerMap.get(type).loadResource(resource);
        return resource;
    }

    private ManifestOrigin makeManifestOrigin(String[] metadata){
        return new ManifestOrigin(metadata);
    }

    private static class FileResourceTypeChooser {
        private final Map<String, AbstractResourceType<?>> fileExtensionToResourceTypeMap;

        public FileResourceTypeChooser(AbstractResourceType<?>[] resourceTypeArray){
            fileExtensionToResourceTypeMap = makeFileExtensionToResourceTypeMap(resourceTypeArray);
        }

        private Map<String, AbstractResourceType<?>> makeFileExtensionToResourceTypeMap(
                AbstractResourceType<?>[] resourceTypeArray
        ){
            Map<String, AbstractResourceType<?>> fileExtensionToResourceTypeMap = new HashMap<>();
            for(AbstractResourceType<?> type: resourceTypeArray){
                for(String extension : type.getAcceptableFileTypes()){
                    if(fileExtensionToResourceTypeMap.containsKey(extension)){
                        throw new RuntimeException("Dual resource types claiming " + extension + " extension: " +
                                fileExtensionToResourceTypeMap.get(extension).getClass().getName() + " and " +
                                type.getClass().getName());
                    }
                    fileExtensionToResourceTypeMap.put(extension, type);
                }
            }
            return fileExtensionToResourceTypeMap;
        }

        public AbstractResourceType<?> getResourceTypeFromFileExtension(String extension){
            return fileExtensionToResourceTypeMap.get(extension);
        }
        public boolean hasMatchingFileExtension(String extension){
            return fileExtensionToResourceTypeMap.containsKey(extension);
        }
    }

    private static class ManifestResourceTypeChooser {
        private final Map<String, AbstractResourceType<?>> manifestPrefixToResourceTypeMap;

        public ManifestResourceTypeChooser(AbstractResourceType<?>[] resourceTypeArray){
            manifestPrefixToResourceTypeMap = makeManifestPrefixToResourceTypeMap(resourceTypeArray);
        }

        private Map<String, AbstractResourceType<?>> makeManifestPrefixToResourceTypeMap(
                AbstractResourceType<?>[] resourceTypeArray
        ){
            Map<String, AbstractResourceType<?>> manifestPrefixToResourceTypeMap = new HashMap<>();
            for(AbstractResourceType<?> type: resourceTypeArray){
                for(String prefix : type.getAcceptableManifestPrefixes()){
                    if(manifestPrefixToResourceTypeMap.containsKey(prefix)){
                        throw new RuntimeException("Dual resource types claiming " + prefix + " prefix: " +
                                manifestPrefixToResourceTypeMap.get(prefix).getClass().getName() + " and " +
                                type.getClass().getName());
                    }
                    manifestPrefixToResourceTypeMap.put(prefix, type);
                }
            }
            return manifestPrefixToResourceTypeMap;
        }

        public AbstractResourceType<?> getResourceTypeFromManifestPrefix(String prefix){
            return manifestPrefixToResourceTypeMap.get(prefix);
        }
        public boolean hasMatchingManifestPrefix(String prefix){
            return manifestPrefixToResourceTypeMap.containsKey(prefix);
        }
    }
}