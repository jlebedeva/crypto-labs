
package padding;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public class PKCS5 implements CryptoPad {
    private int blocksize;

    public PKCS5(int blocksize) {
        this.blocksize = blocksize;
    }

    public byte[] pad(byte[] message) {
        int length = message.length;
        int padding = 0 - (length % blocksize) + 16;
        byte[] result = new byte[length + padding];
        System.arraycopy(message, 0, result, 0, length);
        for (int i = 0 ; i < padding; i++ ) {
            result[length + i] = Integer.valueOf(padding).byteValue();
        }
        return result;
    }

    public byte[] unPad(byte[] message) {
        int padding = (int)message[message.length - 1] + 256;
        System.out.println(padding);
        int length = message.length - padding;
        byte[] result = new byte[length];
        System.arraycopy(message, 0, result, 0, length);
        return result;
    }
}
