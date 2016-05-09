package io;

import config.TwitterBatchConfiguration;
import config.TwitterConfiguration;
import twitter4j.*;
import twitter4j.conf.Configuration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 1/25/16.
 */
public class TwitterBatchScanner {

    private long limitSecondsToRest;
    private int limitNumOfQueries;
    private long limitMaxId;
    private int limitTweetsInMemory;

    private long pathMaxID;

    private int _tweetAmount;
    private int _tweetsCount;
    private int _tweetsInQuery;
    private String _strQuery;
    private String tweetDate;

    private long _totalMillisecs;
    private ArrayList<Status> temporaryTweetStorage;
    private int temporaryQueriesCount;

    private Twitter twitterInterface;
    private ITweetReceiver delegate;


    public TwitterBatchScanner(String strSearchQuery,
                               int tweetsInQuery,
                               int tweetAmount,
                               int tweetsInMemory,
                               ITweetReceiver delegate){

        _strQuery = strSearchQuery;
        _tweetsInQuery = tweetsInQuery;
        _tweetAmount = tweetAmount;
        this.delegate = delegate;
        limitTweetsInMemory = tweetsInMemory;

        this.tweetDate = "";

        this.setPathMaxID(-1);
        this.temporaryTweetStorage = new ArrayList<>();
    }


    private void printInfo(){
        String message = String.format(
                "## Message update \n" +
                        " >> Tweets stats (%d / %d) \n" +
                        " >> Queries stats (%d / %d) \n" +
                        " >> Elapsed time %d secs \n",
                _tweetsCount, _tweetAmount,
                temporaryQueriesCount, limitNumOfQueries,
                _totalMillisecs / 1000
        );
        System.out.println(message);
    }

    private List<TwitterBatchConfiguration> getConfigurationList(TwitterConfiguration configuration, String dir) throws IOException, TwitterException {
        return configuration.getConfigurations1(dir, _strQuery, _tweetsInQuery);
    }

    private void restartConfigLimits(TwitterBatchConfiguration conf){
        _totalMillisecs = conf._limitMillisToWait;
        limitSecondsToRest = conf._limitMillisToWait;
        limitNumOfQueries = conf._limitQueriesLeft;
        temporaryQueriesCount = 0;
    }

    private long preceedToStorage(List<Status> tweets, TwitterBatchConfiguration configuration){
        long id = getMaxId(tweets);
        setPathMaxID(id);

        configuration._twitterQuery.setMaxId(id);

        temporaryTweetStorage.addAll(tweets);
        _tweetsCount += tweets.size();
        temporaryQueriesCount++;

        return id;
    }

    private void printConfInfo() {
        try {
            if(temporaryTweetStorage.size() == 0) return;

            System.out.println(" >> Writing into disk");
            delegate.doTweetStorage(temporaryTweetStorage);
            delegate.flushTweetStorage();
            temporaryTweetStorage.clear();

            System.out.println(" ## Getting a good sleep for " +
                    (limitSecondsToRest / 1000) / 2 + " secs");
            temporaryQueriesCount = 0;
            Thread.sleep(limitSecondsToRest / 2);
            System.out.println(" ## Up and running");

            System.out.println(" ## Finish with one configuration, get another");
        }
        catch(Exception p){
            p.printStackTrace();
        }
    }

    // Code update
    public void computeBatch(String batchConfigPath){
        List<TwitterBatchConfiguration> configList;

        resetTime();
        resetTweetCounter();

        try {
            TwitterConfiguration conf1 = new TwitterConfiguration();
            configList = getConfigurationList(conf1, batchConfigPath);

            boolean isFinished = false;
            do{
                long id = -1;
                for(TwitterBatchConfiguration conf : configList) {
                    // Tweets have been collected, go gome!
                    if(isFinished) break;

                    // The config object is null, do something
                    if (conf == null) continue;
                    restartConfigLimits(conf);

                    for (int i = 0; i < limitNumOfQueries && !isFinished; i++) {
                        // Get the result
                        QueryResult twitterResult = getQueryResult(conf);
                        List<Status> tweets = twitterResult.getTweets();

                        if (tweets.isEmpty()) continue;

                        id = preceedToStorage(tweets, conf);
                        printInfo();
                        isFinished = _tweetsCount > _tweetAmount;
                    }
                    printConfInfo();
                }

                System.out.println("Last ID computed: " + id);
                System.out.printf("Finish with all the configuration, start again?");

            }while(!isFinished);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        finally {
            printConfInfo();
        }

        System.out.println("End of the program");
    }

    private QueryResult getQueryResult(TwitterBatchConfiguration conf) throws TwitterException {
        long currentMillis = System.currentTimeMillis();

        conf._twitterQuery.setMaxId(getPathMaxID());

        QueryResult twitterResult = conf._twitterInterface.search(conf._twitterQuery);
        addTime(currentMillis, System.currentTimeMillis());

        return twitterResult;
    }
    private long getMaxId(List<Status> tweets) {
        long id = -1;
        for (Status tweet : tweets) {
            if (id == -1 || tweet.getId() < id)
                id = tweet.getId();
        }
        return id - 1;
    }

    private void resetTime(){
        _totalMillisecs = 0;
    }
    private void resetTweetCounter(){
        _tweetsCount = 0;
    }


    private void configureLimits() throws TwitterException{
        Map<String, RateLimitStatus> rateLimitStatus = twitterInterface.getRateLimitStatus("search");
        RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

        limitSecondsToRest = (searchTweetsRateLimit.getSecondsUntilReset() + 2) * 1000L;
        limitNumOfQueries = searchTweetsRateLimit.getRemaining() - 1;
    }

    private void addTime(long timeMillis1, long timeMillis2){
        _totalMillisecs += timeMillis2 - timeMillis1;
    }

    public long getPathMaxID() {
        return pathMaxID;
    }

    public void setPathMaxID(long pathMaxID) {
        this.pathMaxID = pathMaxID;
    }
}
