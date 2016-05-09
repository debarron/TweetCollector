package main;

import config.TwitterConfiguration;
import io.TwitterFileWriter;
import io.TwitterScanner;
import twitter4j.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 1/22/16.
 */
public class Main2 {


    public static void main(String[] args) {
        // Test the Twitter4J API

        // The configuration
        final String stringQuery = "EPN ";

        final int tweetsInQuery = 100;
        final int tweetAmount = 1000;

        final String baseDir = "/Users/daniel/Documents/UMKC/Spring2016/PrinciplesOfBigData/TweetData";
        String strDir = baseDir + "test-1.json";

        // Count the tweets, lets see how much it takes to reach 1K
        long t1, totalTime;
        int tweetCount;

        tweetCount = 0;

        try {
            TwitterFileWriter writer = new TwitterFileWriter(strDir);
            TwitterConfiguration myConfig = new TwitterConfiguration(
                    "XnsFBxRioUQzAzeRbHIILqyyR",
                    "KYU020A2s9xZY2JAcqlM7fYGZoKGrwO2DImv2ohgoUuUIXcoiY",
                    "1078513057-pnI9KwYXP0QpirYDNlMKIDOqIdWY7wbDWQMi1Ed",
                    "Vro3cwUZdbAc0yZsTlKUQwurnjJ2hIXZyIDdxHvtdxETz"
            );
            TwitterScanner scanner = new TwitterScanner(stringQuery,
                    tweetsInQuery,
                    tweetAmount,
                    1000,
                    myConfig.getConfiguration(),
                    writer
            );

            // Start up the connection with Twitter
            TwitterFactory twitterFactory = new TwitterFactory(myConfig.getConfiguration());
            Twitter twitterInterface = twitterFactory.getInstance();

            int tweetsPerQuery = 100;
            long tweetMaxId = -1;


//            twitterQuery.setSinceId(lastId);
//            //	This returns all the various rate limits in effect for us with the Twitter API
//            Map<String, RateLimitStatus> rateLimitStatus = twitterInterface.getRateLimitStatus("search");
//            //	This finds the rate limit specifically for doing the search API call we use in this program
//            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
//            //	Always nice to see these things when debugging code...
//            System.out.printf("You have %d calls remaining out of %d, Limit resets in %d seconds\n",
//                    searchTweetsRateLimit.getRemaining(),
//                    searchTweetsRateLimit.getLimit(),
//                    searchTweetsRateLimit.getSecondsUntilReset());



            Query twitterQuery = new Query(stringQuery);
            twitterQuery.setCount(tweetsPerQuery);
            twitterQuery.setMaxId(tweetMaxId);

            totalTime = 0;

            while(tweetCount < tweetAmount) {

                //  This returns all the various rate limits in effect for us with the Twitter API
                // 	This finds the rate limit specifically for doing the search API call we use in this program
                Map<String, RateLimitStatus> rateLimitStatus = twitterInterface.getRateLimitStatus("search");
                RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

                // Check if we need to wait to make more queries
                if(searchTweetsRateLimit.getRemaining() - 1 <= 0){
                    int seconds = searchTweetsRateLimit.getSecondsUntilReset() + 2;

                    totalTime += seconds * 1000;

                    System.out.println("## We need to sleep to get more");
                    System.out.println("## Sleeping for " + seconds + " seconds");

                    try {
                        Thread.sleep(seconds * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("## .... Back again");
                }

                // Get the data
                t1 = System.currentTimeMillis();
                QueryResult twitterResult = twitterInterface.search(twitterQuery);
                totalTime += System.currentTimeMillis() - t1;
                List<Status> tweets = twitterResult.getTweets();

                // Look for the minor id
                for (Status tweet : tweets) {
                    if(tweetMaxId == -1 || tweet.getId() < tweetMaxId)
                        tweetMaxId = tweet.getId();
                }

                twitterQuery.setMaxId(tweetMaxId);
                tweetCount += tweets.size();

                // Print the info
                if(tweetCount % 100 == 0) {
                    System.out.println(" ");
                    System.out.println(">> ");
                    System.out.println("Num of tweets retrieved: " + tweets.size());
                    System.out.println("Tweets collected so far: " + tweetCount);
                    System.out.println("Time expended (secs): " + (totalTime / 1000));
                    System.out.println(">> ");
                    System.out.println(" ");
                }
            }
        }
        catch (TwitterException twE){
            twE.printStackTrace();
            System.out.println("Message " + twE.getMessage());
        }
        catch (IOException p){
            System.out.println("Error while creating the writer");
            p.printStackTrace();
        }

        System.out.println("The end");
        System.out.println("Tweets collected so far: " + tweetCount);

    }



}
