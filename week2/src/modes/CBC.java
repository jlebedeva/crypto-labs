
package modes;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import main.Utils;
import padding.CryptoPad;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public class CBC implements CryptoMode {
    
    private CryptoPad pad;
    private int blocksize = 16;

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
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.DECRYPT_MODE, k);

        int length = cyphertext.length - blocksize;
        if (length % blocksize != 0) {
            throw new IllegalArgumentException();
        }
        byte[] vector = new byte[blocksize];
        System.arraycopy(cyphertext, 0, vector, 0, blocksize);
        byte[] data = new byte[length];
        System.arraycopy(cyphertext, blocksize, data, 0, length);

        byte[] result = new byte[length];
        for (int i = 0; i < length; ) {
            byte[] block = new byte[blocksize];
            System.arraycopy(data, i, block, 0, blocksize);
            byte[] decryptedBlock = c.doFinal(block);
            System.arraycopy(Utils.xor(vector, decryptedBlock), 0, result, i, blocksize);
            vector = block;
            i=i+blocksize;
        }
//        return pad.unPad(result);
        return result;
    }

}
