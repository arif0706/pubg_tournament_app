package com.example.client.ui.slideshow;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.client.R;
import com.example.client.ModelClasses.Transactions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class transactions extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private  String PUBGID;
    View root;
    List<Transactions> list;
    DatabaseReference databaseReference;
    GoogleSignInAccount account;
    TextView no_transaction_display;
    RecyclerView recyclerView;
    ProgressBar load;


    public transactions() {
        // Required empty public constructor
    }


    public static transactions newInstance(String param1, String param2,String PUBGID) {
        transactions fragment = new transactions();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString("PUBGID",PUBGID);
        fragment.setArguments(args);
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
        root= inflater.inflate(R.layout.fragment_transactions, container, false);
        account= GoogleSignIn.getLastSignedInAccount(root.getContext());
        recyclerView=root.findViewById(R.id.transactions_list);
        load=root.findViewById(R.id.load);
        no_transaction_display=root.findViewById(R.id.no_transaction_display);
        System.out.println("ID IN TRANSACTIONS:"+PUBGID);
        if(PUBGID != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users_Client").child("Transactions").child(PUBGID);
            list = new ArrayList<>();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()>0) {
                        no_transaction_display.setVisibility(View.INVISIBLE);
                        list.clear();
                        load.setVisibility(View.VISIBLE);
                        System.out.println();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Transactions object = ds.getValue(Transactions.class);
                            System.out.println(object + "    " + ds.getValue().toString());
                            list.add(object);


                        }
                        Collections.reverse(list);
                        System.out.println(list);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(root.getContext());
                        TransitionManager.beginDelayedTransition(recyclerView,new Fade(Fade.IN));
                        recyclerView.setLayoutManager(linearLayoutManager);
                        Transactions_Adapter adapter = new Transactions_Adapter(root.getContext(), list);
                        recyclerView.setAdapter(adapter);
                        load.setVisibility(View.INVISIBLE);

                    }
                    else {
                        load.setVisibility(View.INVISIBLE);
                        no_transaction_display.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            load.setVisibility(View.INVISIBLE);
            no_transaction_display.setVisibility(View.VISIBLE);

        }
        return root;
    }
}
