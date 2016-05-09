package config;

import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 1/25/16.
 */
public class TwitterConfiguration {
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private String tweetDate;


    public void setTweetDate(String date){
        tweetDate = date;
    }

    public TwitterConfiguration(){}
    public TwitterConfiguration(String consumerKey, String consumerSecret,
                                String accessToken, String accessTokenSecret){

        this.consumerKey= consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }
    public TwitterConfiguration(String fileDir) throws IOException{
        getConfigFromFile(fileDir);
    }


    public Configuration getConfiguration(){
        ConfigurationBuilder config = new ConfigurationBuilder();
        config.setDebugEnabled(true)
                .setOAuthConsumerKey(this.consumerKey)
                .setOAuthConsumerSecret(this.consumerSecret)
                .setOAuthAccessToken(this.accessToken)
                .setOAuthAccessTokenSecret(this.accessTokenSecret);

        config.setJSONStoreEnabled(true);
        return config.build();
    }

    private List<String[]> readFileConfigurations(String fileDir) throws IOException {
        int configsCount;
        String[] config;
        ArrayList<String []> configs = new ArrayList<>();
        BufferedReader fileBuffer = new BufferedReader(new FileReader(fileDir));

        configsCount = Integer.parseInt(fileBuffer.readLine().trim());
        for(int i = 0; i < configsCount; i++){
            config = new String[4];
            fileBuffer.readLine();

            for (int j = 0; j < 4; j++)
                config[j] = fileBuffer.readLine();

            configs.add(config);
        }

        fileBuffer.close();
        return configs;
    }


    private Configuration buildConfigFromParams(String[] config){
        ConfigurationBuilder temp = new ConfigurationBuilder();

        temp.setDebugEnabled(true);
        temp.setOAuthConsumerKey(config[0]);
        temp.setOAuthConsumerSecret(config[1]);
        temp.setOAuthAccessToken(config[2]);
        temp.setOAuthAccessTokenSecret(config[3]);
        temp.setJSONStoreEnabled(true);

        return temp.build();
    }

    public List<TwitterBatchConfiguration> getConfigurations1(
            String fileDir, String queryStr, int tweetsInQuery) throws IOException {

        ArrayList<TwitterBatchConfiguration> result = new ArrayList<>();
        List<String[]> configList = readFileConfigurations(fileDir);
        for(String[] config : configList){

            Configuration temp = buildConfigFromParams(config);
            Twitter twitterInterface = new TwitterFactory(temp).getInstance();

            try {
                TwitterBatchConfiguration newBatch = new TwitterBatchConfiguration(twitterInterface, queryStr, tweetsInQuery);
                newBatch.setQueryDate(tweetDate);
                result.add(newBatch);
            }
            catch(TwitterException t){
                System.out.println(">> Impossible to create a configuration for: ");
                System.out.println(">> consumer key: " + config[0]);
            }
        }

        return result;
    }

    public List<TwitterBatchConfiguration> getConfigurations(
            String fileDir, String queryStr, int tweetsInQuery) throws IOException, TwitterException {

        ArrayList<TwitterBatchConfiguration> result;
        BufferedReader buffer;
        int configs;

        ConfigurationBuilder tempConfig;
        TwitterBatchConfiguration newBatchConfig;
        Twitter twitterInterface;
        Query twitterQuery;

        result = new ArrayList<>();
        buffer = new BufferedReader(new FileReader(fileDir));

        configs = Integer.parseInt(buffer.readLine().trim());
        buffer.readLine();
        for(int i = 0; i < configs; i++){
            String consumerKey = buffer.readLine();
            String consumerSecret = buffer.readLine();
            String accessToken = buffer.readLine();
            String accessTokenSecret = buffer.readLine();

            // Read the ## and do nothing
            buffer.readLine();

            // Create the config for each connection
            tempConfig = new ConfigurationBuilder();

            twitterInterface = new TwitterFactory(
            tempConfig.setDebugEnabled(true)
                    .setOAuthConsumerKey(consumerKey)
                    .setOAuthConsumerSecret(consumerSecret)
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(accessTokenSecret)
                    .setJSONStoreEnabled(true)
                    .build()
            ).getInstance();


            newBatchConfig = new TwitterBatchConfiguration(twitterInterface, queryStr, tweetsInQuery);
            newBatchConfig.setQueryDate(tweetDate);


            result.add(newBatchConfig);
        }

        return result;
    }


    private void getConfigFromFile(String fileDir) throws IOException {
        BufferedReader buffer = new BufferedReader(new FileReader(fileDir));

        consumerKey = buffer.readLine();
        consumerSecret = buffer.readLine();
        accessToken = buffer.readLine();
        accessTokenSecret = buffer.readLine();

        buffer.close();
    }


}
