
package modes;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public interface CryptoMode {
    byte[] encrypt(byte[] key, byte[] message);
    byte[] decrypt(byte[] key, byte[] cyphertext) 
            throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException;

}
