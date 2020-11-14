package sonar.reactorbuilder.util;

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

}
