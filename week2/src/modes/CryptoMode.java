
package modes;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import padding.CryptoPad;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public abstract class CryptoMode {
        
    protected CryptoPad pad;
    protected int blocksize = 16;
    
    protected Cipher createDecryptCipher(byte[] key) throws InvalidKeyException {
        Cipher c = null;
        try {
            c = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            c.init(Cipher.DECRYPT_MODE, k);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        }
        return c;
    }

    protected Cipher createEncryptCipher(byte[] key) throws InvalidKeyException {
        Cipher c = null;
        try {
            c = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            c.init(Cipher.ENCRYPT_MODE, k);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        }
        return c;
    }

    public abstract byte[] encrypt(byte[] key, byte[] message);
    public abstract byte[] decrypt(byte[] key, byte[] cyphertext) 
            throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException;

}
