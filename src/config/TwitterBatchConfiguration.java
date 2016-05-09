package config;

import twitter4j.Query;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Map;

/**
 * Created by daniel on 1/30/16.
 */
public class TwitterBatchConfiguration {
    public Twitter _twitterInterface;
    public Query _twitterQuery;
    public int _limitQueriesLeft;
    public long _limitMillisToWait;

    public TwitterBatchConfiguration(
            Twitter twitterInterface, String strQuery, int limitTweetsInQuery)
            throws TwitterException {

        _twitterInterface = twitterInterface;

        _twitterQuery = new Query(strQuery);
        _twitterQuery.setCount(limitTweetsInQuery);

        Map<String, RateLimitStatus> rateLimitStatus = twitterInterface.getRateLimitStatus("search");
        _limitMillisToWait  = getSecondsToWait(rateLimitStatus);
        _limitQueriesLeft = getQueriesLeft(rateLimitStatus);

    }

//    Format should be YYYY-MM-DD
    public void setQueryDate(String dateSince){
        this._twitterQuery.setSince(dateSince);
    }


    private long getSecondsToWait(Map<String, RateLimitStatus> rateLimitStatus){
        return (rateLimitStatus.get("/search/tweets").getSecondsUntilReset() + 2) * 1000L;
    }
    private int getQueriesLeft(Map<String, RateLimitStatus> rateLimitStatus){
        return rateLimitStatus.get("/search/tweets").getRemaining() - 1;
    }

}
