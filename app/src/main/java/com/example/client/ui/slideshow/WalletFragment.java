package com.example.client.ui.slideshow;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.client.Logged1;
import com.example.client.ModelClasses.internet_receiver;
import com.example.client.R;
import com.example.client.ModelClasses.User;
import com.example.client.pinRegistration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class WalletFragment extends Fragment implements internet_receiver.getConnection {

    private WalletViewModel slideshowViewModel;
    private BottomNavigationView navigationView;
 TextView amount,winning_money;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount acct;
    User user;
    View root;
    String PUBGID,USERNAME,PUBG_USERNAME;
    TableLayout frameLayout;
    ProgressBar progressBar;
    FrameLayout frame;
    Boolean flag=false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_wallet, container, false);
        slideshowViewModel =
                ViewModelProviders.of(this).get(WalletViewModel.class);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        acct = GoogleSignIn.getLastSignedInAccount(getActivity().getApplication());
        navigationView=root.findViewById(R.id.bottom_navigation);
        amount=root.findViewById(R.id.show_balance_text);
        winning_money=root.findViewById(R.id.winning_money);
        frameLayout=root.findViewById(R.id.tab_layout);
        frame=root.findViewById(R.id.container);
        progressBar=root.findViewById(R.id.load);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");

                databaseReference.orderByChild("email").equalTo(acct.getEmail()).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            TransitionManager.beginDelayedTransition(frame,new AutoTransition());
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                user = ds.getValue(User.class);
                                System.out.println(user.Balance);
                                if(user.pin.equals("0")){
                                    Intent intent=new Intent(root.getContext(), pinRegistration.class);
                                    intent.putExtra("PUBGID",user.PUBG_ID);
                                    startActivityForResult(intent,1);

                                }
                                else{

                                }
                                amount.setText(user.Balance + "/-");
                                winning_money.setText(user.winning_money + "/-");
                            }
                            flag=true;
                            PUBGID = user.PUBG_ID;
                            USERNAME=user.Name;
                            PUBG_USERNAME=user.PUBG_USERNAME;
                            System.out.println("ID IN IF:" + PUBGID);



                        } else {
                            flag=true;
                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            Handler handler=new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {

                        try {
                           Thread.sleep(100);
                        } catch (InterruptedException e) {

                        }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("PUBGID IN RUN"+PUBGID);
                            progressBar.setVisibility(View.INVISIBLE);
                            openFragment(AddMoneyFragment.newInstance("","",PUBGID,navigationView));

                        }
                    });

                }
            }).start();



        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        return root;

    }
    public void openFragment(Fragment fragment) {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        TransitionManager.beginDelayedTransition(navigationView,new AutoTransition());
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.add_money:
                            TransitionManager.beginDelayedTransition(frame,new AutoTransition().setDuration(150));
                            frameLayout.setVisibility(View.VISIBLE);
                            openFragment(AddMoneyFragment.newInstance("","",PUBGID,navigationView));
                            return true;
                        case R.id.redeem_money:
                            TransitionManager.beginDelayedTransition(frame,new AutoTransition().setDuration(150));
                            frameLayout.setVisibility(View.VISIBLE);
                            openFragment(RedeemMoneyFragment.newInstance("","",PUBGID,navigationView));
                            return true;
                        case R.id.transactions:
                            frameLayout.setVisibility(View.INVISIBLE);
                            openFragment(transactions.newInstance("","",PUBGID));
                            return true;
                        case R.id.transfer_to_friend:
                            TransitionManager.beginDelayedTransition(frame,new AutoTransition().setDuration(150));
                            frameLayout.setVisibility(View.VISIBLE);
                            openFragment(Transfer_to_friend.newInstance("","",PUBGID,USERNAME,PUBG_USERNAME,navigationView));
                            return  true;
                    }
                    return false;
                }
            };

    @Override
    public void getNoConnectionValue(String text) {
        flag=false;
    }

    @Override
    public void getYesConnectionValue(String online, String text) {
        flag=true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==1){
            if(resultCode==RESULT_OK){
                if(data.getStringExtra("message").equals("1")){

                }
            }
            else if(resultCode==RESULT_CANCELED){
                Intent intent=new Intent(getContext(), Logged1.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }
}