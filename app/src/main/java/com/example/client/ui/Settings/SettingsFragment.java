package com.example.client.ui.Settings;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.client.MatchAdapter;
import com.example.client.R;
import com.example.client.ui.home.HomeFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import static com.example.client.R.drawable.ic_baseline_keyboard_arrow_down_24;
import static com.example.client.R.drawable.ic_baseline_keyboard_arrow_up_24;

public class SettingsFragment extends Fragment {
    RecyclerView recyclerView;
    FaqAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        final View root = inflater.inflate(R.layout.activity_settings, container, false);
        recyclerView=root.findViewById(R.id.FAQ);
        this.setEnterTransition(new AutoTransition());
        FAQ[] list=new FAQ[]{
                new FAQ("1. How to join in match?","1.Visit the home page where you find the display of matches.\n2.A button named 'join' is appeared if it is enabled then you can click on it and join the match after verifying the prices."),
                new FAQ("2. How to Redeem Money?","1.Go to Wallet.\n2.In the options given bottom select the second one i.e., Redeem.\n3.Give the amount you want to withdraw(should be greater than 10).\n4.Click 'Redeem'\n5.A summary is appeared verify the details and click Ok.\n6.The amount will be credited in your account within 24-36hrs."),
                new FAQ("3. How to update our details?","1.Go to My Account.\n2.There you can only edit your PUBG username and BHIM/UPI id."),
                new FAQ("4. Can we get refund from a joined match?","No once you have joined the match you can't get the refund.The refund is only given when the match is cancelled by the host."),
                new FAQ("5. Can we pay for others?","Directly you can't pay for others.But there is a way,you can go to wallet and transfer the amount to your friends by entering his PUBG ID(note:Make sure he is registered in this app.)"),
                new FAQ("6. How can I know my payments?","Go to wallet and check for the transactions"),
                new FAQ("7. Other problem?","Mail us to\n rameshyadavchalla@gmail.com")
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setVisibility(View.VISIBLE);
        TransitionManager.beginDelayedTransition(recyclerView,new Slide(Gravity.TOP));
        adapter = new FaqAdapter(root.getContext(), list);

        recyclerView.setAdapter(adapter);
        return root;
    }

}
