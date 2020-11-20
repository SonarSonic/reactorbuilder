package sonar.reactorbuilder.util;

import net.minecraft.util.text.translation.I18n;

import java.io.File;

public class Util {

    public static byte[] getByteArrayFromBooleanArray(boolean[] array, int size){
        byte[] bytes = new byte[size];
        for(int i = 0 ; i < Math.min(array.length, size); i ++){
            bytes[i] = (byte)(array[i] ? 1 : 0);
        }
        return bytes;
    }

    public static boolean[] getBooleanArrayFromByteArray(byte[] array, int size){
        boolean[] bools = new boolean[size];
        for(int i = 0 ; i < Math.min(array.length, size); i ++){
            bools[i] = array[i] != 0;
        }
        return bools;
    }

    public static String getFileExtension(File file){
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i >= 0) {
            extension = file.getName().substring(i+1);
        }
        return extension;
    }

    public static String translate(String s){
        return I18n.translateToLocal(s);
    }

}
