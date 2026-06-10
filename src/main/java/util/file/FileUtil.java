package util.file;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.Scanner;

public final class FileUtil {
    public static final String DIRECTORY_EXTENSION = "directory";

    private FileUtil() {
    }

    public static String getFilePath(File file) {
        return file.getAbsolutePath();
    }

    public static String getFileName(File file) {
        String filePath = getFilePath(file);
        int lastIndexOfPeriod = filePath.lastIndexOf('.');
        String filePathWithoutExtension;
        if (lastIndexOfPeriod > 0) {
            String extension = filePath.substring(lastIndexOfPeriod);
            if (extension.contains("\\")) { //if the extension has a backslash, it means there is no extension
                filePathWithoutExtension = filePath;
            } else {
                filePathWithoutExtension = filePath.substring(0, lastIndexOfPeriod);
            }
        } else {
            filePathWithoutExtension = filePath;
        }
        int lastIndexOfBackSlash = filePath.lastIndexOf('\\');
        if (lastIndexOfBackSlash > 0) {
            return filePathWithoutExtension.substring(lastIndexOfBackSlash + 1);
        } else {
            return filePathWithoutExtension;
        }
    }

    public static String getFileExtension(File file) {
        if (!file.isDirectory()) {
            String filePath = getFilePath(file);
            int lastIndexOfPeriod = filePath.lastIndexOf('.');
            if (lastIndexOfPeriod > 0) {
                return filePath.substring(lastIndexOfPeriod + 1);
            }
            return null;
        } else {
            return DIRECTORY_EXTENSION;
        }
    }

    public static InputStream makeInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            throw new RuntimeException("Unable to find file " + file.getAbsolutePath(), fnfe);
        }
    }

    public static OutputStream makeOutputStream(File file){
        try{
            return new FileOutputStream(file);
        } catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            throw new RuntimeException("Unable to make file " + file.getAbsolutePath(), fnfe);
        }
    }

    public static Scanner makeScanner(File file){
        try{
            return new Scanner(file);
        }catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
            throw new RuntimeException("Unable to make file " + file.getAbsolutePath(), fnfe);
        }
    }

    public static BufferedImage parseImage(File imageFile) {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new UncheckedIOException(ioe);
        }
    }

    public static Properties parseProperties(File propertiesFile) {
        try {
            Properties toRet = new Properties();
            InputStream inputStream = makeInputStream(propertiesFile);
            toRet.load(inputStream);
            inputStream.close();
            return toRet;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new UncheckedIOException(ioe);
        }
    }

    public static void writeProperties(Properties properties, File propertiesFile){
        try{
            OutputStream outputStream = makeOutputStream(propertiesFile);
            properties.store(outputStream, null);
            outputStream.close();
        } catch(IOException ioe){
            ioe.printStackTrace();
            throw new UncheckedIOException(ioe);
        }
    }
}
