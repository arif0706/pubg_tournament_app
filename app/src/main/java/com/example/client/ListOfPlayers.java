package com.example.client;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import com.example.client.ModelClasses.MatchList;
import com.example.client.ModelClasses.Players;
import com.example.client.ModelClasses.internet_receiver;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListOfPlayers extends AppCompatActivity implements Players_list_adapter.onPlayerListener, internet_receiver.getConnection {
    List<Players> list;
    int counter;
    RecyclerView recyclerView;
    Players_list_adapter players_list_adapter;
    DatabaseReference databaseReference;
    String match_id,email;
    MaterialCardView ROOM_PASSWORD_VIEW;
    TextView roomid,password,rules,prize,count;
    ImageButton room_id_button,password_button;
    ImageView imageView;
    GoogleSignInAccount account;
    ExtendedFloatingActionButton timer;
    int flag=0;
    long diff;
    BroadcastReceiver internet_receiver = null;

    LinearLayout internet_layout;
    TextView internet_check;

    @Override
    public boolean onSupportNavigateUp() {
        finishAfterTransition();
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_players);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent=getIntent();
        timer=findViewById(R.id.timer);
        TransitionManager.beginDelayedTransition(findViewById(R.id.app_bar),new AutoTransition());

        internet_layout=findViewById(R.id.internet_layout);
        internet_check=findViewById(R.id.internet_check);

        count=findViewById(R.id.count);

        String time=intent.getStringExtra("time");
        String time2;

        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy kk:mm");
        Date obj2=new Date();
        time2=sdf.format(obj2);

        try {
            Date obj3=sdf.parse(time2);
            Date obj1=sdf1.parse(time);
            diff= obj1.getTime() - obj3.getTime();
            System.out.println("difference between milliseconds: " + diff);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calculateTime(diff);
        account= GoogleSignIn.getLastSignedInAccount(this);
        match_id=intent.getStringExtra("match_id");
        email=intent.getStringExtra("email");
        rules=findViewById(R.id.rules);
        recyclerView=findViewById(R.id.users_list);
        ROOM_PASSWORD_VIEW=findViewById(R.id.card);
        room_id_button=findViewById(R.id.copy_room);
        password_button=findViewById(R.id.copy_password);
        roomid=findViewById(R.id.room_id);
        password=findViewById(R.id.password);
        imageView=findViewById(R.id.imageView2);
        prize=findViewById(R.id.prize);

        internet_receiver = new internet_receiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internet_receiver, intentFilter);



        String stringRules=getString(R.string.Rules);
        String[] arr =stringRules.split("\n");
        int bulletGap=(int) dp(5);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String line = arr[i];
            SpannableString ss = new SpannableString(line);
            ss.setSpan(new BulletSpan(bulletGap), 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);

            //avoid last "\n"
            if(i+1<arr.length)
                ssb.append("\n");

        }
        SpannableString spannableString=new SpannableString("Match rules:\n");
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,spannableString.length(),SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        rules.setText(spannableString);
        rules.append(ssb);




        room_id_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager=(ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(roomid.getText().toString());
                Toast.makeText(ListOfPlayers.this, "Room Id copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager=(ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(password.getText().toString());
                Toast.makeText(ListOfPlayers.this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference("Matches_Going");
        list=new ArrayList<>();



        databaseReference.child(match_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    System.out.println(ds.getValue());
                    Players players=ds.getValue(Players.class);
                    list.add(players);

                }
                recyclerView.setLayoutManager(new LinearLayoutManager(ListOfPlayers.this));
                players_list_adapter = new Players_list_adapter(ListOfPlayers.this, list,ListOfPlayers.this);
                recyclerView.setAdapter(players_list_adapter);
                SpannableString spannableString1=new SpannableString("List of players joined:");
                spannableString1.setSpan(boldSpan,0,spannableString1.length(),SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                count.setText(spannableString1);
                count.append("   "+String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child(match_id).orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Matches");
                    databaseReference.child(match_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            MatchList matchList=dataSnapshot.getValue(MatchList.class);
                            //for (DataSnapshot ds:dataSnapshot.getChildren()){
                             //  matchList =ds.getValue(MatchList.class);
                           // }
                            findViewById(R.id.textView15).setVisibility(View.VISIBLE);
                            prize.setText(matchList.fprize+"/-");
                            blink();

                            ROOM_PASSWORD_VIEW.setVisibility(View.VISIBLE);
                            if(matchList.roomId.equals("")&&matchList.password.equals("")){
                                roomid.setText("-----");
                                password.setText("-----");
                                room_id_button.setEnabled(false);
                                password_button.setEnabled(false);
                            }
                            else {

                                roomid.setText(matchList.roomId);
                                password.setText(matchList.password);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    rules.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private float dp(int dp) {
        return getResources().getDisplayMetrics().density * dp;
    }

    @Override
    public void OnPlayerClick(int position) {

    }
    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.prize);
                        if(txt.getVisibility() == View.VISIBLE){
                            txt.setVisibility(View.INVISIBLE);
                        }else{
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    public void calculateTime(long diff){

        new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String hour,mins,seconds;
                hour=Long.toString((long) (millisUntilFinished/3.6e+6));
                if (Integer.parseInt(hour)<10){
                    hour="0"+hour;
                }
                mins=Long.toString(millisUntilFinished/60000);

                if(Integer.parseInt(mins)>=60||Integer.parseInt(mins)<=60){
                    mins=Integer.toString(Integer.parseInt(mins)%60);
                    if(Integer.parseInt(mins)<10){
                        mins="0"+mins;
                    }
                }
                seconds=Long.toString((millisUntilFinished%60000)/1000);
                if (Integer.parseInt(seconds)<10){
                    seconds="0"+seconds;
                }
               timer.setText(hour+":"+mins+":"+seconds);


            }

            @Override
            public void onFinish() {
                timer.setText("Match has Started");

            }
        }.start();

    }


    @Override
    public void getNoConnectionValue(String text) {

        TransitionManager.beginDelayedTransition(internet_layout,new Slide(Gravity.TOP));
            internet_layout.setVisibility(View.VISIBLE);
            internet_layout.setBackgroundColor(Color.RED);
            internet_check.setTextColor(Color.WHITE);
            internet_check.setText(text);
            flag=0;

    }

    @Override
    public void getYesConnectionValue(String online, String text) {
        TransitionManager.beginDelayedTransition(internet_layout,new Slide(Gravity.BOTTOM));
            internet_layout.setVisibility(View.INVISIBLE);
    }

    private void internet_display() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 5000;
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception ignored) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        internet_layout.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

}
