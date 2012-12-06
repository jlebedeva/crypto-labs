
package modes;

import java.security.InvalidKeyException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import main.Utils;
import padding.CryptoPad;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public class CBC extends CryptoMode {

    public CBC(CryptoPad pad) {
        this.pad = pad;
    }

    @Override
    public byte[] encrypt(byte[] key, byte[] message) {

        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] cyphertext) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        Cipher c = createDecryptCipher(key);

        int length = cyphertext.length - blocksize;
        if (length % blocksize != 0) {
            throw new IllegalArgumentException();
        }
        byte[] vector = new byte[blocksize];
        System.arraycopy(cyphertext, 0, vector, 0, blocksize);
        byte[] data = new byte[length];
        System.arraycopy(cyphertext, blocksize, data, 0, length);
        System.out.println("Message with padding is " + length + " bytes long, which is " + (length / blocksize) + " blocks");
       
        byte[] result = new byte[length];
        for (int num = 0; num*blocksize < length; num++) {
            int i = num * 16;
            byte[] block = new byte[blocksize];
            System.arraycopy(data, i, block, 0, blocksize);
            byte[] decryptedBlock = c.doFinal(block);
            System.arraycopy(Utils.xor(vector, decryptedBlock), 0, result, i, blocksize);
            
            vector = block;
        }
        result = pad.unPad(result);
        System.out.print("Byte message is: ");
        System.out.println(Arrays.toString(result));
        return result;
    }

}
