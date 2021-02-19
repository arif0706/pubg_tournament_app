package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class PasswordRegistration extends AppCompatActivity {

    GoogleSignInAccount account;
    FirebaseAuth auth;
    String pass,re_pass;
    TextInputLayout password,re_entered_password,oldUsersPassword;
    ConstraintLayout layout;
    Toast toast;
    ProgressIndicator progressIndicator;
    ProgressDialog progressDialog;
    TextView welcomeText;

    FrameLayout newUsersFrame,oldUsersFrame,progressFrame;
    Animation fade,shake;

    CheckBox conditionCheck;


    Button login,register,forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>SR Gamerz</font>"));
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_password_registration);
        account = GoogleSignIn.getLastSignedInAccount(this);
        password=findViewById(R.id.new_password);
        re_entered_password=findViewById(R.id.confirm_password);
        auth=FirebaseAuth.getInstance();
        layout=findViewById(R.id.layout);
        progressIndicator=findViewById(R.id.progressIndicator);

        login=findViewById(R.id.login);
        register=findViewById(R.id.submit);
        forgotPassword=findViewById(R.id.forgorPassword);

        welcomeText=findViewById(R.id.welcomeText);

        conditionCheck=findViewById(R.id.conditionCheck);

        fade= AnimationUtils.loadAnimation(this,R.anim.search_result);
        shake=AnimationUtils.loadAnimation(this,R.anim.incorrect);

        newUsersFrame=findViewById(R.id.newUsersFrame);
        oldUsersFrame=findViewById(R.id.oldUsersFrame);
        progressFrame=findViewById(R.id.progressFrame);
        oldUsersPassword=findViewById(R.id.oldUsersPassword);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (toast!=null){
                    toast.cancel();
                    toast=null;
                }

                return false;
            }
        });

        progressDialog =new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in...");
        progressDialog.setIndeterminate(true);

        findViewById(R.id.newUsersFrame).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (toast!=null){
                    toast.cancel();
                    toast=null;
                }
                return false;
            }
        });
        findViewById(R.id.oldUsersFrame).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (toast!=null){
                    toast.cancel();
                    toast=null;
                }
                return false;
            }
        });

        conditionCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    register.setEnabled(true);

                }
                else {
                    register.setEnabled(false);

                }
            }
        });

        auth.createUserWithEmailAndPassword(account.getEmail(),"123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {


                        TransitionManager.beginDelayedTransition(oldUsersFrame,new AutoTransition().setDuration(3000));
                        oldUsersFrame.startAnimation(fade);
                        welcomeText.setText("Welcome "+account.getDisplayName()+",");

                        progressFrame.setVisibility(View.GONE);
                        oldUsersFrame.setVisibility(View.VISIBLE);
                        newUsersFrame.setVisibility(View.GONE);

                    }
                }
                else{
                         FirebaseUser user1=FirebaseAuth.getInstance().getCurrentUser();
                         user1.delete();
                        progressFrame.setVisibility(View.INVISIBLE);
                        newUsersFrame.setVisibility(View.VISIBLE);
                        oldUsersFrame.setVisibility(View.GONE);

                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(PasswordRegistration.this).setTitle("Forgot password").setMessage("A password reset link will be generated and sent to this mail")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auth.sendPasswordResetEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            toast = Toast.makeText(PasswordRegistration.this, "Email sent", Toast.LENGTH_SHORT);
                                        }
                                        else{
                                            toast=Toast.makeText(PasswordRegistration.this,task.getException().getMessage(),Toast.LENGTH_LONG);
                                        }
                                        toast.show();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false)
                        .show();
            }
        });

        oldUsersPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!oldUsersPassword.getEditText().getText().toString().equals("")){
                    oldUsersPassword.setError(null);
                }


            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!oldUsersPassword.getEditText().getText().toString().equals("")) {
                    progressDialog.show();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(oldUsersPassword.getWindowToken(), 0);
                    loggingIn(oldUsersPassword.getEditText().getText().toString());
                }
                else{
                    oldUsersPassword.setError("Enter the password!");
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Registering...");

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                pass=password.getEditText().getText().toString();
                re_pass=re_entered_password.getEditText().getText().toString();
                if(!pass.equals("")&&!re_pass.equals("")&&pass.length()>=6) {
                    if (pass.equals(re_pass)) {
                        progressDialog.show();
                       auth.createUserWithEmailAndPassword(account.getEmail(),pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordRegistration.this, "Account created", Toast.LENGTH_SHORT).show();
                            }
                           }
                       });

                    } else {
                       toast= Toast.makeText(getApplicationContext(), "Password mismatch!", Toast.LENGTH_SHORT);

                                toast.show();
                    }
                }
                else{

                    toast=Toast.makeText(getApplicationContext(),"Invalid passwords!",Toast.LENGTH_SHORT);
                            toast.show();

                }
            }
        });

    }

    private void loggingIn(String password) {
        auth.signInWithEmailAndPassword(account.getEmail(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                   Intent intent=new Intent(PasswordRegistration.this,Logged1.class);
                   startActivity(intent);
                   finish();
                }
                else{
                    if(task.getException().getMessage().equals("The password is invalid or the user does not have a password.")){
                        oldUsersPassword.startAnimation(shake);
                        oldUsersPassword.setError("Incorrect password!");
                    }
                    else{
                        Toast.makeText(PasswordRegistration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}