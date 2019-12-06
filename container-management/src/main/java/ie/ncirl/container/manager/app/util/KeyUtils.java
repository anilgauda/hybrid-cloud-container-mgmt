package ie.ncirl.container.manager.app.util;

public class KeyUtils {

    public static String inString(byte[] key) {
        return new String(key);
    }

    public static byte[] inBytes(String key) {
        return key.getBytes();
    }
}
