/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package week5;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Programming Assignment Week 5
 *
 * Your goal this week is to write a program to compute discrete log modulo a
 * prime p. Let g be some element in Z*p and suppose you are given h in Z*p such
 * that h=g^x where 1≤x≤2^40. Your goal is to find x. More precisely, the input to
 * your program is p,g,h and the output is x.
 * The trivial algorithm for this problem is to try all 2^40 possible values of x
 * until the correct one is found, that is until we find an x satisfying h=g^x in
 * Zp. This requires 2^40 multiplications. In this project you will implement an
 * algorithm that runs in time roughly 2^20 using a meet in the middle
 * attack.
 * Let B=2^20. Since x is less than B^2 we can write the unknown x base B as
 * x=x_0*B+x_1 where x_0,x_1 are in the range [0,B−1]. Then
 *
 * h=g^x=g^(x_0*B+x_1)=(g^B)^x_0 * g^x_1 in Zp.
 *
 * By moving the term g^x_1 to the other side we obtain
 *
 * h/g^x_1=(g^B)^x_0 in Zp.
 *
 * The variables in this equation are x_0,x_1 and everything else is known: you
 * are given g,h and B=2^20. Since the variables x_0 and x_1 are now on different
 * sides of the equation we can find a solution using meet in the middle
 * (Lecture 3.3): First build a hash table of all possible values of the left
 * hand side h/g^x_1 for x_1=0,1,…,2^20. Then for each value x_0=0,1,2,…,2^20 check if
 * the right hand side (g^B)^x_0 is in this hash table. If so, then you have found
 * a solution (x_0,x_1) from which you can compute the required x as x=x_0*B+x_1. The
 * overall work is about 2^20 multiplications to build the table and another 2^20
 * lookups in this table.
 *
 * To test:
 * x1(3)=9412394884692981603806680440403102015154978075456925615997659385849607264684414990414637267232961588893041660817569274188696136246050970099061095323101258
 * x0(3)=7565599653190319053467818354668782984230096884918571310099042116774227208796185303200868402606980112854220217466838985482961089857423369985182034226789451
 */
public class TableBuilder implements Callable<HashMap<BigInteger, Integer>> {
    
    private int start;
    private int length;
    
    private static BigInteger p;
    private static BigInteger g;
    private static BigInteger h;
    private static final BigInteger B = BigInteger.valueOf(2).pow(20);
    private static final int threads = 8; // number of threads, divisible by 2
    
    private TableBuilder(int start, int length) {
        this.start = start;
        this.length = length;
    }
    
    @Override
    public HashMap<BigInteger, Integer> call() throws Exception {
        return buildLeftChunk();
    }
    
    private HashMap<BigInteger, Integer> buildLeftChunk() {
        HashMap<BigInteger, Integer> chunk = new HashMap<>();
        BigInteger currentValue = g.modPow(BigInteger.valueOf(start), p);
        for (int i = start; i < (start + length); i++) {
            if (i != start) {
                currentValue = currentValue.multiply(g).mod(p);
            }
            if (i == 3) {
                System.out.println("x1(3) = " + h.multiply(currentValue.modInverse(p)).mod(p));
            }
            chunk.put(h.multiply(currentValue.modInverse(p)).mod(p), i);
        }
        return chunk;
    }
    
    public static void main(String[] args) throws IOException {
        long totalStartTime = System.currentTimeMillis();
        long startTime = totalStartTime;
        getInput();
        long finishTime = System.currentTimeMillis() - startTime;
        System.out.println("1. Input loaded (" + finishTime + "ms)");

        startTime = System.currentTimeMillis();
        HashMap<BigInteger, Integer> leftTable = buildLeftTable();
        finishTime = System.currentTimeMillis() - startTime;
        System.out.println("2. Left-side table built (" + finishTime + "ms)");
        
        startTime = System.currentTimeMillis();        
        BigInteger[] result = findCollision(leftTable);
        finishTime = System.currentTimeMillis() - startTime;
        System.out.println("3. Right-side computation finished (" + finishTime + "ms)");
        if (result != null) {
            System.out.println("x0 (right table) = " + result[0]);
            System.out.println("x1 (left table) = " + result[1]);
            BigInteger answer = B.multiply(result[0]).add(result[1]).mod(p);
            System.out.println("x = " + answer);
            System.out.println("Check it: g^x = h mod p (" + g.modPow(answer, p).equals(h) + ")");
        } else {
            System.out.println("x not found");
        }
        long totalTime = (System.currentTimeMillis() - totalStartTime) / 1000;
        System.out.println("Total time: " + totalTime + " seconds");
    }
    
    private static void getInput() throws IOException {
        Properties props = new Properties();
        props.load(ClassLoader.getSystemResourceAsStream("input"));
        for (String propName : props.stringPropertyNames()) {
            switch (propName) {
                case "p": p = new BigInteger(props.getProperty(propName));
                    break;
                case "g": g = new BigInteger(props.getProperty(propName));
                    break;
                case "h": h = new BigInteger(props.getProperty(propName));
                    break;
            }
        }
        System.out.println("p = " + p);
        System.out.println("g = " + g);
        System.out.println("h = " + h);
    }

    public static HashMap<BigInteger, Integer> buildLeftTable() {
        HashMap<BigInteger, Integer> leftTable = new HashMap<>();
        try {
            int chunkSize = B.intValue() / threads;
            int chunkNumber = 0;
            List<TableBuilder> tasks = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                tasks.add(new TableBuilder(chunkSize * chunkNumber, chunkSize));
                chunkNumber++;
            }
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            List<Future<HashMap<BigInteger, Integer>>> results = executor.invokeAll(tasks);
            executor.shutdown();

            for (Future<HashMap<BigInteger, Integer>> result : results) {
                try {
                    leftTable.putAll(result.get());
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return leftTable;
    }

    private static BigInteger[] findCollision(HashMap<BigInteger, Integer> leftTable) {
        BigInteger base = g.modPow(B, p);
        BigInteger currentValue = BigInteger.valueOf(1);
        for (int i = 0; i < B.longValue(); i++) {
            if (i != 0) {
                currentValue = currentValue.multiply(base).mod(p);
            }
            if (i == 3) {
                System.out.println("x0(3) = " + currentValue);
            }
            if (leftTable.containsKey(currentValue)) {
                int percent = (i * 100) / B.intValue();
                System.out.println("Collision after " + percent + "%");
                return new BigInteger[] {BigInteger.valueOf(i), BigInteger.valueOf(leftTable.get(currentValue))};
            }
        }
        return null;
    }
}
