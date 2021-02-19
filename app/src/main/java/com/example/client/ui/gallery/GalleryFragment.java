package com.example.client.ui.gallery;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.client.CustomModalSheetFragment;
import com.example.client.Logged1;
import com.example.client.MainActivity;
import com.example.client.ModelClasses.internet_receiver;
import com.example.client.PasswordChange;
import com.example.client.R;
import com.example.client.ModelClasses.User;
import com.example.client.pinRegistration;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class GalleryFragment extends BottomSheetDialogFragment implements CustomModalSheetFragment.BottomSheetListener, internet_receiver.getConnection {

    private GalleryViewModel galleryViewModel;
    Button button, changePassword,changePin;
    private  TextView PUBGID,Kills,Wins,fullname,BHIM,no_of_matches,pubg_username,total_money_earned;
    private  EditText editText;
    View root,root1;
    String PUBG_ID,BHIM_ID,ID;
    BottomSheetDialogFragment bottomSheetDialogFragment;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount acct;
    int identify=0;
    MaterialCardView pubgusername_card,bhimid_card;
    CircleImageView imageView;
    int internet_check=0;
    BroadcastReceiver internet_receiver=null;
    TableLayout profile_desc;
    ConstraintLayout constraintLayout;
    TextInputLayout pubgid,pubg_Username,bhimid,referral_code_input;

    FirebaseAuth auth;

    ShimmerFrameLayout shimmerProfile,shimmerBHIM,shimmerTable,shimmerPasswordButton,shimmerPinButton,shimmerPUBGID;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL,R.style.DialogStyle);

        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
         root = inflater.inflate(R.layout.fragment_account, container, false);
         root1=inflater.inflate(R.layout.bottomsheet,container,false);

        //ButterKnife.bind(getActivity());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getApplicationContext(), gso);
        acct = GoogleSignIn.getLastSignedInAccount(getActivity().getApplication());
        button=root1.findViewById(R.id.button2);
        PUBGID=root.findViewById(R.id.PUBGID);
        Wins=root.findViewById(R.id.wins);
        Kills=root.findViewById(R.id.kills);
        fullname=root.findViewById(R.id.fullname);
        editText=root1.findViewById(R.id.edit);
        BHIM=root.findViewById(R.id.BHIMID);
        total_money_earned=root.findViewById(R.id.total_money_earned);
        ImageButton imageButton=root.findViewById(R.id.PUBG_username_EDIT);
        ImageButton imageButton1=root.findViewById(R.id.BHIMIDEDIT);
        bhimid_card =root.findViewById(R.id.bhim_card);
        imageView=root.findViewById(R.id.profile_pic);
        no_of_matches=root.findViewById(R.id.matches_played);
        pubg_username=root.findViewById(R.id.PUBG_username);
        pubgusername_card=root.findViewById(R.id.pubg_username_card);
        profile_desc=root.findViewById(R.id.profile_desc);
        constraintLayout=root.findViewById(R.id.view);
        changePassword=root.findViewById(R.id.PasswordButton);
        changePin=root.findViewById(R.id.PinButton);


        internet_receiver = new internet_receiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(internet_receiver, intentFilter);


        auth=FirebaseAuth.getInstance();



        shimmerBHIM=root.findViewById(R.id.shimmerBHIM);
        shimmerPasswordButton=root.findViewById(R.id.shimmerPasswordButton);
        shimmerPinButton=root.findViewById(R.id.shimmerPinButton);
        shimmerPUBGID=root.findViewById(R.id.shimmerPUBGID);
        shimmerProfile=root.findViewById(R.id.shimmerProfile);
        shimmerTable=root.findViewById(R.id.shimmerTable);

        shimmerBHIM.startShimmerAnimation();
        shimmerPasswordButton.startShimmerAnimation();
        shimmerPUBGID.startShimmerAnimation();
        shimmerTable.startShimmerAnimation();
        shimmerProfile.startShimmerAnimation();
        shimmerPinButton.startShimmerAnimation();
        imageButton.setEnabled(false);
        imageButton1.setEnabled(false);



        fullname.setBackgroundColor(Color.GRAY);
        PUBGID.setBackgroundColor(Color.GRAY);
        pubg_username.setBackgroundColor(Color.GRAY);
        no_of_matches.setBackgroundColor(Color.GRAY);
        Kills.setBackgroundColor(Color.GRAY);
        Wins.setBackgroundColor(Color.GRAY);
        total_money_earned.setBackgroundColor(Color.GRAY);
        BHIM.setBackgroundColor(Color.GRAY);
        changePassword.setEnabled(false);
        changePin.setEnabled(false);



        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(), PasswordChange.class);
                startActivityForResult(intent,1);

            }
        });

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
        databaseReference.orderByChild("email").equalTo(acct.getEmail()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    System.out.println(dataSnapshot.getValue());




                    shimmerBHIM.stopShimmerAnimation();
                    shimmerPasswordButton.stopShimmerAnimation();
                    shimmerPUBGID.stopShimmerAnimation();
                    shimmerTable.stopShimmerAnimation();
                    shimmerProfile.stopShimmerAnimation();
                    shimmerPinButton.stopShimmerAnimation();
                    imageButton.setEnabled(true);
                    imageButton1.setEnabled(true);

                    TransitionManager.beginDelayedTransition(constraintLayout,new Fade(Fade.IN).setDuration(400));

                    fullname.setBackgroundColor(Color.TRANSPARENT);
                    PUBGID.setBackgroundColor(Color.TRANSPARENT);
                    pubg_username.setBackgroundColor(Color.TRANSPARENT);
                    no_of_matches.setBackgroundColor(Color.TRANSPARENT);
                    Kills.setBackgroundColor(Color.TRANSPARENT);
                    Wins.setBackgroundColor(Color.TRANSPARENT);
                    total_money_earned.setBackgroundColor(Color.TRANSPARENT);
                    BHIM.setBackgroundColor(Color.TRANSPARENT);
                    changePassword.setEnabled(true);
                    changePin.setEnabled(true);





                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        User user = ds.getValue(User.class);
                        PUBGID.append(user.PUBG_ID);
                        ID= user.PUBG_ID;
                        pubg_username.setText(user.PUBG_USERNAME);
                        PUBG_ID = pubg_username.getText().toString();
                        Wins.setText(user.Wins);
                        Kills.setText(user.Kills);
                        fullname.setText(acct.getDisplayName());
                        BHIM.setText(user.BHIM_ID);
                        total_money_earned.setText(String.valueOf(user.total_money_earned));
                        no_of_matches.setText(String.valueOf(user.no_of_matches));
                        BHIM_ID = BHIM.getText().toString();
                        try {
                            Glide.with(GalleryFragment.this).load(user.photo_url)
                                    .into(imageView);
                        } catch (NullPointerException e) {
                          //  AlarmManager mgr = (AlarmManager) root.getContext().getSystemService(Context.ALARM_SERVICE);
                            //mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(root.getContext(), 0, new Intent(getActivity().getIntent()), getActivity().getIntent().getFlags()));
                            //System.exit(2);

                        }

                    }


                }
                else{

                    final String[] tokenId = new String[1];
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setTitle("Enter the details");
                    View view1=LayoutInflater.from(getContext()).inflate(R.layout.account_alert,null);

                    pubgid=view1.findViewById(R.id.pubgid_new);
                    pubg_Username=view1.findViewById(R.id.pubg_username_new);
                    bhimid=view1.findViewById(R.id.bhimid_new);
                    referral_code_input=view1.findViewById(R.id.refer_new);


                    pubgid.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(!pubgid.getEditText().getText().toString().equals(""))
                                pubgid.setError(null);
                        }
                    });
                    referral_code_input.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(!referral_code_input.getEditText().getText().toString().equals(""))
                                referral_code_input.setError(null);
                        }
                    });




                    builder.setView(view1);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok",null);
                    builder.setNegativeButton("Cancel",null);
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button positive=((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                            Button negative=((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                            negative.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getContext(), Logged1.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });
                            positive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                                    String PUBGID = pubgid.getEditText().getText().toString();
                                    String PUBG_Username = pubg_Username.getEditText().getText().toString();
                                    String BHIMId = bhimid.getEditText().getText().toString();
                                    final String[] referral_code = {referral_code_input.getEditText().getText().toString()};
                                    if (referral_code[0].equals("")) {
                                        referral_code[0] = "no";
                                    }

                                    if (!PUBGID.equals("") && !PUBG_Username.equals("") && !BHIMId.equals("")){
                                        databaseReference1.orderByChild("PUBG_ID").equalTo(PUBGID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    pubgid.setError("An account exists with this PUBG ID");
                                                } else {
                                                    if (!referral_code[0].equals(PUBGID)) {
                                                        databaseReference1.child(referral_code[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    User user = new User(acct.getEmail(), acct.getDisplayName(), PUBGID, "0", "0", "0", PUBG_Username, BHIMId, tokenId[0], "no", 0, acct.getPhotoUrl().toString(), 0, 0, referral_code[0], "no","0");
                                                                    databaseReference1.child(PUBGID).setValue(user);
                                                                    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                                                                    Map<String, Object> bal = new HashMap<>();
                                                                    bal.put("Balance",0);
                                                                    bal.put("Winnings",0);
                                                                    firebaseFirestore.collection("UsersBalance").document(PUBGID).set(bal);
                                                                    dialog.dismiss();
                                                                }


                                                                else if (referral_code[0].equals("no")){
                                                                    User user = new User(acct.getEmail(), acct.getDisplayName(), PUBGID, "0", "0", "0", PUBG_Username, BHIMId, tokenId[0], "no", 0, acct.getPhotoUrl().toString(), 0, 0, referral_code[0], "no","0");
                                                                    databaseReference1.child(PUBGID).setValue(user);
                                                                    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                                                                    Map<String, Object> bal = new HashMap<>();
                                                                    bal.put("Balance",0);
                                                                    bal.put("Winnings",0);
                                                                    firebaseFirestore.collection("UsersBalance").document(PUBGID).set(bal);
                                                                    dialog.dismiss();
                                                                }

                                                                else{
                                                                    referral_code_input.setError("Invalid Code");
                                                                }

                                                            }
                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                    } else {
                                                        referral_code_input.setError("Invalid code");
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if (task.isSuccessful()) {
                                                tokenId[0] = task.getResult().getToken();
                                            }

                                        }
                                    });


                                }
                                    else{
                                        Toast.makeText(root.getContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    });

                    dialog.show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.PUBG_username_EDIT:
                        identify=1;
                        bottomSheetDialogFragment = new CustomModalSheetFragment(getContext(), GalleryFragment.this,PUBG_ID,"PUBG USERNAME","Careful while editing.Wrong id make us difficulty in identification.");
                        bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
                        break;
                    case R.id.BHIMIDEDIT:
                        identify=2;
                        bottomSheetDialogFragment = new CustomModalSheetFragment(getContext(), GalleryFragment.this,BHIM_ID,"BHIM ID","Careful while Editing.Mistakes may cause problem in transactions");
                        bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
                        break;
                }
            }
        };
        imageButton.setOnClickListener(onClickListener);
        imageButton1.setOnClickListener(onClickListener);


        changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(), pinRegistration.class);
                intent.putExtra("PUBGID",ID);
                intent.putExtra("messageToDisplay","Change your wallet pin.");
                startActivityForResult(intent,2);
            }
        });
        return root;
    }



    @Override
    public void onButtonClick(String text) {
        if(internet_check==1) {
            if (identify == 1) {
                if (text.equals("".trim())) {
                    Toast.makeText(root.getContext(), "Fill the details", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                    databaseReference.child(ID).child("PUBG_USERNAME").setValue(text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pubg_username.setText(text);

                        }
                    });

                }
            } else if (identify == 2) {

                if (text.equals("".trim())) {
                    Toast.makeText(root.getContext(), "Fill the details", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                    databaseReference.child(ID).child("BHIM_ID").setValue(text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            BHIM.setText(text);
                        }
                    });

                }
            }
        }
        else
        {
            Snackbar.make(root.findViewById(R.id.view),"No internet Connection",Snackbar.LENGTH_SHORT).show();
        }
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
                auth.signOut();
                Intent intent=new Intent(root.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        }

    }
}
