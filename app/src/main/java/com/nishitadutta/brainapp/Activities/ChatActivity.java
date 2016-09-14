package com.nishitadutta.brainapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.nishitadutta.brainapp.Global.Constants;
import com.nishitadutta.brainapp.Objects.BrainResponse;
import com.nishitadutta.brainapp.R;
import com.twitter.sdk.android.Twitter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import java.io.IOException;
import java.util.Date;

import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.UserClicksAvatarPictureListener;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Nishita on 04-09-2016.
 */
@EActivity(R.layout.activity_chat)
@OptionsMenu(R.menu.action_bar)
public class ChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth auth;
    private static final String TAG="ChatActivity";

    @AfterInject
    void afterBind(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_large);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @OptionsItem(R.id.logout)
    void logOut() {
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        logOutFromFacebook();
        logOutFromTwitter();
    //    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
      signOutFromGoogle();
        goToMain();
        //logout code
    }

    private void logOutFromFacebook() {
        FacebookSdk.sdkInitialize(this);
        LoginManager.getInstance().logOut();
    }

    private void signOutFromGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.FIREBASE_WEB_CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        if(mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(TAG, "onResult: Signed out from google");
                        }
                    });
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void logOutFromTwitter() {
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
        //
    }
    //rest of app


    FirebaseUser user;
    OkHttpClient client = new OkHttpClient();

    SlyceMessagingFragment slyceMessagingFragment;

    @FragmentById(R.id.messaging_fragment)
    void setMessagingFragment(SlyceMessagingFragment slyceMessagingFragment) {
        String photoUrl = new String();
        this.slyceMessagingFragment = slyceMessagingFragment;
        this.slyceMessagingFragment.setStyle(R.style.SliceTheme);
        user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            photoUrl = user.getPhotoUrl().toString();
        } catch (NullPointerException ne) {
            Log.e(this.getLocalClassName(), "photo url null");
            //TODO: Madhuri, set a default avatar url
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.slyceMessagingFragment.setDefaultAvatarUrl(photoUrl);
        this.slyceMessagingFragment.setDefaultDisplayName(user.getDisplayName());
        this.slyceMessagingFragment.setDefaultUserId(user.getUid());
        this.slyceMessagingFragment.setOnSendMessageListener(new UserSendsMessageListener() {
            @Override
            @Background
            public void onUserSendsTextMessage(String text) {
                String url = Constants.BRAIN_API_URL.replace("[uid]", user.getUid()).replace("[msg]", text);
                Log.e(ChatActivity.this.getLocalClassName(), "Inside run");
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                addMessage(request);

            }

            @Override
            public void onUserSendsMediaMessage(Uri imageUri) {

            }
        });
        this.slyceMessagingFragment.setUserClicksAvatarPictureListener(new UserClicksAvatarPictureListener() {
            @Override
            public void userClicksAvatarPhoto(String userId) {
                if (userId == "ROBO") {
                    Toast.makeText(ChatActivity.this, "Photo clicked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Background
    void addMessage(Request request) {
        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            Gson gson = new Gson();
            BrainResponse brainResponse = gson.fromJson(json, BrainResponse.class);
            TextMessage textMessage = new TextMessage();
            textMessage.setText(brainResponse.getCnt().toString());
            textMessage.setDate(new Date().getTime());
            textMessage.setDisplayName("Manisha");
            textMessage.setUserId("ROBO");
            textMessage.setAvatarUrl("https://lh3.googleusercontent.com/-Y86IN-vEObo/AAAAAAAAAAI/AAAAAAAKyAM/6bec6LqLXXA/s0-c-k-no-ns/photo.jpg");
            textMessage.setSource(MessageSource.EXTERNAL_USER);


            slyceMessagingFragment.addNewMessage(textMessage);
        } catch (IOException ioe) {
            System.out.print(ioe.toString());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
