package com.example.client.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.client.ModelClasses.MatchList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private List<MatchList> list;
    private MutableLiveData<String> mText;
    private MutableLiveData<List<MatchList>> matches;
    public LiveData<List<MatchList>> getMatches(){
        if(matches==null){
            matches= new MutableLiveData<>();
            loadmatches();
        }
        return matches;
    }
    private void loadmatches(){
        list=new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Matches");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    MatchList matchList=ds.getValue(MatchList.class);
                    list.add(matchList);
                }
                matches.setValue(list);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}