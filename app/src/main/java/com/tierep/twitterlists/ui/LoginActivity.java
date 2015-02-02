package com.tierep.twitterlists.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;


public class LoginActivity extends Activity {

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getActionBar() != null) {
            getActionBar().hide();
        }

        Session mySession = Session.getInstance();
        mySession.initialize(this);

        final TwitterAuthConfig authConfig = new TwitterAuthConfig(mySession.getTWITTER_KEY(), mySession.getTWITTER_SECRET());

        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();

                Session mySession = Session.getInstance();
                mySession.setAccessToken(authToken.token);
                mySession.setAccessTokenSecret(authToken.secret);
                mySession.setUserId(session.getUserId());

                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(LoginActivity.this, "Login failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
