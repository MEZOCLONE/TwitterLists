package com.tierep.twitterlists;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Session singleton that contains all the information of the session.
 * <p/>
 * Created by Pieter on 30/01/15.
 */
public class Session {
    private static final Session session = new Session();

    private String TWITTER_KEY;
    private String TWITTER_SECRET;

    private String accessToken = "";
    private String accessTokenSecret = "";

    private Twitter twitterInstance = null;
    private AsyncTwitter asyncTwitterInstance = null;

    private long userId;

    /**
     * Private constructor prevents instantiation in other classes
     */
    private Session() {
    }

    public void initialize(Context context) {
        InputStream in = context.getResources().openRawResource(R.raw.twitter4j);
        Properties prop = new Properties();
        try {
            prop.load(in);
            this.TWITTER_KEY = prop.getProperty("oauth.consumerKey");
            this.TWITTER_SECRET = prop.getProperty("oauth.consumerSecret");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Twitter getTwitterInstance() {
        if (twitterInstance == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(TWITTER_KEY)
                    .setOAuthConsumerSecret(TWITTER_SECRET)
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(accessTokenSecret);
            TwitterFactory tf = new TwitterFactory(cb.build());
            twitterInstance = tf.getInstance();
        }
        return twitterInstance;
    }

    public AsyncTwitter getAsyncTwitterInstance() {
        if (asyncTwitterInstance == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(TWITTER_KEY)
                    .setOAuthConsumerSecret(TWITTER_SECRET)
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(accessTokenSecret);

            AsyncTwitterFactory factory = new AsyncTwitterFactory(cb.build());
            asyncTwitterInstance = factory.getInstance();
        }
        return asyncTwitterInstance;
    }

    public static Session getInstance() {
        return session;
    }

    public String getTWITTER_SECRET() {
        return TWITTER_SECRET;
    }

    public String getTWITTER_KEY() {
        return TWITTER_KEY;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
