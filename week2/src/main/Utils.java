
package main;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public class Utils {
    public static byte[] xor(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) {
            throw new IllegalArgumentException();
        }
        byte[] result = new byte[b1.length];
        for (int i = 0; i < b1.length; i++) {
            int x = b1[i] ^ b2[i];
            result[i] = (byte)x;
        }
        return result;
    }
}
