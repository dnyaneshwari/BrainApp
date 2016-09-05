package com.nishitadutta.brainapp.Activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.nishitadutta.brainapp.Global.Constants;
import com.nishitadutta.brainapp.Objects.BrainResponse;
import com.nishitadutta.brainapp.R;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.SupposeBackground;

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
public class ChatActivity extends AppCompatActivity {

    FirebaseUser user;
    OkHttpClient client = new OkHttpClient();

    SlyceMessagingFragment slyceMessagingFragment;

    @FragmentById(R.id.messaging_fragment)
    void setMessagingFragment(SlyceMessagingFragment slyceMessagingFragment) {
        String photoUrl = new String();
        this.slyceMessagingFragment = slyceMessagingFragment;
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
                if(userId=="ROBO"){
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
            textMessage.setText(brainResponse.getCnt());
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
}
