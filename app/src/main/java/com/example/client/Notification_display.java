package com.example.client;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.client.ModelClasses.Notification_Messages;
import com.example.client.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SimpleTimeZone;

public class Notification_display extends AppCompatActivity implements Notification_display_adapter.checked {
    String PUBGID;
    List<Notification_Messages> list;
    RecyclerView recyclerView;
    Notification_display_adapter adapter;
    TextView noMessageDisplay;
    ProgressBar progressBar;
   Snackbar snackbar;
   int count=0;
   boolean seen;
   List<String> keys;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                DeleteAllMessages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        setContentView(R.layout.activity_notification_display);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Notifications</font>"));
        Intent intent=getIntent();
        PUBGID=intent.getStringExtra("PUBGID");
        recyclerView=findViewById(R.id.recycler_view);
        progressBar=findViewById(R.id.progressBar);
        list=new ArrayList<>();
        keys=new ArrayList<>();
        noMessageDisplay=findViewById(R.id.messageDisplay);
            progressBar.setIndeterminate(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users_Client");
        databaseReference.child("Messages").child(PUBGID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.GONE);
                list.clear();
                count = (int) snapshot.getChildrenCount();
                System.out.println(snapshot.getKey());
        if(snapshot.exists()) {
            noMessageDisplay.setVisibility(View.GONE);
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Notification_Messages messages = dataSnapshot.getValue(Notification_Messages.class);
                System.out.println(dataSnapshot.getKey());
                messages.key = dataSnapshot.getKey();
                list.add(messages);
            }
        }

        else{
            noMessageDisplay.setVisibility(View.VISIBLE);

        }
                Collections.reverse(list);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(Notification_display.this);
                TransitionManager.beginDelayedTransition(recyclerView, new Fade(Fade.IN).setDuration(600));
                recyclerView.setLayoutManager(linearLayoutManager);
                View view=linearLayoutManager.getChildAt(20);
                System.out.println(view);
                adapter = new Notification_display_adapter(Notification_display.this, list, PUBGID, Notification_display.this,recyclerView,linearLayoutManager);
                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });






    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(count>0) {
            Intent intent1 = new Intent();
            System.out.println("on Support navigate " + (ArrayList<String>) keys);
            intent1.putStringArrayListExtra("keys", (ArrayList<String>) keys);

            setResult(RESULT_OK, intent1);
        }
        finishAfterTransition();
    }

    public void DeleteAllMessages(){
if(count>0) {
    new MaterialAlertDialogBuilder(Notification_display.this).setTitle(Html.fromHtml("<font color='#1c2e4a'><b>Warning</b></font>")).setMessage(Html.fromHtml("<font color='#1c2e4a'>Messages once deleted cannot be restored again.Are you sure want to delete the messages?</font>")).setPositiveButton("yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            ProgressDialog progressDialog = new ProgressDialog(Notification_display.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Deleting...");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            TransitionManager.beginDelayedTransition(recyclerView, new Slide(Gravity.START));
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users_Client").child("Messages");
            databaseReference1.child(PUBGID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    snackbar = Snackbar.make(findViewById(R.id.constraint_layout), "Failed to delete", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            });
        }
    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    }).show();
}
}
    @Override
    public boolean onSupportNavigateUp() {
        if(count>0) {
            Intent intent1 = new Intent();
            System.out.println("on Support navigate " + (ArrayList<String>) keys);
            intent1.putStringArrayListExtra("keys", (ArrayList<String>) keys);

            setResult(RESULT_OK, intent1);
        }
        finishAfterTransition();
        return  true;
    }

    @Override
    public void Checked(List<String> keys) {

        this.keys=keys;
        System.out.println("Keys in display"+keys);
    }
}