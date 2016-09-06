package com.nishitadutta.brainapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.nishitadutta.brainapp.R;
import com.nishitadutta.brainapp.Utils.SharedPreferenceUtils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "JLJE1Vqrb7dktIXCXlqTZTQ0I";
    private static final String TWITTER_SECRET = "OR4EZ46xhRB7Rkn3fXTdhhA8lOQiopgopLBtkQFlc51O02YetL";


    public static final int RC_SIGN_IN=9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        //setContentView(R.layout.activity_main);
        FirebaseAuth auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null) {

            Log.e(this.getLocalClassName(), "Not logged in");
            Intent in = new Intent(this, LoginActivity.class);
            startActivity(in);
            finish();
        }
        else{
            Log.e(this.getLocalClassName(), "logged in");
            Intent in= new Intent(this, ChatActivity_.class);
            startActivity(in);
            finish();
        }


        /*else if (!SharedPreferenceUtils.getInstance(this).getStringValue("googleId", "").equals("")) {
            intent = new Intent(this, ChatActivity_.class);
            startActivity(intent);
            finish();
        } else if (!SharedPreferenceUtils.getInstance(this).getStringValue("facebookUserId", "").equals("")) {
            intent = new Intent(this, ChatActivity_.class);
            startActivity(intent);
            finish();
        } else if (!SharedPreferenceUtils.getInstance(this).getStringValue("twitterUserId", "").equals("")) {
            intent = new Intent(this, ChatActivity_.class);
            startActivity(intent);
            finish();
        }
        else {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }*/
    }

}
