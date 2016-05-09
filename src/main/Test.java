package main;

import java.util.Random;

/**
 * Created by daniel on 4/10/16.
 */
public class Test {
    public static void main(String[] args) {
        log("Generating 10 random integers in range 0..99.");

        //note a single Random object is reused here
        Random randomGenerator = new Random();
        for (int idx = 1; idx <= 76; ++idx){
            int randomInt = randomGenerator.nextInt(20) % 20;
            log("Generated : " + randomInt);
        }

        log("Done.");
    }

    private static void log(String aMessage){
        System.out.println(aMessage);
    }
}
