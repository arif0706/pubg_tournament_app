package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChange extends AppCompatActivity {

    TextInputLayout currentPassword,newPassword,reEnteredPassword;
    Button currentPasswordSubmit,newPasswordSubmit;
    ScrollView newPasswordLayout;

     FirebaseUser firebaseUser;
     FirebaseAuth auth;
     ProgressBar load;

     TextView forgotPassword;

     String currentPasswordString,newPasswordString,reEnteredPasswordString;

     ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Account password</font>"));
        setContentView(R.layout.activity_password_change);

        currentPassword=findViewById(R.id.current_password);
        newPassword=findViewById(R.id.newPassword);
        reEnteredPassword=findViewById(R.id.reEnteredPassword);

        currentPasswordSubmit=findViewById(R.id.currentPasswordSubmit);
        newPasswordSubmit=findViewById(R.id.newPasswordSubmit);

        newPasswordLayout=findViewById(R.id.passwordChangeLayout);

        forgotPassword=findViewById(R.id.forgotPassword);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating...");
        progressDialog.setIndeterminate(true);

        load=findViewById(R.id.load);

        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();




        currentPasswordSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentPassword.getEditText().getText().toString().equals("")){
                    currentPassword.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
                    currentPassword.getEditText().setSelection(currentPassword.getEditText().getText().toString().length());
                    load.setVisibility(View.VISIBLE);
                    currentPasswordString=currentPassword.getEditText().getText().toString();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentPassword.getEditText().getWindowToken(), 0);
                    AuthCredential authCredential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),currentPassword.getEditText().getText().toString());
                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                TransitionManager.beginDelayedTransition(currentPassword,new AutoTransition());
                                currentPassword.setEnabled(false);
                                currentPassword.setEndIconDrawable(R.drawable.ic_baseline_check_circle_outline_24);
                                newPasswordLayout.setVisibility(View.VISIBLE);
                                currentPasswordSubmit.setVisibility(View.GONE);
                            }
                            else{
                                currentPassword.setError("Incorrect password!");
                            }
                            load.setVisibility(View.GONE);
                        }
                    });
                }
                else{
                    currentPassword.setError("Enter a password!");
                }
            }
        });

        currentPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!currentPassword.getEditText().getText().toString().equals("")){
                    currentPassword.setError(null);
                }

            }
        });


        newPasswordSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPasswordString=newPassword.getEditText().getText().toString();
                reEnteredPasswordString=reEnteredPassword.getEditText().getText().toString();
                if(!newPasswordString.equals("")&&!reEnteredPasswordString.equals("")){
                    if(!newPasswordString.equals(currentPasswordString)){
                        if(newPasswordString.equals(reEnteredPasswordString)){
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(newPassword.getEditText().getWindowToken(), 0);
                            progressDialog.show();
                            UpdatePassword(newPasswordString);
                        }
                        else{
                            Toast.makeText(PasswordChange.this, "Password mismatch", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(PasswordChange.this, "Current password and new password cannot be same!", Toast.LENGTH_SHORT).show();;
                    }
                }
                else{
                    Toast.makeText(PasswordChange.this, "Enter the passwords!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newPassword.getEditText().getWindowToken(), 0);
                new MaterialAlertDialogBuilder(PasswordChange.this)
                        .setCancelable(false)
                        .setTitle(Html.fromHtml("<b>Forgot password</b>"))
                        .setMessage(Html.fromHtml("<font color='#000000'>A password reset mail will be sent to the current account's email. And doing this, you will be logged out.</font>"))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Sending email...");
                                progressDialog.show();
                                auth.sendPasswordResetEmail(firebaseUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful()){
                                            Snackbar.make(findViewById(R.id.view),"Email sent",Snackbar.LENGTH_SHORT)
                                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                                    .show();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(PasswordChange.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });


    }
    private void UpdatePassword(String newPasswordString){
        firebaseUser.updatePassword(newPasswordString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {

                    Toast.makeText(PasswordChange.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(PasswordChange.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}