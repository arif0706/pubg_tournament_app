package com.example.client;

import android.content.Context;
import android.text.format.DateUtils;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.ModelClasses.Notification_Messages;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Notification_display_adapter extends RecyclerView.Adapter<Notification_display_adapter.Notification_display_holder> {

    Context context;
    List<Notification_Messages> list;
    String PUBGID;
    checked check;
    List<String> keys=new ArrayList<>();
    View view;
    RecyclerView recyclerView;
    ViewGroup parent;
    LinearLayoutManager linearLayoutManager;

    Notification_display_adapter(Context context, List<Notification_Messages> list, String PUBGID, checked check, RecyclerView recyclerView, LinearLayoutManager linearLayoutManager){
        this.context=context;
        this.list=list;
        this.PUBGID=PUBGID;
        this.check=check;
        this.recyclerView=recyclerView;
        this.linearLayoutManager=linearLayoutManager;

    }

    @NonNull
    @Override
    public Notification_display_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(context).inflate(R.layout.notification_display_list,parent,false);
        this.parent=parent;
        return new Notification_display_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Notification_display_holder holder, int position) {
        final Notification_Messages[] messages = {list.get(position)};
        holder.title.setText(messages[0].title);
        holder.reason.setText(messages[0].message);

        SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy kk:mm");
        Date setTime= null;
        if(messages[0].seen){
        holder.badge.setVisibility(View.GONE);
        keys.add(messages[0].key);
        List<String> withoutDuplicates=new ArrayList<>(new HashSet<>(keys));
        check.Checked(withoutDuplicates);
        }
        else{
            holder.badge.setVisibility(View.VISIBLE);

        }
        try {
            setTime = sdf1.parse(messages[0].Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time= DateUtils.formatDateTime(context, setTime.getTime(), DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR);
        boolean expanded=list.get(position).isExpanded();
        holder.time.setText(time);

        holder.expand_answer.setVisibility(expanded?View.VISIBLE:View.GONE);
        if(list.get(position).expanded){
            holder.expand.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
        }


        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messages[0] =list.get(position);
                messages[0].setExpanded(!messages[0].isExpanded());
                notifyItemChanged(position);
                messages[0].seen=true;
                holder.badge.setVisibility(View.GONE);
                if(position==list.size()-1){
                    //recyclerView.scrollToPosition(position);
                    int distanceInPixels;
                    View firstVisibleChild = recyclerView.getChildAt(0);
                    int itemHeight = firstVisibleChild.getHeight();
                    int currentPosition = recyclerView.getChildAdapterPosition(firstVisibleChild);
                    int p = Math.abs(position - currentPosition);
                    if (p > 4) distanceInPixels = (p - (p - 4)) * itemHeight;
                    else       distanceInPixels = p * itemHeight;
                    linearLayoutManager.scrollToPositionWithOffset(position, distanceInPixels);
                }

            }
        });
        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messages[0] =list.get(position);
                messages[0].setExpanded(!messages[0].isExpanded());
                TransitionManager.beginDelayedTransition(holder.expand_answer,new Slide(Gravity.BOTTOM));
                notifyItemChanged(position);


            }
        });
        view.setActivated(true);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Notification_display_holder extends RecyclerView.ViewHolder {

        TextView title,reason,time;
        ConstraintLayout expand_answer,constraintLayout;
        MaterialCardView cardView;
        Button expand;
        ImageView badge;
        public Notification_display_holder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            reason=itemView.findViewById(R.id.reason);
            expand=itemView.findViewById(R.id.expand);
            time=itemView.findViewById(R.id.date);
            constraintLayout=itemView.findViewById(R.id.constraint_layout);
            expand_answer=itemView.findViewById(R.id.expand_answer);
            cardView=itemView.findViewById(R.id.card);
            badge=itemView.findViewById(R.id.badge);

        }
    }
    public interface checked{
        void Checked(List<String> keys);
    }

}
