package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class pinRegistration extends AppCompatActivity  implements View.OnClickListener {
    TextInputLayout newPin,reEnteredPin;
    GoogleSignInAccount account;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    String newPinString,reEnteredPinString;

    String PUBGID,message;

    EditText password;
        TextInputLayout alertPassword;


    Toast toast;
    TextView displayMessage;

    ProgressDialog progressDialog;

    Button zero, one,two,three,four,five,six,seven,eight,nine;
    ImageButton delete,ok;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Wallet pin</font>"));
        setContentView(R.layout.activity_pin_registration);
        newPin=findViewById(R.id.newPin);
        reEnteredPin=findViewById(R.id.reEnteredPin);
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();
        account= GoogleSignIn.getLastSignedInAccount(getApplication());
        newPin.getEditText().setTransformationMethod(new bigbubbleTransformation());
        reEnteredPin.getEditText().setTransformationMethod(new bigbubbleTransformation());


        newPin.getEditText().setShowSoftInputOnFocus(false);
        reEnteredPin.getEditText().setShowSoftInputOnFocus(false);

        newPin.getEditText().requestFocus();

        displayMessage=findViewById(R.id.message);


        Intent intent=getIntent();
        PUBGID=intent.getStringExtra("PUBGID");
        message=intent.getStringExtra("messageToDisplay");

        one=findViewById(R.id.one);
        two=findViewById(R.id.two);
        three=findViewById(R.id.three);
        four=findViewById(R.id.four);
        five=findViewById(R.id.five);
        six=findViewById(R.id.six);
        seven=findViewById(R.id.seven);
        eight=findViewById(R.id.eight);
        nine=findViewById(R.id.nine);
        zero=findViewById(R.id.zero);

        ok=findViewById(R.id.ok);
        delete=findViewById(R.id.delete);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);

        delete.setOnClickListener(this);

        if(message!=null){
            displayMessage.setText(message);
        }

        View view= LayoutInflater.from(pinRegistration.this).inflate(R.layout.password_entry,null);
        alertPassword=view.findViewById(R.id.dialogPassword);
        AlertDialog.Builder builder=new AlertDialog.Builder(pinRegistration.this);
        builder.setTitle("Password");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok",null);
        builder.setNegativeButton("Cancel",null);
        AlertDialog dialog = builder.create();
        alertPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    if(!alertPassword.getEditText().getText().toString().equals("")){
                        alertPassword.setError(null);
                    }
            }
        });

        progressDialog=new ProgressDialog(pinRegistration.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);



        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positive=((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                Button negative=((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String alertPasswordString = alertPassword.getEditText().getText().toString();
                        if (!alertPasswordString.equals("")) {
                            dialog.dismiss();
                            progressDialog.show();
                            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), alertPasswordString);
                            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(newPin.getEditText().getWindowToken(), 0);

                                    if (task.isSuccessful()) {
                                       createPin();
                                    } else {
                                        if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                           toast= Toast.makeText(pinRegistration.this, "Incorrect password", Toast.LENGTH_SHORT);
                                        } else {
                                            toast=Toast.makeText(pinRegistration.this, task.getException().getMessage(), Toast.LENGTH_SHORT);
                                        }
                                        toast.show();
                                    }

                                }
                            });
                        }
                        else{
                            alertPassword.setError("Enter a password!");
                        }
                    }
                });
            }
        });
       ok.setImageResource(R.drawable.ic_baseline_keyboard_tab_24);
        newPin.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               if(newPin.getEditText().hasFocus()){
                  ok.setImageResource(R.drawable.ic_baseline_keyboard_tab_24);
               }
               else{
                   ok.setImageResource(R.drawable.ic_baseline_check_24);
               }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().requestFocus();
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    newPinString=newPin.getEditText().getText().toString();
                    reEnteredPinString=reEnteredPin.getEditText().getText().toString();

                    if(!newPinString.equals("")&&!reEnteredPinString.equals("")){
                        if(newPinString.length()==4) {
                            if (newPinString.equals(reEnteredPinString)) {
                                dialog.show();
                            }

                            else {
                                toast = Toast.makeText(pinRegistration.this, "Pin mismatch!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        else {
                            toast=Toast.makeText(pinRegistration.this,"Length of pin must be 4!",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    else{

                        toast=Toast.makeText(pinRegistration.this,"Fields are empty!",Toast.LENGTH_SHORT);
                        toast.show();

                    }


                }
            }
        });

        findViewById(R.id.layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(toast!=null){
                    toast.cancel();
                    toast=null;
                }
                return false;
            }
        });
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(newPin.getEditText().hasFocus()) {
                    if (!newPin.getEditText().getText().toString().equals(""))
                    {
                        newPin.getEditText().setText("");
                    }
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    if(!reEnteredPin.getEditText().getText().toString().equals("")){
                        reEnteredPin.getEditText().setText("");
                    }
                }
                return false;
            }
        });

    }

    private void createPin(){

        DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
        databaseReference1.child(PUBGID).child("pin").setValue(newPinString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    toast=Toast.makeText(pinRegistration.this,"pin updated!",Toast.LENGTH_SHORT);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("message","1");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
                else{
                    toast=Toast.makeText(pinRegistration.this,task.getException().getMessage(),Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("1");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("1");
                }
                break;
            case R.id.two:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("2");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("2");
                }
                break;
            case R.id.three:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("3");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("3");
                }
                break;
            case R.id.four:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("4");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("4");
                }
                break;
            case R.id.five:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("5");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("5");
                }
                break;
            case R.id.six:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("6");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("6");
                }
                break;
            case R.id.seven:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("7");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("7");
                }
                break;
            case R.id.eight:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("8");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("8");
                }
                break;
            case R.id.nine:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("9");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("9");
                }
                break;
            case R.id.zero:
                if(newPin.getEditText().hasFocus()){
                    newPin.getEditText().append("0");
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    reEnteredPin.getEditText().append("0");
                }
                break;
            case R.id.delete :
                if(newPin.getEditText().hasFocus()) {
                    String text = newPin.getEditText().getText().toString();
                    if (!text.equals("")) {
                        text = text.substring(0, text.length() - 1);
                        newPin.getEditText().setText(text);
                    }
                }
                else if(reEnteredPin.getEditText().hasFocus()){
                    String text=reEnteredPin.getEditText().getText().toString();
                    if(!text.equals("")){
                        text=text.substring(0,text.length()-1);
                        reEnteredPin.getEditText().setText(text);
                    }
                }
                break;
        }

    }
}