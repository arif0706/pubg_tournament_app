package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.client.ui.Refer;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PinVerification extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout password,animate;
    Animation shake;
    TextView messageDisplay,purpose;
    String pin;

    String PUBGID;
    String purposeMessage;

    Button zero, one,two,three,four,five,six,seven,eight,nine;
    ImageButton delete,ok;


    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Wallet pin"));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_verification);
        password=findViewById(R.id.pin);
        messageDisplay=findViewById(R.id.messageDisplay);
        purpose=findViewById(R.id.purpose);

        password.getEditText().requestFocus();
        animate=findViewById(R.id.animate);
        password.getEditText().setSelection(password.getEditText().getText().toString().length());
        shake= AnimationUtils.loadAnimation(this,R.anim.incorrect);
        password.getEditText().setTransformationMethod(new bigbubbleTransformation());
        password.getEditText().setShowSoftInputOnFocus(false);
        Intent intent=getIntent();
        PUBGID=intent.getStringExtra("PUBGID");
        purposeMessage=intent.getStringExtra("displayMessage");

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

        purpose.setText(purposeMessage);





        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TransitionManager.beginDelayedTransition(findViewById(R.id.tab_layout),new AutoTransition());
                if(messageDisplay.getVisibility()==View.VISIBLE){
                    messageDisplay.setVisibility(View.INVISIBLE);

                }
                if(password.getEditText().getText().toString().equals("")) {
                    password.getEditText().requestFocus();
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                databaseReference1.child(PUBGID).child("pin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pin=snapshot.getValue(String.class);
                        if (password.getEditText().getText().toString().equals("")||!password.getEditText().getText().toString().equals(pin)){
                            TransitionManager.beginDelayedTransition(findViewById(R.id.tab_layout),new AutoTransition());
                            messageDisplay.setVisibility(View.VISIBLE);
                            messageDisplay.setTextColor(Color.RED);
                            messageDisplay.setText("Incorrect pin");
                            messageDisplay.startAnimation(shake);
                            password.startAnimation(shake);
                        }
                        else {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("hello","hi");
                            setResult(Activity.RESULT_OK,resultIntent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!password.getEditText().getText().toString().equals(""))
                {
                    password.getEditText().setText("");
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.one:
                password.getEditText().append("1");
                break;
            case R.id.two:
                password.getEditText().append("2");
                break;
            case R.id.three:
                password.getEditText().append("3");
                break;
            case R.id.four:
                password.getEditText().append("4");
                break;
            case R.id.five:
                password.getEditText().append("5");
                break;
            case R.id.six:
                password.getEditText().append("6");
                break;
            case R.id.seven:
                password.getEditText().append("7");
                break;
            case R.id.eight:
                password.getEditText().append("8");
                break;
            case R.id.nine:
                password.getEditText().append("9");
                break;
            case R.id.zero:
                password.getEditText().append("0");
                break;

            case R.id.delete :
                String text=password.getEditText().getText().toString();
                if(!text.equals("")) {
                    text = text.substring(0, text.length() - 1);
                    password.getEditText().setText(text);
                }
                break;
        }
    }
}