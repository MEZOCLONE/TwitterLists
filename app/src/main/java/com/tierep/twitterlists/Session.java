package com.tierep.twitterlists;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tierep.twitterlists.twitter4jcache.TwitterCache;

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
    private static final String PREFERENCE_FILE_KEY = "com.tierep.twitterlists.PREFERENCE_FILE_KEY";
    private static final String KEY_ACCESSTOKEN = "Session.ACCESSTOKEN";
    private static final String KEY_ACCESSTOKENSECRET = "Session.ACCESSTOKENSECRET";
    private static final String KEY_USERID = "Session.USERID";

    private static final Session session = new Session();

    private String TWITTER_KEY;
    private String TWITTER_SECRET;

    private String accessToken = "";
    private String accessTokenSecret = "";

    private Twitter twitterInstance = null;
    private TwitterCache twitterCacheInstance = null;
    private AsyncTwitter asyncTwitterInstance = null;

    private long userId;

    /**
     * Private constructor prevents instantiation in other classes
     */
    private Session() {
    }

    /**
     *
     * @param context
     * @return true if there are valid authorization tokens. False otherwise.
     */
    public boolean initialize(Context context) {
        // Initialize consumer key and consumer secret
        InputStream in = context.getResources().openRawResource(R.raw.twitter4j);
        Properties prop = new Properties();
        try {
            prop.load(in);
            this.TWITTER_KEY = prop.getProperty("oauth.consumerKey");
            this.TWITTER_SECRET = prop.getProperty("oauth.consumerSecret");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize access tokens, if any.
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString(KEY_ACCESSTOKEN, "");
        accessTokenSecret = sharedPref.getString(KEY_ACCESSTOKENSECRET, "");
        userId = sharedPref.getLong(KEY_USERID, 0);

        Log.d("TOKENS", accessToken);
        Log.d("TOKEN SECRET", accessTokenSecret);

        if (!accessToken.equals("") && !accessTokenSecret.equals("")) {
            return true; // TODO checken dat de tokens nog niet geinvalideerd zijn.
        } else {
            return false;
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

    public TwitterCache getTwitterCacheInstance() {
        if (twitterCacheInstance == null) {
            twitterCacheInstance = new TwitterCache(getTwitterInstance());
        }
        return twitterCacheInstance;
    }

    public AsyncTwitter getAsyncTwitterInstance() {
        if (asyncTwitterInstance == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true) // TODO disable logging for production
                    .setOAuthConsumerKey(TWITTER_KEY)
                    .setOAuthConsumerSecret(TWITTER_SECRET)
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(accessTokenSecret);

            AsyncTwitterFactory factory = new AsyncTwitterFactory(cb.build());
            asyncTwitterInstance = factory.getInstance();
        }
        return asyncTwitterInstance;
    }

    /**
     * Clears the current session by removing any access tokings and clearing the current Twiter instances.
     * This can be used to log the user out of the app.
     */
    public void clearSession(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ACCESSTOKEN, "");
        editor.putString(KEY_ACCESSTOKENSECRET, "");
        editor.putLong(KEY_USERID, 0);
        editor.commit();

        // TODO implement further
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

    public void setAccessToken(String accessToken, Context context) {
        this.accessToken = accessToken;
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ACCESSTOKEN, accessToken);
        editor.commit();
    }

    public void setAccessTokenSecret(String accessTokenSecret, Context context) {
        this.accessTokenSecret = accessTokenSecret;
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ACCESSTOKENSECRET, accessTokenSecret);
        editor.commit();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId, Context context) {
        this.userId = userId;
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(KEY_USERID, userId);
        editor.commit();
    }
}
