package com.nishitadutta.brainapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.nishitadutta.brainapp.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "JLJE1Vqrb7dktIXCXlqTZTQ0I";
    private static final String TWITTER_SECRET = "OR4EZ46xhRB7Rkn3fXTdhhA8lOQiopgopLBtkQFlc51O02YetL";


    public static final int RC_SIGN_IN=9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        FirebaseAuth auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
            startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setProviders(
                                AuthUI.GOOGLE_PROVIDER,
                                AuthUI.FACEBOOK_PROVIDER)
                        .build(),
                RC_SIGN_IN);

    }
}
