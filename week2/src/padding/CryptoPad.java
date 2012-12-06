
package padding;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public interface CryptoPad {

    byte[] pad(byte[] message);

    byte[] unPad(byte[] message);
}
