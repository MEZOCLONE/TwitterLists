package com.tierep.twitterlists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tierep.twitterlists.ui.ListActivity;
import com.tierep.twitterlists.ui.LoginActivity;


public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Session mySession = Session.getInstance();
        boolean result = mySession.initialize(this);

        if (result) {
            Intent intentListActivity = new Intent(this, ListActivity.class);
            startActivity(intentListActivity);
        } else {
            Intent intentLoginActivity = new Intent(this, LoginActivity.class);
            startActivity(intentLoginActivity);
        }
        finish();
    }
}
