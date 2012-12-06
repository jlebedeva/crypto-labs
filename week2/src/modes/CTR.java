
package modes;

import padding.CryptoPad;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public class CTR implements CryptoMode {

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
    public byte[] decrypt(byte[] key, byte[] cyphertext) {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
