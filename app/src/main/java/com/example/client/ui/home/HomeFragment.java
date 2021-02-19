package com.example.client.ui.home;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.ListOfPlayers;
import com.example.client.Logged1;
import com.example.client.MatchAdapter;

import com.example.client.ModelClasses.MatchList;
import com.example.client.ModelClasses.internet_receiver;
import com.example.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.todkars.shimmer.ShimmerRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements MatchAdapter.OnMatchListener{

    private List<MatchList> list;
    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    HomeViewModel homeViewModel1;
    private View root,root1;
    GoogleSignInAccount account;
    ImageView imageView;
    MaterialCardView materialCardView;
    Button refresh;
    TextView no_match_display;
    DatabaseReference databaseReference;
    ShimmerFrameLayout shimmerFrameLayout;
        BroadcastReceiver internet_receiver=null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        root= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=root.findViewById(R.id.my_recycler_view);
        root1=inflater.inflate(R.layout.recyclerview_list,container,false);
        //chip=root.findViewById(R.id.new_match);
        homeViewModel1=new ViewModelProvider(this).get(HomeViewModel.class);
        account= GoogleSignIn.getLastSignedInAccount(root.getContext());
        imageView=root1.findViewById(R.id.image);
        no_match_display=root.findViewById(R.id.no_match_display);
        materialCardView=root1.findViewById(R.id.card);
        shimmerFrameLayout=root.findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmerAnimation();

        list = new ArrayList<>();
            //snackbar.dismiss();
            databaseReference = FirebaseDatabase.getInstance().getReference("Matches");
            databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        no_match_display.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        list.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            MatchList matchList = ds.getValue(MatchList.class);
                            list.add(matchList);
                        }
                        TransitionManager.beginDelayedTransition(recyclerView,new Fade(Fade.IN).setDuration(500 ));
                        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                        adapter = new MatchAdapter(root.getContext(), list, HomeFragment.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);


                    if(dataSnapshot.getChildrenCount()==0) {
                        shimmerFrameLayout.stopShimmerAnimation();
                         no_match_display.setVisibility(View.VISIBLE);
                         shimmerFrameLayout.setVisibility(View.GONE);
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

       // internet_receiver =new internet_receiver();
       // IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //getActivity().registerReceiver(internet_receiver,intentFilter);




        return root;
    }






    @Override
    public void OnMatchClick(int position,View view,String name) {
        System.out.println(list.get(position).matchId);
        Intent intent=new Intent(getActivity().getApplicationContext(), ListOfPlayers.class);
        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(getActivity(),view,name);
        intent.putExtra("match_id",list.get(position).matchId);
        intent.putExtra("email",account.getEmail());
        intent.putExtra("time",list.get(position).date+" "+list.get(position).time);
        startActivity(intent,options.toBundle());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }

}
