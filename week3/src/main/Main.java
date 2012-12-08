package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Programming Assignment Week 3
 * 
 * Instead of computing a hash of the entire file, the web site breaks the file
 * into 1KB blocks (1024 bytes). It computes the hash of the last block and 
 * appends the value to the second to last block. It then computes the hash of
 * this augmented second to last block and appends the resulting hash to the
 * third block from the end.
 * 
 * The final hash value – a hash of the first block with its appended hash –
 * is distributed to users via the authenticated channel as above.
 * 
 * In this project we will be using SHA256 as the hash function.
 * For an implementation of SHA256 use an existing crypto library such as
 * PyCrypto (Python), Crypto++ (C++), or any other. When appending the hash
 * value to each block, please append it as binary data, that is, as 32
 * unencoded bytes (which is 256 bits). If the file size is not a multiple of
 * 1KB then the very last block will be shorter than 1KB, but all other blocks
 * will be exactly 1KB.Your task is to write code to compute the hash h0 of
 * a given file F and to verify blocks of F as they are received by the client.
 * 
 * Your task is to write code to compute the hash h0 of a given file F and to
 * verify blocks of F as they are received by the client. In the box below
 * please enter the (hex encoded) hash h0 for this video file:
 * https://class.coursera.org/crypto-004/lecture/download.mp4?lecture_id=27
 * 
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public class Main {
    private static final String filename = "6 - 1 - Introduction (11 min).mp4";
/**
 * Download test sample at: https://class.coursera.org/crypto-004/lecture/download.mp4?lecture_id=28     
 * Its hash 03c08f4ee0b576fe319338139c045c89c3e8e9409633bea29442e21425006ea8;
 */
    private static final String testFilename = "6 - 2 - Generic birthday attack (16 min).mp4";
    private static final int blockLength = 1024;
    private static final int hashLength = 32;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "r");
        long fileLength = file.length();
        long blockNumber = fileLength / blockLength;
        if (fileLength % blockLength != 0) {
            blockNumber++;
        }
        int lastBlockLength = (int) (fileLength - (blockLength * (blockNumber - 1)));
        
        System.out.println("File length is " + fileLength);
        System.out.println("That is " + blockNumber + " blocks of " + blockLength + " bytes");
        System.out.println("Last block is " + lastBlockLength + " bytes long");
        
        byte[] hash = new byte[hashLength];
        byte[] input = new byte[hashLength + blockLength];
        for (long i = blockNumber; i-- > 0;) {
            file.seek(i * blockLength);
            if (i == blockNumber - 1) {
                byte[] lastBlock = new byte[lastBlockLength];
                file.read(lastBlock, 0, lastBlockLength);
                hash = md5(lastBlock);
            } else {
                file.read(input, 0, blockLength);
                System.arraycopy(hash, 0, input, blockLength, hashLength);
                hash = md5(input);
            }
        }
        BigInteger bi = new BigInteger(1, hash);
        System.out.println("Hash is: " + String.format("%0" + (hash.length << 1) + "x", bi));

    }

    private static byte[] md5(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(input);
        return md.digest();
    }
}
