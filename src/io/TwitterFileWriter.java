package io;

import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.google.gson.*;

/**
 * Created by daniel on 1/24/16.
 */
public class TwitterFileWriter implements ITweetReceiver{

    private File dataFile;
    private FileWriter writer;
    private BufferedWriter buffer;
    private String dataFileDir;

    private int _dataFileLen;
    private final String eol = "\n";

    public TwitterFileWriter(String fileDir) throws IOException{
        dataFileDir = fileDir;
        _dataFileLen = 0;

        init();
    }

    private void init() throws IOException {
        dataFile = new File(dataFileDir);

        writer = new FileWriter(dataFile, true);
        buffer = new BufferedWriter(writer);
    }

    public int getDataFileLen(){ return _dataFileLen; }

    public void saveTweets(List<Status> list) throws IOException {
        _dataFileLen += list.size();

        StringBuilder buff = new StringBuilder();
        for(Status tweet : list)
            buff.append(tweet.toString() + eol);

        buffer.append(buff.toString());
    }

    public void saveTweetsJSON(List<Status> list) throws IOException {
        _dataFileLen += list.size();
        StringBuilder buff = new StringBuilder();

        Gson parser = new Gson();
        for(Status tweet : list)
            buff.append(parser.toJson(tweet) + eol);

        buffer.append(buff.toString());
    }

    public void flushFile() throws IOException {
        buffer.flush();
        writer.flush();
    }

    public void close() throws IOException {
        buffer.close();
        writer.close();
    }

    @Override
    public void doTweetStorage(List<Status> tweets) throws IOException {
        if(tweets.size() > 0)
            saveTweetsJSON(tweets);
//            saveTweets(tweets);
    }

    @Override
    public void flushTweetStorage() throws IOException{
        flushFile();
    }

    @Override
    public void closeTweetStorage() throws IOException{
        close();
    }
}
