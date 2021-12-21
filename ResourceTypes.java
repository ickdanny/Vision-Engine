package internalconfig;

import internalconfig.game.systems.dialoguesystems.CommandDataTuple;
import internalconfig.game.systems.dialoguesystems.Dialogue;
import internalconfig.game.systems.dialoguesystems.DialogueCommands;
import resource.AbstractResourceOrigin;
import resource.AbstractResourceType;
import resource.FileOrigin;
import resource.ManifestOrigin;
import resource.ParentResource;
import resource.Resource;
import resource.ResourceLoader;
import sound.midi.MidiMetadata;
import sound.midi.MidiSequence;
import util.file.AbstractSingleFileParser;
import util.file.ConfigurableDirectoryParser;
import util.file.FileUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

@SuppressWarnings("Convert2Lambda")
public final class ResourceTypes {
    private ResourceTypes() {
    }

    private static final List<AbstractResourceType<?>> resourceTypesList = new ArrayList<>();

    public static final ResourceTypeTemplate<List<Resource<?>>> DIRECTORY = new ResourceTypeTemplate<List<Resource<?>>>(
            new String[]{FileUtil.DIRECTORY_EXTENSION},
            new String[]{"directory"}
    ) {
        @Override
        public List<Resource<?>> makeDataFromFile(FileOrigin origin, ResourceLoader loader) {
            return makeData(origin.getFile(), loader);
        }

        @Override
        protected List<Resource<?>> makeDataFromManifest(ManifestOrigin origin, ResourceLoader loader) {
            return makeData(new File(origin.getMetadata()[2]), loader);
        }

        private List<Resource<?>> makeData(File file, ResourceLoader loader){
            List<Resource<?>> childList = new ArrayList<>();
            ConfigurableDirectoryParser configurableDirectoryParser = new ConfigurableDirectoryParser(
                    new AbstractSingleFileParser() {
                        @Override
                        public void parseFile(File file) {
                            Resource<?> child = loader.parseFile(file);
                            if (child != null) {
                                childList.add(child);
                            }
                        }
                    });
            configurableDirectoryParser.parseDirectory(file);
            return childList;
        }

        @Override
        public Resource<List<Resource<?>>> constructResource(String id, AbstractResourceOrigin origin, List<Resource<?>> childList) {
            return new ParentResource(id, origin, childList, this);
        }

        @Override
        public boolean acceptsLoader() {
            return true;
        }
    };

    public final static ResourceTypeTemplate<List<Resource<?>>> MANIFEST = new ResourceTypeTemplate<List<Resource<?>>>(
            new String[]{"resourcemanifest"},
            new String[]{"manifest"}
    ) {
        @Override
        public List<Resource<?>> makeDataFromFile(FileOrigin origin, ResourceLoader loader) {
            List<Resource<?>> childList = new ArrayList<>();
            AbstractSingleFileParser manifestParser = new AbstractSingleFileParser() {
                @Override
                public void parseFile(File file) {
                    Scanner scanner = FileUtil.makeScanner(file);
                    while (scanner.hasNextLine()) {
                        Resource<?> child = loader.parseManifestLine(parseLine(scanner.nextLine()));
                        if (child != null) {
                            childList.add(child);
                        }
                    }
                    scanner.close();
                }
            };
            manifestParser.parseFile(origin.getFile());
            return childList;
        }

        private String[] parseLine(String line) {
            return line.split(" ");
        }

        @Override
        public Resource<List<Resource<?>>> constructResource(String id, AbstractResourceOrigin origin, List<Resource<?>> childList) {
            return new ParentResource(id, origin, childList, this);
        }

        @Override
        public boolean acceptsLoader() {
            return true;
        }
    };

    public static final ResourceTypeTemplate<BufferedImage> IMAGE = new ResourceTypeTemplate<BufferedImage>(
            new String[]{"png"},
            new String[]{"image"}
    ) {
        @Override
        public BufferedImage makeDataFromFile(FileOrigin origin) {
            return makeData(origin.getFile());
        }

        @Override
        protected BufferedImage makeDataFromManifest(ManifestOrigin origin) {
            return makeData(new File(origin.getMetadata()[2]));
        }

        private BufferedImage makeData(File file){
            return FileUtil.parseImage(file);
        }
    };

