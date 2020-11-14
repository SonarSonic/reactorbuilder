package sonar.reactorbuilder.common.files;

import java.io.File;

public class FileUtils {

    private static final AbstractFileReader[] readers = new AbstractFileReader[]{ThizNCPFReader.INSTANCE, HellrageJSONReader.INSTANCE};

    public static AbstractFileReader getFileReader(File file, String extension){
        for(AbstractFileReader reader : readers){
            if(reader.canReadFile(file, extension)){
                return reader;
            }
        }
        return null;
    }

    public static String getFileExtension(File file){
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i >= 0) {
            extension = file.getName().substring(i+1);
        }
        return extension;
    }

}
