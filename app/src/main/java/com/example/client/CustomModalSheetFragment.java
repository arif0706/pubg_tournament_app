package com.example.client;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.client.ui.gallery.GalleryFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomModalSheetFragment extends BottomSheetDialogFragment  {
    View root1;
    private BottomSheetListener bottomsheet;
    EditText editText;
    TextView PUBGID,Message,warning;
    BottomSheetListener listener;
    Context context;
    View contentView;
    String text;
    String message,Warning;
    BottomSheetBehavior bottomSheetBehavior;

    public CustomModalSheetFragment(Context context,BottomSheetListener listener,String text,String message,String Warning){
        this.listener=listener;
        this.context=context;
        this.text=text;
        this.message=message;
        this.Warning=Warning;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        contentView=inflater.inflate(R.layout.bottomsheet,container,false);

        root1=inflater.inflate(R.layout.fragment_account,container,false);
        PUBGID=root1.findViewById(R.id.PUBGID);
        Message=contentView.findViewById(R.id.textView);
        Message.setText(message);
        warning=contentView.findViewById(R.id.warning);
        warning.setText(Warning);
        editText=contentView.findViewById(R.id.edit);
        editText.setText(text);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        contentView.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                listener.onButtonClick(editText.getText().toString());
                dismiss();
            }
        });
        return contentView;
    }
    public void showkeyboard(){

    }

    public interface BottomSheetListener{
         void onButtonClick(String text);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            bottomsheet= (BottomSheetListener) context;}
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement BottomsheetListener");
        }
    }

}
