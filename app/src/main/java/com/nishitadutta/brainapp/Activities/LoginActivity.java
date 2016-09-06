package com.nishitadutta.brainapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.nishitadutta.brainapp.Global.Constants;
import com.nishitadutta.brainapp.R;
import com.nishitadutta.brainapp.Utils.SharedPreferenceUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.wangyuwei.particleview.ParticleView;

/**
 * Created by Nishita on 18-08-2016.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "LoginActivity";

    //Firebase
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Twitter
    @BindView(R.id.twitter_login_button)
    TwitterLoginButton loginButton;

    //Facebook
    static LoginManager loginManager;
    CallbackManager callbackManager;

    @BindView(R.id.btn_facebook_login)
    LoginButton btnFacebookLogin;

    @BindView(R.id.particle_view)
    ParticleView particleView;

    //Google
    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;
    @BindView(R.id.btn_google_login)
    SignInButton btnGoogleLogin;
    @OnClick(R.id.btn_google_login)
    public void googleSignIn(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.e(LOG_TAG, "Google Sign In button clicked");
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        particleView.startAnim();

        //Firebase
        mFireBaseAuth = FirebaseAuth.getInstance();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User signed in.
                    // Now, You can get UserId by calling firebase User.getUid() and save it in preference
                    //Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    //startActivity(intent);
                    //finish();

                } else {
                    // User signed out. Now, You can clear the preference.
                }
            }
        };

        //Twitter

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                handleTwitterSession(result.data);
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.FIREBASE_WEB_CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Facebook
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.setReadPermissions("email", "public_profile");
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("facebook login", "success");
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String name = object.optString("name");
                        String facebookUserId = object.optString("id");
                        if (!TextUtils.isEmpty(name)) {
                            SharedPreferenceUtils.getInstance(LoginActivity.this).setValue(Constants.USERNAME, name);
                        }
                        SharedPreferenceUtils.getInstance(LoginActivity.this).setValue(Constants.FACEBOOK_ID, facebookUserId);
                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }


            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Facebook Sign in failed. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Handle authentication failure
                            Log.e(LoginActivity.this.getLocalClassName(),"Authentication with firebase/google failed");
                        } else {
                            // Do whatever you want on successful authentication
                            Log.e(LoginActivity.this.getLocalClassName(),"Authentication with firebase/google success");
                            goToMain();
                        }
                    }
                });
    }

    private void goToMain() {
        Intent in=new Intent(this,MainActivity.class);
        startActivity(in);
        finish();
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            // Handle authentication failure
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(LoginActivity.this.getLocalClassName(), "Authentication with firebase/facebook failed");
                        } else {
                            // Do whatever you want on successful authentication
                            Log.e(LoginActivity.this.getLocalClassName(), "Authentication with firebase/facebook success");
                            goToMain();
                        }
                    }
                });
    }

    private void handleTwitterSession(TwitterSession session) {

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        mFireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential Twitter : onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.e(LOG_TAG, "signInWithCredential-Twitter", task.getException());
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            SharedPreferenceUtils.getInstance(LoginActivity.this).setValue("account", "twitter");
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Google
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                Log.d("google sign in", "successful");
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e("google sign in", "failed");
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }

        //Twitter
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onStart() {
        super.onStart();
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFireBaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
