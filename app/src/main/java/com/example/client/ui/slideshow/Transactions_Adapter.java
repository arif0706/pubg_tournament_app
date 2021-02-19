package com.example.client.ui.slideshow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.ModelClasses.Transactions;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Transactions_Adapter extends RecyclerView.Adapter<Transactions_Adapter.TransactionsView> {

    Context context;
    List<Transactions> transaction_list;


    View view;
    Transactions_Adapter(Context context, List<Transactions> transaction_list){
        this.context=context;
        this.transaction_list=transaction_list;
    }



    @NonNull
    @Override
    public TransactionsView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(context).inflate(R.layout.recyclerviewlist,parent,false);



        return new TransactionsView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Transactions_Adapter.TransactionsView holder, int position) {

        Transactions dum=transaction_list.get(position);
        SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy kk:mm");
        Date setTime= null;
        try {
            setTime = sdf1.parse(dum.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TransitionManager.beginDelayedTransition(holder.card,new Slide(Gravity.START));
        String time= DateUtils.formatDateTime(context, setTime.getTime(), DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR|DateUtils.FORMAT_SHOW_TIME);
        holder.date.setText(time);
        System.out.println("Time"+setTime.getTime());
        if(dum.From.equals("0")){
            String text="Paid To:   "+dum.paid_to;
            SpannableString spannableString=new SpannableString(text);
            StyleSpan bold=new StyleSpan(Typeface.BOLD);
            spannableString.setSpan(bold,0,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.message.setText(spannableString);
            holder.amount.setText("-"+dum.amount+"/-");
            holder.amount.setTextColor(Color.RED);
        }
        else {
            String text="Received From:   "+dum.From;
            SpannableString spannableString=new SpannableString(text);
            StyleSpan bold=new StyleSpan(Typeface.BOLD);
            spannableString.setSpan(bold,0,13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.message.setText(spannableString);
            holder.amount.setText("+"+dum.amount+"/-");
            holder.amount.setTextColor(Color.rgb(0,100,0));
        }
        view.setActivated(true);

    }

    @Override
    public int getItemCount() {
        return transaction_list.size();
    }


    class TransactionsView extends RecyclerView.ViewHolder{

        TextView message,date,amount;
        MaterialCardView card;


        public TransactionsView(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.amount);
            date=itemView.findViewById(R.id.date);
            message=itemView.findViewById(R.id.message);
            card=itemView.findViewById(R.id.card);

        }
    }
}
