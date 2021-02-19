package com.example.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.client.ModelClasses.internet_receiver;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import static com.example.client.StartScreen.ANIM_ITEM_DURATION;
import static com.example.client.StartScreen.ITEM_DELAY;
import static com.example.client.StartScreen.STARTUP_DELAY;


public class MainActivity extends AppCompatActivity implements internet_receiver.getConnection {
    FirebaseAuth mAuth;
    Button register;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton SignInGoogle;
    BroadcastReceiver internet_receiver=null;
    int internet_check=0;
    GoogleSignInOptions gso;

    ImageView layout;
    public static final String CHANNEL_ID="SR Gamerz";
    public static final String CHANNEL_NAME="SR Gamerz";
    public static final String CHANNEL_DESC="SR Gamerz notification";
    GoogleSignInAccount account;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
         getSupportActionBar().hide();

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        TransitionManager.beginDelayedTransition(findViewById(R.id.context),new AutoTransition());
        TransitionManager.endTransitions(findViewById(R.id.context));
        register=findViewById(R.id.register);
        layout=findViewById(R.id.img_logo);
        Intent intent=new Intent();
        FirebaseMessaging.getInstance().subscribeToTopic("weather");



        SignInGoogle=findViewById(R.id.signInGoogle);
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setVibrationPattern(new long[0]);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        SignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                googleSignIn();
            }
        });

        tokenId();
        mAuth=FirebaseAuth.getInstance();
        internet_receiver = new internet_receiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internet_receiver, intentFilter);

       // animate();



    }


    void animate(){
        ViewGroup container = (ViewGroup) findViewById(R.id.context);

        ViewCompat.animate(layout)
                .translationY(-250)
                .setStartDelay(STARTUP_DELAY)
                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();

        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (!(v instanceof Button)) {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50).alpha(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(1000);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(500);
            }

            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }
    }
    public void googleSignIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internet_receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);




            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            if(internet_check==1) {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                Intent intent = new Intent(MainActivity.this, PasswordRegistration.class);
                startActivity(intent);
                finish();
            }
            else {
                Snackbar.make(findViewById(R.id.context),"No internet connection",Snackbar.LENGTH_SHORT).show();
            }

        } catch (ApiException e) {

            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());

        }
    }

    public void tokenId()
    {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Token Error", "getInstanceId failed", task.getException());
                            return;
                        }


                        String token = task.getResult().getToken();

                        Log.d("Token", token);

                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();

        account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser user=mAuth.getCurrentUser();
        //Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();

               if (user != null) {
                    Intent intent = new Intent(MainActivity.this, Logged1.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    SignInGoogle.setVisibility(View.VISIBLE);
                    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                    mGoogleSignInClient.signOut();
                }






    }
    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }

    @Override
    public void getNoConnectionValue(String text) {
        internet_check=0;

    }

    @Override
    public void getYesConnectionValue(String online, String text) {
            internet_check=1;
    }
}