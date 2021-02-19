package com.example.client;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import com.example.client.ModelClasses.User;
import com.example.client.ModelClasses.Transactions;
import com.example.client.ModelClasses.internet_receiver;
import com.example.client.Notification.Api;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Logged1 extends AppCompatActivity implements CustomModalSheetFragment.BottomSheetListener,internet_receiver.getConnection, ConnectionClassManager.ConnectionClassStateChangeListener,Notification_display_adapter.checked {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount acct, acct1;
    protected DrawerLayout drawer;
    String token_id;
    String PUBGID;
    BroadcastReceiver internet_receiver = null;
    LinearLayout linearLayout;
    TextView internet_check;
    int flag=0;
    private  String ref_code,ref_bal;
    TextView NotificationBadgeCounter;
    CountDownLatch countDownLatch;
    Animation shake;
    FrameLayout badge_layout;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.END));
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(getApplication());
        countDownLatch=new CountDownLatch(1);
        check();
        tokenId();

        setContentView(R.layout.activity_logged1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        linearLayout = findViewById(R.id.internet_layout);
        internet_check = findViewById(R.id.internet_check);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        shake= AnimationUtils.loadAnimation(this,R.anim.bell_shake);

        NavigationView navigationView = findViewById(R.id.nav_view);
        TransitionManager.beginDelayedTransition(navigationView,new Slide(Gravity.END));
        View hView = navigationView.inflateHeaderView(R.layout.nav_header_logged1);
        TextView Email = (TextView) hView.findViewById(R.id.email);
        TextView FullName = (TextView) hView.findViewById(R.id.full_name);
        CircleImageView img = (CircleImageView) hView.findViewById(R.id.imageView);
        String photoURI = acct.getPhotoUrl().toString();
        Glide.with(getApplication()).load(photoURI)
                .into(img);
        Email.setText(acct.getEmail());
        FullName.setText(acct.getDisplayName());


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_account, R.id.nav_wallet, R.id.nav_settings, R.id.nav_logout,R.id.nav_refer)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        internet_receiver = new internet_receiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internet_receiver, intentFilter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logged1, menu);
        MenuItem menuItem = menu.findItem(R.id.notifications);
        View view = MenuItemCompat.getActionView(menuItem);
        NotificationBadgeCounter = view.findViewById(R.id.notification_badge);
        badge_layout=view.findViewById(R.id.badge_layout);
        if(PUBGID!=null) {
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users_Client").child("Messages").child(PUBGID);
            databaseReference1.orderByChild("seen").equalTo(false).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    badge_layout.startAnimation(shake);
                    if (snapshot.exists()) {
                        if (NotificationBadgeCounter != null) {
                            if (snapshot.getChildrenCount() > 0) {

                                NotificationBadgeCounter.setVisibility(View.VISIBLE);
                                NotificationBadgeCounter.setText(String.valueOf(snapshot.getChildrenCount()));
                            } else {
                                NotificationBadgeCounter.setVisibility(View.GONE);
                            }
                        }

                    }
                    else{
                        NotificationBadgeCounter.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(menuItem);
                }
            });




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.notifications:
                if (PUBGID!=null) {
                    Intent intent = new Intent(this, Notification_display.class);
                    intent.putExtra("PUBGID", PUBGID);
                    startActivityForResult(intent, 1);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.main_layout),new AutoTransition());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void check() {

                        String Email = acct.getEmail();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                        Query query = databaseReference.orderByChild("email").equalTo(Email);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = null;
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        user = ds.getValue(User.class);
                                        ref_code = user.referral_code;
                                        PUBGID = user.PUBG_ID;




                                        assert user != null;
                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                                        User finalUser = user;
                                        databaseReference1.child(user.PUBG_ID).child("block").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue().equals("yes")) {
                                                    Intent intent = new Intent(Logged1.this, Blocked.class);
                                                    intent.putExtra("PUBGID", finalUser.PUBG_ID);
                                                    startActivity(intent);
                                                    finish();
                                                }


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Users_Client").child("Messages").child(PUBGID);
                                    databaseReference1.orderByChild("seen").equalTo(false).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if(snapshot.exists()) {
                                                badge_layout.startAnimation(shake);
                                                if(NotificationBadgeCounter!=null) {
                                                    if(snapshot.getChildrenCount()>0) {

                                                        NotificationBadgeCounter.setVisibility(View.VISIBLE);
                                                        NotificationBadgeCounter.setText(String.valueOf(snapshot.getChildrenCount()));
                                                    }
                                                    else{
                                                        NotificationBadgeCounter.setVisibility(View.GONE);
                                                    }
                                                }

                                            }
                                            else{
                                                if(NotificationBadgeCounter!=null) {
                                                 NotificationBadgeCounter.setVisibility(View.GONE);
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    if (!ref_code.equals("no")) {
                                        if (user.no_of_matches == 1) {
                                            if (user.referral_code_flag.equals("no")) {
                                                final Double[] newWalletMoney = new Double[1];
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                DocumentReference documentReference = db.collection("UsersBalance").document(ref_code);
                                                User finalUser2 = user;
                                                db.runTransaction(new Transaction.Function<Double>() {
                                                    @Nullable
                                                    @Override
                                                    public Double apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                        DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                                                        newWalletMoney[0] = documentSnapshot.getDouble("Balance") + 10;
                                                        transaction.update(documentReference, "Balance", newWalletMoney[0]);
                                                        return null;
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Double>() {
                                                    @Override
                                                    public void onSuccess(Double aDouble) {
                                                        final String[] token = new String[1];
                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users_Client");
                                                        databaseReference.child(ref_code).child("Balance").setValue(String.valueOf(newWalletMoney[0]));
                                                        databaseReference.child(finalUser2.PUBG_ID).child("referral_code_flag").setValue("yes");
                                                        databaseReference.child(ref_code).child("Token_Id").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                token[0] = dataSnapshot.getValue(String.class);
                                                                sendNotification(token[0], finalUser2.Name);
                                                                Date date1 = new Date();
                                                                SimpleDateFormat dateFormat;
                                                                dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                                                Transactions trans = new Transactions("10", dateFormat.format(date1), "0", "Referred by " + finalUser2.Name);
                                                                databaseReference1.child("Transactions").child(ref_code).push().setValue(trans);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                } else {


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });




    }
    public void sendNotification(String token_id,String name){
        String title="Amount of 10/- credited";
        String body=name+" referred your code 🤩👍";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://dbtry-c5fbe.web.app/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);
        Call<ResponseBody> call= api.sendNotification(token_id,title,body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                /*try {
                    assert response.body() != null;
                    Toast.makeText(getContext(),response.body().string(),Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onButtonClick(String text) {
        System.out.println(text);

    }
    public void tokenId() {
        flag=1;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {

                            token_id = task.getResult().getToken();

                            Log.d("Token", token_id);


                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                            DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Registered_Clients");
                            databaseReference.orderByChild("email").equalTo(acct.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                                User user=ds.getValue(User.class);
                                                databaseReference.child(user.PUBG_ID).child("Token_Id").setValue(token_id);
                                                databaseReference1.child(user.PUBG_ID).setValue(token_id);
                                            }
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            DatabaseReference databaseReference2= FirebaseDatabase.getInstance().getReference("Clients_tokens");
                            databaseReference2.child(acct.getId()).setValue(token_id);

                        }


                    }
                });

    }


    @Override
    public void getNoConnectionValue(String text) {
            TransitionManager.beginDelayedTransition(findViewById(R.id.main_layout),new AutoTransition());
            TransitionManager.beginDelayedTransition(linearLayout,new AutoTransition());
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout.setBackgroundColor(Color.RED);
            internet_check.setTextColor(Color.WHITE);
            internet_check.setText(text);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void getYesConnectionValue(String online, String text) {
        linearLayout.setBackgroundColor(Color.parseColor("#4b8b3b"));
        internet_check.setTextColor(Color.WHITE);
        internet_check.setText(online);

        blink();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);

       // Toast.makeText(this, String.valueOf(level), Toast.LENGTH_LONG).show();
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        Number downSpeed = nc.getLinkDownstreamBandwidthKbps();
        int upSpeed = nc.getLinkUpstreamBandwidthKbps();
        ConnectionClassManager.getInstance().register(this);
        ConnectionQuality cq = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internet_receiver);
    }

    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1500;
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run()
                    {
                        TransitionManager.beginDelayedTransition(linearLayout,new Slide(Gravity.BOTTOM));
                        TransitionManager.beginDelayedTransition(findViewById(R.id.main_layout),new AutoTransition());
                        linearLayout.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBandwidthStateChange(ConnectionQuality bandwidthState) {

    }

    @Override
    public void Checked(List<String> keys) {
        System.out.println("in checked");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("Users_Client").child("Messages").child(PUBGID);
        if(requestCode==1){
            if(resultCode==RESULT_OK){

                ArrayList<String> keys = new ArrayList<>();
                keys.clear();
                keys=data.getStringArrayListExtra("keys");
                System.out.println("keys in logged one"+keys);
                for(int i=0;i<keys.size();i++){
                    databaseReference1.child(keys.get(i)).child("seen").setValue(true);
                }
            }
        }
    }
}

