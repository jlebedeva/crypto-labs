
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
public class CTR extends CryptoMode {

    private CryptoPad pad;

    public CTR(CryptoPad pad) {
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
        Cipher c = createEncryptCipher(key);

        int length = cyphertext.length - blocksize;
        byte[] vector = new byte[blocksize];
        System.arraycopy(cyphertext, 0, vector, 0, blocksize);
        byte[] data = new byte[length];
        System.arraycopy(cyphertext, blocksize, data, 0, length);
        int blockNum = length / blocksize;
        if (blockNum % length != 0) {
            blockNum++;
        }
        System.out.println("Message with padding is " + length + " bytes long, which is " + blockNum + " blocks");
       
        byte[] result = new byte[length];
       for (int currentNum = 0; currentNum < blockNum; currentNum++) {
            int index = currentNum * 16;
            byte[] block = new byte[blocksize];
            int blockLength;
            if (blockNum > currentNum + 1) {
                blockLength = blocksize;
            } else {
                blockLength = length - index;
            }
            System.arraycopy(data, index, block, 0, blockLength);
            byte[] decryptedBlock = c.doFinal(vector);
            vector[vector.length - 1]++;
            System.arraycopy(Utils.xor(block, decryptedBlock), 0, result, index, blockLength);
        }
        System.out.print("Byte message is: ");
        System.out.println(Arrays.toString(result));
        return result;
    }

}
