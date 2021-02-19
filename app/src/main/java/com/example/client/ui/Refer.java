package com.example.client.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.client.ModelClasses.User;
import com.example.client.PinVerification;
import com.example.client.R;
import com.example.client.UserValues;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Refer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Refer extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    TextView rules;
    View root;

    private String Users_PUBGID;
    public Refer() {

    }


    public static Refer newInstance(String param1, String param2) {
        Refer fragment = new Refer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       root=inflater.inflate(R.layout.fragment_refer, container, false);
        String stringRules=getString(R.string.refer_instructions);
        String[] arr =stringRules.split("\n");
        int bulletGap=(int) dp(5);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        rules=root.findViewById(R.id.rules);
        for (int i = 0; i < arr.length; i++) {
            String line = arr[i];
            SpannableString ss = new SpannableString(line);
            ss.setSpan(new BulletSpan(bulletGap), 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);

            //avoid last "\n"
            if(i+1<arr.length)
                ssb.append("\n");

        }
        SpannableString spannableString=new SpannableString("Instructions:\n");
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,spannableString.length(),SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        rules.setText(spannableString);
        rules.append(ssb);


        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(root.getContext());
        this.setEnterTransition(new AutoTransition());
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
        databaseReference.orderByChild("email").equalTo(account.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = null;
                if(dataSnapshot.exists()) {
                    System.out.println(dataSnapshot.getValue());
                    TransitionManager.beginDelayedTransition(root.findViewById(R.id.layout),new Fade(Fade.IN));
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        user = ds.getValue(User.class);
                    }
                    System.out.println("data" + user.email);
                    Users_PUBGID = user.PUBG_ID;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
                root.findViewById(R.id.refer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(root.getContext(), Users_PUBGID, Toast.LENGTH_LONG).show();
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TITLE, "SR Gamerz");
                        share.putExtra(Intent.EXTRA_TEXT, "Hey buddy! 😀 check out this new application where you can earn lots of money by playing PUBG \n use my referral code " + Users_PUBGID +".\n"+"So install it and let us earn together.💰🤑" +"\n"+"https://drive.google.com/file/d/1SCR3GyGz3UF8YMuBocRHQms1u6wN8gfw/view?usp=sharing");
                        startActivity(Intent.createChooser(share, "Select  the app to share SRGamerz"));
                      /* ApplicationInfo app = root.getContext().getApplicationInfo();
                        String filePath =app.publicSourceDir;

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("application/vnd.android.com.example.client-archive");


                        // Append file and send Intent
                        intent.putExtra(Intent.EXTRA_STREAM,  FileProvider.getUriForFile(root.getContext().getApplicationContext(), root.getContext().getPackageName()+".fileprovider", new File(filePath)));
                        startActivity(Intent.createChooser(intent, "Share app via"));*/
                    }

                });


        return root;
    }

    private float dp(int dp) {
        return getResources().getDisplayMetrics().density * dp;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                Toast.makeText(root.getContext(), data.getStringExtra("hello"), Toast.LENGTH_SHORT).show();
            }
        }
    }
}