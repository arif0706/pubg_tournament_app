package com.example.client.ui.slideshow;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.health.TimerStat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.client.ModelClasses.Redemption;
import com.example.client.ModelClasses.internet_receiver;
import com.example.client.Notification.Api;
import com.example.client.PinVerification;
import com.example.client.R;
import com.example.client.ModelClasses.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RedeemMoneyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RedeemMoneyFragment extends Fragment implements internet_receiver.getConnection {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Chip chip;
    TextInputLayout amount;
    Button submit;
    GoogleSignInAccount acct;
    GoogleSignInClient mGoogleSignInClient;
    User user;
    String title="Redemption Request",body="From";
    List<String> token_id;
    int internet_check=0;
    String PUBGID;
    BroadcastReceiver internet_receiver = null;
    TextView Summary_amount,Summary_date,Summary_BHIMID;
    TableLayout request;
    Button cancel_request;
    ProgressBar progressBar;
    BottomNavigationView navigationView;
    Snackbar snackbar;



    Redemption redemptionMain;


    public RedeemMoneyFragment() {
        // Required empty public constructor
    }
    public RedeemMoneyFragment(User user){
        this.user=user;
    }


    public static RedeemMoneyFragment newInstance(String param1, String param2,String PUBGID,BottomNavigationView navigationView) {
        RedeemMoneyFragment fragment = new RedeemMoneyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString("PUBGID",PUBGID);
        fragment.setArguments(args);
        fragment.navigationView=navigationView;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            PUBGID=getArguments().getString("PUBGID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_redeem_money, container, false);
        amount=root.findViewById(R.id.amount);
        submit=root.findViewById(R.id.submit);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        acct = GoogleSignIn.getLastSignedInAccount(getActivity().getApplication());
        body=body.concat(" "+acct.getDisplayName());
        Summary_amount=root.findViewById(R.id.Summary_amount);
        Summary_date=root.findViewById(R.id.Summary_date);
        Summary_BHIMID=root.findViewById(R.id.Summary_BHIMID);
        request=root.findViewById(R.id.request);
        cancel_request=root.findViewById(R.id.cancel_request);
        progressBar=root.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        TransitionManager.beginDelayedTransition(root.findViewById(R.id.constraint_layout),new Fade(Fade.IN));

        amount.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!amount.getEditText().getText().toString().equals("")){
                    amount.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!amount.getEditText().getText().toString().equals("")){
                    amount.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!amount.getEditText().getText().toString().equals("")){
                    amount.setError(null);
                }
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getContext()).setTitle(Html.fromHtml("<font color='#1c2e4a'><b>Warning</b></font>")).setMessage(Html.fromHtml("<font color='#1c2e4a'>Are you sure to delete the request?</font>")).setPositiveButton(Html.fromHtml("<font color='#FF0000'>Yes</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent=new Intent(root.getContext(), PinVerification.class);
                        intent.putExtra("PUBGID",PUBGID);
                        intent.putExtra("displayMessage","Cancelling the withdrawal request!");
                        startActivityForResult(intent,1);


                    }
                }).setNegativeButton(Html.fromHtml("No"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });

        internet_receiver = new internet_receiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(internet_receiver, intentFilter);
        if(PUBGID!=null) {
            amount.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Redemption_Requests");
            databaseReference.child(PUBGID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.GONE);
                    if(snapshot.exists()){
                        TransitionManager.beginDelayedTransition(request,new Slide(Gravity.START));
                        request.setVisibility(View.VISIBLE);

                        root.findViewById(R.id.textView3).setVisibility(View.GONE);

                        Redemption redemption = snapshot.getValue(Redemption.class);

                        Timestamp ts= Timestamp.valueOf(redemption.Date_of_req);
                        Date setTime = new Date(ts.getTime());
                        Summary_amount.setText(redemption.amount+"/-");
                        String time= DateUtils.formatDateTime(root.getContext(),setTime.getTime() , DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR|DateUtils.FORMAT_SHOW_TIME);
                        Summary_date.setText(time);
                        Summary_BHIMID.setText(redemption.BHIM_ID);

                    }
                    else{
                        amount.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.VISIBLE);
                        TransitionManager.beginDelayedTransition(request,new AutoTransition());
                        request.setVisibility(View.GONE);

                        root.findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Summary");
                    View view1 = LayoutInflater.from(getContext()).inflate(R.layout.summary_redemption, null);
                    builder.setView(view1);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", null);
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog dialog = builder.create();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(amount.getWindowToken(), 0);
                    String text = amount.getEditText().getText().toString();
                    databaseReference1.orderByChild("email").equalTo(acct.getEmail()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            progressBar.setIndeterminate(false);
                            progressBar.setVisibility(View.GONE);
                            if (dataSnapshot.exists()) {
                                if (!text.isEmpty()) {
                                    System.out.println(text + acct.getEmail());
                                    if (internet_check == 1) {
                                        databaseReference1.orderByChild("email").equalTo(acct.getEmail()).addValueEventListener(new ValueEventListener() {
                                            User user;
                                            Redemption redemption;

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                System.out.println(dataSnapshot.getValue());
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    user = ds.getValue(User.class);
                                                }
                                                if (user.winning_money >= Integer.parseInt(text)) {
                                                    if (!user.BHIM_ID.equals("0")) {
                                                        if (Integer.parseInt(text) >= 10) {


                                                            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
                                                            com.google.firebase.Timestamp ts=com.google.firebase.Timestamp.now();
                                                            redemption = new Redemption(user.BHIM_ID, user.PUBG_USERNAME, text, user.email,user.PUBG_ID,String.valueOf(timestamp),ts.getSeconds());
                                                            databaseReference.orderByChild("email").equalTo(acct.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        new MaterialAlertDialogBuilder(getContext()).setTitle("Already one redemption is under process")
                                                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                                    }
                                                                                }).show();
                                                                    } else {
                                                                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                                                            @Override
                                                                            public void onShow(DialogInterface dialog) {
                                                                                TextView amount = view1.findViewById(R.id.redemption_amount);
                                                                                amount.append("     " + text + "/-");
                                                                                TextView bhim = view1.findViewById(R.id.BHIMID);
                                                                                bhim.append("     " + user.BHIM_ID);
                                                                                Button positive = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                                                                                positive.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {

                                                                                        Intent intent=new Intent(root.getContext(),PinVerification.class);
                                                                                        intent.putExtra("PUBGID",PUBGID);
                                                                                        intent.putExtra("displayMessage","Withdrawal request of "+text+"/-.");
                                                                                        startActivityForResult(intent,2);

                                                                                        redemptionMain=redemption;
                                                                                        dialog.dismiss();


                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                        amount.getEditText().setText("");
                                                                        dialog.show();

                                                                    }
                                                                }


                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        } else {
                                                            amount.setError("Amount should be more than 10/-");

                                                        }
                                                    } else {
                                                       snackbar= Snackbar.make(v, "BHIMID is not updated", Snackbar.LENGTH_LONG);
                                                       snackbar.setAnchorView(navigationView);
                                                       snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                                       snackbar.show();
                                                    }

                                                } else {
                                                    new MaterialAlertDialogBuilder(v.getContext()).setTitle("Not enough Balance").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else {
                                        new MaterialAlertDialogBuilder(v.getContext()).setTitle("Check the internet Connection").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).show();
                                    }
                                } else {
                                    amount.setError("Enter the amount!");
                                }

                            } else {
                                snackbar=Snackbar.make(v, "Update your PUBG credentials", Snackbar.LENGTH_LONG);
                                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                snackbar.show();
                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            });
        }
        return  root;
    }

    private void removeRequest() {
        DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("Redemption_Requests");
        databaseReference1.child(PUBGID).removeValue();
    }

    public  void getTokens(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Admin_Tokens");
        token_id=new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                 String tokens=ds.getValue(String.class);
                    System.out.println(tokens);

                    token_id.add(tokens);
                    single(token_id);
                    token_id.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println("List::::"+token_id);

    }
    public void  single(List<String> token_id){
        int i=token_id.size()-1;
        System.out.println("In Single"+token_id.get(i));
        SendNotification(token_id.get(i));
    }
    public void SendNotification(String token){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://dbtry-c5fbe.web.app/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);
        Call<ResponseBody> call= api.sendNotification(token,title,body);
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
    public void getNoConnectionValue(String text) {
        internet_check=0;
    }

    @Override
    public void getYesConnectionValue(String online, String text) {
        internet_check=1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                removeRequest();
            }
        }
        if(requestCode==2){
            if(resultCode==RESULT_OK){
                submitRequest();
            }
        }
    }

    private void submitRequest(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Redemption_Requests");
        databaseReference.child(PUBGID).setValue(redemptionMain).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("success");
                getTokens();


            }
        });

    }
}

