package main;

import config.TwitterConfiguration;
import io.TwitterBatchScanner;
import io.TwitterFileWriter;
import io.TwitterScanner;

import javax.swing.plaf.synth.SynthTextAreaUI;

/**
 * Created by daniel on 1/22/16.
 */

/*
http://twitter4j.org/en/code-examples.html
http://keyurj.blogspot.com/2014/02/reading-twitter-stream-using-twitter4j.html
http://stackoverflow.com/questions/9395999/twitter4j-access-tweet-information-from-streaming-api
https://github.com/yusuke/twitter4j
* */

public class Main {


    public static void displayHelp(){
        String message = "USAGE of TweetCollector \n" +
                "\t console: java -jar TweetCollector.jar <config> <result> <twets> <query> <date> \n" +
                "\t >> config: Complete path to the config file \n" +
                "\t >> result: Path to the file where the program will store the result \n" +
                "\t >> tweets: Number of tweets to collect  \n" +
                "\t >> query: Terms of the search, format: \"term1 term2\" \n" +
                "\t >> id: Start the search from this id (optional), \n\n";

        System.out.println(message);
    }

    public static void main(String[] args) {


        final int tweetsInQuery = 100; // Max 100 min 15
        final String baseDir = "/Users/daniel/Documents/UMKC/Spring2016/PrinciplesOfBigData/TweetData";

        // The configuration
        String stringQuery;
        int tweetAmount;
        String configDir;
        String configDirBatch;
        String outputDir;
        String date = "";
        String dateUntil = "";
        long tid = 715739541047418879L;

//        // Display help
//        if(args.length == 0){
//            displayHelp();
//            System.exit(1);
//        }
//        configDir = args[0];
//        outputDir = args[1];
//        tweetAmount = Integer.parseInt(args[2]);
//        stringQuery = args[3];

        if (args.length > 4) {
            tid = Long.parseLong(args[4]);
        }


        configDir = baseDir + "/config6.txt";
        outputDir = baseDir + "/data-ActionOnChangeB-1.json";
        outputDir = baseDir + "/data-Positive-1-1-5.json";
        tweetAmount = 100000;
        stringQuery = "#ActionOnChange";

        stringQuery = "(#actonclimate) OR (#ClimateChange) OR (#actonclimate) OR (#environment) OR (#pollution)";

                /*#ClimateSkeptic, #ClimateDenial*/
        date = "2010-01-13";
        date = "2007-02-04";


        // Count the tweets, lets see how much it takes to reach 1K
        try {
            TwitterFileWriter writer = new TwitterFileWriter(outputDir);
            TwitterConfiguration myConfig = new TwitterConfiguration(configDir);

//            TwitterScanner scanner = new TwitterScanner(stringQuery,
//                    tweetsInQuery,
//                    tweetAmount,
//                    1000,
//                    myConfig.getConfiguration(),
//                    writer
//            );

            TwitterBatchScanner scanner = new TwitterBatchScanner(stringQuery,
                    tweetsInQuery,
                    tweetAmount,
                    1000,
                    writer);

            if(tid != -1){
                scanner.setPathMaxID(tid);
            }

            scanner.computeBatch(configDir);
        }
        catch (Exception twE){
            twE.printStackTrace();
            System.out.println("Message " + twE.getMessage());
        }

        System.out.println("\n ## End of program \n");
    }



}
