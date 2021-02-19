package com.example.client.ui.slideshow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.client.ModelClasses.Notification_Messages;
import com.example.client.ModelClasses.User;
import com.example.client.ModelClasses.Transactions;
import com.example.client.Notification.Api;
import com.example.client.PinVerification;
import com.example.client.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Transfer_to_friend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Transfer_to_friend extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    TextInputLayout id,amount;
    TableLayout searchResult;
    ProgressBar searchProgress;
    View root,root1;
    Button transfer;
    TextView searchResult_username,searchResult_email,no_data_display;
    ProgressDialog progressDialog;
    ConstraintLayout constraintLayout;
    Snackbar snackbar;
    int entered_amount;
   private  String USERS_PUBGID,USERS_BALANCE,USERS_USERNAME,USERS_PUBG_USERNAME;
   private String FRIENDS_PUBGID,FRIENDS_BALANCE,FRIENDS_PUBG_USERNAME,FRIENDS_USERNAME,FRIENDS_TOKEN;
  static BottomNavigationView navigationView;
  Animation shake;
    View.OnFocusChangeListener focusChangeListener;
    DatabaseReference databaseReference;

    final Double[] UsersBalance = new Double[1];
    final Double[] FriendsBalance = new Double[1];


    public Transfer_to_friend() {
        // Required empty public constructor
    }

    public static Transfer_to_friend newInstance(String param1, String param2, String USERS_PUBGID, String USERS_USERNAME, String USERS_PUBG_USERNAME, BottomNavigationView navigationView) {
        Transfer_to_friend fragment = new Transfer_to_friend();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString("USERS_PUBGID",USERS_PUBGID);
        args.putString("USERS_USERNAME",USERS_USERNAME);
        args.putString("USERS_PUBG_USERNAME",USERS_PUBG_USERNAME);
        Transfer_to_friend.navigationView =navigationView;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            USERS_PUBGID=getArguments().getString("USERS_PUBGID");
            USERS_USERNAME=getArguments().getString("USERS_USERNAME");
            USERS_PUBG_USERNAME=getArguments().getString("USERS_PUBG_USERNAME");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       root= inflater.inflate(R.layout.fragment_transfer_to_friend, container, false);
       root1=inflater.inflate(R.layout.fragment_wallet,container,false);
        id=root.findViewById(R.id.PUBG_ID);
        searchResult=root.findViewById(R.id.searchResult);
        searchResult_email=root.findViewById(R.id.searchResult_email);
        searchResult_username=root.findViewById(R.id.searchResult_username);
        amount=root.findViewById(R.id.amount);
        searchProgress=root.findViewById(R.id.searchProgress);
        no_data_display=root.findViewById(R.id.no_data_display);
        transfer=root.findViewById(R.id.transfer);
        shake= AnimationUtils.loadAnimation(root.getContext(),R.anim.search_result);
        constraintLayout=root.findViewById(R.id.constraint_layout);
        if(USERS_PUBGID!=null) {
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(snackbar!=null){
                    snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                    snackbar.dismiss();
                    snackbar=null;
                }
                return false;
            }
        });
        id.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!id.getEditText().getText().toString().equals("")) {
                    FRIENDS_PUBGID=id.getEditText().getText().toString();
                    getData(id.getEditText().getText().toString());
                } else {
                    id.setError("Enter the Id:");
                }
            }

        });
        id.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!id.getEditText().getText().toString().equals("")) {
                    id.setError(null);
                    searchResult.setVisibility(View.GONE);
                    transfer.setEnabled(false);
                }
                else{

                }


            }
        });
        amount.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!amount.getEditText().getText().toString().equals("")) {
                    amount.setError(null);
                }
            }
        });
        amount.getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(id.getEditText().getText().toString().equals("")){
                    id.setError("Enter the id");
                }

                else {
                    FRIENDS_PUBGID = id.getEditText().getText().toString();
                    getData(id.getEditText().getText().toString());
                }

                return false;
            }
        });
    databaseReference =FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");

          transfer.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {


                  FRIENDS_PUBGID = id.getEditText().getText().toString();
                  getData(FRIENDS_PUBGID);
                  if (!USERS_PUBGID.equals(FRIENDS_PUBGID)) {
                      InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                      imm.hideSoftInputFromWindow(amount.getEditText().getWindowToken(), 0);
                      searchResult.setVisibility(View.INVISIBLE);
                      if (!amount.getEditText().getText().toString().equals("")&&!id.getEditText().getText().toString().equals("")) {
                          entered_amount = Integer.parseInt(amount.getEditText().getText().toString());
                          if (entered_amount >= 10){
                              Intent intent=new Intent(root.getContext(), PinVerification.class);
                              intent.putExtra("PUBGID",USERS_PUBGID);
                              intent.putExtra("displayMessage","Transferring an amount of "+entered_amount+"/- to "+FRIENDS_USERNAME+".");
                              startActivityForResult(intent,1);

                          }
                          else{
                              amount.setError("Amount should be more than 10");

                          }
                      } else {
                          snackbar=Snackbar.make(constraintLayout,"Fill all the fields",Snackbar.LENGTH_SHORT);
                          snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                          snackbar.setAnchorView(navigationView);
                          snackbar.show();
                      }
                  } else {
                      snackbar = Snackbar.make(constraintLayout, "Self transaction is not allowed", Snackbar.LENGTH_SHORT);
                      snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                      snackbar.setAnchorView(navigationView);
                      snackbar.show();
                  }
              }
          });
      }
      else{
          searchResult.setVisibility(View.VISIBLE);
          no_data_display.setVisibility(View.VISIBLE);
          no_data_display.setText("Registration is pending");
          searchProgress.setVisibility(View.GONE);
          searchResult_username.setVisibility(View.GONE);
          searchResult_email.setVisibility(View.GONE);
          root.findViewById(R.id.Username_header).setVisibility(View.GONE);
          root.findViewById(R.id.email_header).setVisibility(View.GONE);
          transfer.setEnabled(false);
      }

        return  root;
    }
    void getData(String USERS_PUBGID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");

            databaseReference.child(USERS_PUBGID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        TransitionManager.beginDelayedTransition(searchResult,new Fade(Fade.IN));
                        searchResult.setVisibility(View.VISIBLE);
                        User user = dataSnapshot.getValue(User.class);
                        FRIENDS_BALANCE = user.Balance;
                        searchProgress.setVisibility(View.GONE);
                        no_data_display.setVisibility(View.GONE);
                        searchResult_username.setVisibility(View.VISIBLE);
                        searchResult_email.setVisibility(View.VISIBLE);
                        root.findViewById(R.id.Username_header).setVisibility(View.VISIBLE);
                        root.findViewById(R.id.email_header).setVisibility(View.VISIBLE);
                        searchResult_email.setText(user.email);
                        FRIENDS_PUBG_USERNAME=user.PUBG_USERNAME;
                        FRIENDS_USERNAME=user.Name;
                        FRIENDS_TOKEN=user.Token_Id;
                        searchResult_username.setText(user.PUBG_USERNAME);
                        transfer.setEnabled(true);
                    } else {
                       TransitionManager.beginDelayedTransition(searchResult,new Fade(Fade.IN));
                        //searchResult.setAnimation(shake);
                        searchResult.setVisibility(View.VISIBLE);
                        no_data_display.setVisibility(View.VISIBLE);
                        searchProgress.setVisibility(View.GONE);
                        searchResult_username.setVisibility(View.GONE);
                        searchResult_email.setVisibility(View.GONE);
                        root.findViewById(R.id.Username_header).setVisibility(View.GONE);
                        root.findViewById(R.id.email_header).setVisibility(View.GONE);
                        transfer.setEnabled(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void SendNotification(String tokenId){
        String title="Amount received";
        String body=USERS_USERNAME+" paid you an amount of "+entered_amount+" \n Play and win😎😎";
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl("https://dbtry-c5fbe.web.app/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Api api=retrofit.create(Api.class);
            Call<ResponseBody> call= api.sendNotification(tokenId,title,body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //Toast.makeText(root.getContext(), data.getStringExtra("hello"), Toast.LENGTH_SHORT).show();
                Transfer();
            }
        }
    }
    public void Transfer(){
        progressDialog = new ProgressDialog(root.getContext());
        progressDialog.setMessage("Fetching details...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("UsersBalance").document(USERS_PUBGID);
        DocumentReference documentReference1 = db.collection("UsersBalance").document(FRIENDS_PUBGID);
        db.runTransaction(new Transaction.Function<Double>() {
            @Nullable
            @Override
            public Double apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                DocumentSnapshot documentSnapshot1 = transaction.get(documentReference1);
                UsersBalance[0] = documentSnapshot.getDouble("Balance");
                progressDialog.setMessage("Transferring...");
                if(entered_amount<=UsersBalance[0]) {
                    FriendsBalance[0] = documentSnapshot1.getDouble("Balance") + entered_amount;
                    progressDialog.setMessage("Updating...");
                    transaction.update(documentReference, "Balance", UsersBalance[0] - entered_amount);
                    transaction.update(documentReference1, "Balance", FriendsBalance[0]);
                    return null;
                }
                else{
                    throw new FirebaseFirestoreException("Not enough balance",
                            FirebaseFirestoreException.Code.ABORTED);


                }



            }
        }).addOnSuccessListener(new OnSuccessListener<Double>() {
            @Override
            public void onSuccess(Double aDouble) {
                progressDialog.dismiss();
                Date date1 = new Date();
                SimpleDateFormat dateFormat;
                dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users_Client");
                databaseReference.child(USERS_PUBGID).child("Balance").setValue(String.valueOf(UsersBalance[0] - entered_amount));
                databaseReference.child(FRIENDS_PUBGID).child("Balance").setValue(String.valueOf(FriendsBalance[0]));
                Transactions trans = new Transactions(String.valueOf(entered_amount), dateFormat.format(date1), "0", USERS_PUBG_USERNAME + "(" + USERS_USERNAME + ")");
                databaseReference1.child("Transactions").child(FRIENDS_PUBGID).push().setValue(trans);
                trans = new Transactions(String.valueOf(entered_amount), dateFormat.format(date1), "Transfer: " + FRIENDS_PUBG_USERNAME + " (" + FRIENDS_USERNAME + ")", "0");
                databaseReference1.child("Transactions").child(USERS_PUBGID).push().setValue(trans);
                Notification_Messages messages=new Notification_Messages(dateFormat.format(date1),"An amount of "+String.valueOf(entered_amount)+"/- 🤑 is credited in your account by "+USERS_USERNAME,"Amount credited.",false);

                databaseReference1.child("Messages").child(FRIENDS_PUBGID).push().setValue(messages);
                snackbar = Snackbar.make(root, "Amount Transferred Successfully", Snackbar.LENGTH_SHORT);
                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                snackbar.setAnchorView(navigationView);
                snackbar.show();
                amount.getEditText().setText("");
                id.getEditText().setText("");
                SendNotification(FRIENDS_TOKEN);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.setMessage("Transfer failed...");
                progressDialog.dismiss();
                if(e.getMessage().equals("Not enough balance")){
                    snackbar=Snackbar.make(root,"Not enough balance",Snackbar.LENGTH_SHORT);
                }
                else {
                    snackbar = Snackbar.make(root, "Transfer failed", Snackbar.LENGTH_SHORT);
                }
                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                snackbar.setAnchorView(navigationView);
                snackbar.show();
            }
        });

    }
}
