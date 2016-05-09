package io;

import twitter4j.Status;

import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 1/25/16.
 */
public interface ITweetReceiver {
    void doTweetStorage(List<Status> tweets) throws IOException;
    void flushTweetStorage() throws IOException;
    void closeTweetStorage() throws IOException;
}