    public static final ResourceTypeTemplate<MidiSequence> MIDI_SEQUENCE = new ResourceTypeTemplate<MidiSequence>(
            new String[]{"mid"},
            new String[]{"midi"}
    ) {
        @Override
        public MidiSequence makeDataFromFile(FileOrigin origin) {
            return new MidiSequence(FileUtil.makeInputStream(origin.getFile()), MidiMetadata.DEFAULT_LOOPING);
        }

        @Override
        protected MidiSequence makeDataFromManifest(ManifestOrigin origin) {
            File file = new File(origin.getMetadata()[2]);
            MidiMetadata metadata = makeMetadata(origin);
            return new MidiSequence(FileUtil.makeInputStream(file), metadata);
        }

        private MidiMetadata makeMetadata(ManifestOrigin origin){
            String[] manifestMetadata = origin.getMetadata();
            switch(manifestMetadata.length){
                case 4:
                    return new MidiMetadata(Boolean.parseBoolean(manifestMetadata[3]));
                case 5:
                    return new MidiMetadata(
                            Boolean.parseBoolean(manifestMetadata[3]),
                            Long.parseLong(manifestMetadata[4]));
                case 6:
                    return new MidiMetadata(
                            Boolean.parseBoolean(manifestMetadata[3]),
                            Long.parseLong(manifestMetadata[4]),
                            Long.parseLong(manifestMetadata[5]));
                default:
                    throw new RuntimeException("wrong number of arguments to construct MidiSequence from manifest!");
            }
        }

        @Override
        protected void cleanUpData(MidiSequence data) {
            data.close();
        }

        @Override
        protected boolean requiresCleanUp() {
            return true;
        }
    };

    public static final ResourceTypeTemplate<Dialogue> DIALOGUE = new ResourceTypeTemplate<Dialogue>(
            new String[]{"dialogue"},
            new String[]{"dialogue"}
    ){
        @Override
        public Dialogue makeDataFromFile(FileOrigin origin) {
            return makeData(origin.getFile());
        }

        @Override
        protected Dialogue makeDataFromManifest(ManifestOrigin origin) {
            return makeData(new File(origin.getMetadata()[2]));
        }

        private Dialogue makeData(File file){
            ArrayList<CommandDataTuple> commandDataTupleArrayList = new ArrayList<>();
            AbstractSingleFileParser dialogueParser = new AbstractSingleFileParser() {
                @Override
                public void parseFile(File file) {
                    Scanner scanner = FileUtil.makeScanner(file);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String commandString = line.substring(1, line.indexOf(']'));
                        String data;
                        if(line.indexOf(']') + 1 != line.length()) {
                            line = line.substring(line.indexOf(']') + 1);
                            data = line.substring(1, line.indexOf(']'));
                        }
                        else{
                            data = "";
                        }
                        DialogueCommands command = DialogueCommands.getCommand(commandString);
                        commandDataTupleArrayList.add(new CommandDataTuple(command, data));
                    }
                    scanner.close();
                }
            };
            dialogueParser.parseFile(file);
            return new Dialogue(commandDataTupleArrayList.toArray(new CommandDataTuple[0]));
        }
    };

    public static final ResourceTypeTemplate<Properties> PROPERTIES = new ResourceTypeTemplate<Properties>(
            new String[]{"properties"},
            new String[0]
    ) {
        @Override
        protected Properties makeDataFromFile(FileOrigin origin) {
            return FileUtil.parseProperties(origin.getFile());
        }

        @Override
        protected void writeDataToFile(FileOrigin origin, Properties data) {
            FileUtil.writeProperties(data, origin.getFile());
        }
    };

    public static AbstractResourceType<?>[] values() {
        return resourceTypesList.toArray(new AbstractResourceType<?>[0]);
    }

    private static class ResourceTypeTemplate<T> extends resource.AbstractResourceType<T> {
        private final String[] acceptableFileTypes;
        private final String[] acceptableManifestPrefixes;

        public ResourceTypeTemplate(String[] acceptableFileTypes, String[] acceptableManifestPrefixes) {
            this.acceptableFileTypes = acceptableFileTypes;
            this.acceptableManifestPrefixes = acceptableManifestPrefixes;
            resourceTypesList.add(this);
        }

        @Override
        public String[] getAcceptableFileTypes() {
            return acceptableFileTypes;
        }

        @Override
        public String[] getAcceptableManifestPrefixes() {
            return acceptableManifestPrefixes;
        }
    }
}