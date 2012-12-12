package jlebedeva.week4;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Programming Assignment Week 4
 *
 * In this project you will experiment with a padding oracle attack against a
 * toy web site hosted at crypto-class.appspot.com. Padding oracle
 * vulnerabilities affect a wide variety of products, including secure tokens.
 * This project will show how they can be exploited. We discussed CBC padding
 * oracle attacks in Lecture 7.6, but if you want to read more about them,
 * please see Vaudenay's paper.
 *
 * Now to business. Suppose an attacker wishes to steal secret information from
 * our target web site crypto-class.appspot.com. The attacker suspects that the
 * web site embeds encrypted customer data in URL parameters such as this:
 * http://crypto-class.appspot.com/po?er=f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4
 * That is, when customer Alice interacts with the site, the site embeds a URL
 * like this in web pages it sends to Alice. The attacker intercepts the URL
 * listed above and guesses that the ciphertext following the "po?er=" is a hex
 * encoded AES CBC encryption with a random IV of some secret data about Alice's
 * session.
 *
 * After some experimentation the attacker discovers that the web site is
 * vulnerable to a CBC padding oracle attack. In particular, when a decrypted
 * CBC ciphertext ends in an invalid pad the web server returns a 403 error code
 * (forbidden request). When the CBC padding is valid, but the message is
 * malformed, the web server returns a 404 error code (URL not found).
 *
 * Armed with this information your goal is to decrypt the ciphertext listed
 * above. To do so you can send arbitrary HTTP requests to the web site of the
 * form http://crypto-class.appspot.com/po?er="your ciphertext here" and observe
 * the resulting error code. The padding oracle will let you decrypt the given
 * ciphertext one byte at a time. To decrypt a single byte you will need to send
 * up to 256 HTTP requests to the site. Keep in mind that the first ciphertext
 * block is the random IV. The decrypted message is ASCII encoded.
 */
public class Main {
    private static final String prefix = "http://crypto-class.appspot.com/po?er=";
    private static final byte[] ciphertext = DatatypeConverter.parseHexBinary(
            "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4");
    private static final int blockSize = 16;
    private static HttpClient httpClient;

    public static void main(String[] args) throws IOException {
        try {
            httpClient = init();
            if (ciphertext.length % blockSize != 0) {
                throw new IllegalArgumentException("Wrong block size");
            }
            List<byte[]> blocks = new ArrayList<byte[]>();
            for (int num = 0; num*blockSize < ciphertext.length; num++) {
                byte[] block = new byte[blockSize];
                System.arraycopy(ciphertext, num*blockSize, block, 0, blockSize);
                blocks.add(block);
                System.out.println("Block " + num + ": " + toHex(block));
            }
            byte[] vector = blocks.remove(0);
            String result = "";
            for (byte[] block : blocks) {
                byte[] decrypted = decryptBlock(vector, block);
                result = result.concat(new String(decrypted));
            }
            System.out.println(result);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public static HttpClient init() {
        HttpClient client = new DefaultHttpClient();
        HttpHost proxy = new HttpHost("cache.jet.msk.su", 8080, "http");
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        return client;
    }

    public static byte[] decryptBlock(byte[] vector, byte[] block) throws IOException {
        byte[] guessed = new byte[blockSize];
        String blockString = toHex(block);
        for (int i = 0; i < guessed.length; i++) {
            guessed[i] = 0;
        }
        for (int posInBlock = blockSize; posInBlock-- > 0;) {
            for (int guess = 0; guess < 256; guess++) {
                byte[] xor = getXorValue((byte)guess, posInBlock, guessed);
                String modifiedVector = toHex(xor(xor, vector));
                if (isRightGuess(modifiedVector + blockString)) {
                    guessed[posInBlock] = (byte)guess;
                    System.out.println(new String(guessed));
                    break;
                }
                System.out.println("Alert! No valid pad found for position: " + posInBlock);
            }
        }
        return guessed;
    }
    
    public static byte[] getXorValue(byte guess, int posInBlock, byte[] guessed) {
        byte padValue = (byte) (blockSize - posInBlock);
        byte[] xorValue = new byte[blockSize];
        for (int i = 0; i < blockSize; i++) {
            if (i < posInBlock) {
                xorValue[i] = 0;
            } else if (i == posInBlock) {
                xorValue[i] = (byte) (padValue ^ guess);
            } else {
                xorValue[i] = padValue;
            }
        }
        return xor(xorValue, guessed);
    }

    public static boolean isRightGuess(String suffix) throws IOException {
        int code = 0;
        try {
            HttpGet request = new HttpGet(prefix + suffix);
            HttpResponse response = httpClient.execute(request);
            request.releaseConnection();
            code = response.getStatusLine().getStatusCode();
        } catch (HttpResponseException e) {
            code = e.getStatusCode();
        } finally {
//            System.out.println("suffix: " + suffix + ", code: " + code);
            if (code == 404) {
                return true;
            }
        }
        return false;
    }

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

    public static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        return String.format("%0" + (array.length << 1) + "x", bi);
    }
}
