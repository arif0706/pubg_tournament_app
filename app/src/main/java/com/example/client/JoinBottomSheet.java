package com.example.client;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class JoinBottomSheet extends BottomSheetDialogFragment {
    joinBottomSheetListener listener;
    Context context;
    private View contentview;
    Button cancel,confirm;
    String match_id,Entry_fee,balance,Pubg_ID;
    TextView Bal,Fee,M_Id;
    
    JoinBottomSheet(Context context,joinBottomSheetListener listener,String match_id,String Entry_fee,String balance,String Pubg_ID){
        this.match_id=match_id;
        this.Entry_fee=Entry_fee;
        this.Pubg_ID=Pubg_ID;
        this.balance=balance;
        this.context=context;
        this.listener=listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentview=inflater.inflate(R.layout.join_bottom_sheet,container,false);
        confirm=contentview.findViewById(R.id.confirm);
        cancel=contentview.findViewById(R.id.cancel);
        Fee=contentview.findViewById(R.id.entry_fee_edit);
        Bal=contentview.findViewById(R.id.balance_edit);
        M_Id=contentview.findViewById(R.id.match_id_edit);


        Fee.setText(Entry_fee+"/-");
        Bal.setText(balance+"/-");
        M_Id.setText(match_id);
        if (Double.parseDouble(balance)<Double.parseDouble(Entry_fee))
            confirm.setEnabled(false);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveButtonClick(balance,Pubg_ID);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeButtonClick(null);
            }
        });

        return contentview;
    }

    public interface joinBottomSheetListener{
        public void onPositiveButtonClick(String balance,String PUBG_ID);
        public void onNegativeButtonClick(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
