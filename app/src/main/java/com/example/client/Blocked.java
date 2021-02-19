package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Blocked extends AppCompatActivity {
    TextView email;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked);
        email=findViewById(R.id.email_click);
        Intent intent=getIntent();
        String PUBGID=intent.getStringExtra("PUBGID");
        getSupportActionBar().hide();
        GoogleSignInAccount     acct = GoogleSignIn.getLastSignedInAccount(getApplication());
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
        databaseReference.child(PUBGID).child("block").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("no")) {
                    Intent intent=new Intent(Blocked.this,Logged1.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",(String) email.getText(), null));
                intent.putExtra(Intent.EXTRA_EMAIL,email.getText());
                //intent.putExtra(Intent.EXTRA_CC, "");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Unblocking of account");
                intent.putExtra(Intent.EXTRA_TEXT,"My account regarding this email has been blocked.I request you to unblock my account.");
                startActivity(Intent.createChooser(intent,"Send email"));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
               GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(Blocked.this, gso);
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Blocked.this);
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(Blocked.this, new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog=new ProgressDialog(Blocked.this);
                                progressDialog.setMessage("Logging out");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setIndeterminate(true);
                                progressDialog.show();
                                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }
}