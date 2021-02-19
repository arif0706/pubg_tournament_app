package com.example.client.ui.Settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.google.android.material.card.MaterialCardView;

import static com.example.client.R.drawable.ic_baseline_keyboard_arrow_up_24;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FAQsHolder> {

    View view;
    Context context;
   FAQ[] list;
    FaqAdapter(Context context,FAQ[] list){
        this.context=context;
        this.list=list;

    }
    @NonNull
    @Override
    public  FAQsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.faqs,parent,false);
        return new FAQsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQsHolder holder, int position) {
        holder.question_view.setText(list[position].getQuestion());
        holder.answer_view.setText(list[position].getAnswer());
        boolean expanded=list[position].isExpanded();
        holder.expand_answer.setVisibility(expanded?View.VISIBLE:View.GONE);
        if (holder.expand_answer.getVisibility()==View.VISIBLE){
            holder.expand.setBackgroundResource(ic_baseline_keyboard_arrow_up_24);
        }
       holder. expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FAQ faq=list[position];
                faq.setExpanded(!faq.isExpanded());
                notifyItemChanged(position);

            }
        });
        view.setActivated(true);
    }


    @Override
    public int getItemCount() {
       return list.length;
    }
    public class FAQsHolder extends RecyclerView.ViewHolder{
        Button expand;
        ConstraintLayout expand_answer;
        MaterialCardView cardView;
        TextView question_view,answer_view;

        public FAQsHolder(@NonNull View itemView) {
            super(itemView);
            expand=itemView.findViewById(R.id.expand);
            expand_answer=itemView.findViewById(R.id.expand_answer);
            cardView=itemView.findViewById(R.id.card);
            question_view=itemView.findViewById(R.id.title);
            answer_view=itemView.findViewById(R.id.reason);


        }
    }
}
