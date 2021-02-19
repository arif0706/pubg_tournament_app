package com.example.client;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.ModelClasses.Players;

import java.util.List;

public class Players_list_adapter extends RecyclerView.Adapter<Players_list_adapter.PlayerViewHolder> {

    View view;
    Context context;
    List<Players> players_lists;
    private onPlayerListener playerListener;



     public Players_list_adapter(Context context, List<Players> players_lists,onPlayerListener playerListener){
        this.context=context;
        this.players_lists=players_lists;
        this.playerListener=playerListener;
        System.out.println("In adapter");
    }
    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(context).inflate(R.layout.players_list,parent,false);
        System.out.println("in createView Holder");
        return new PlayerViewHolder(view,playerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
         Players players=players_lists.get(position);
         holder.Player_name.setText(players.PUBG_USERNAME);
        view.setActivated(true);


    }

    @Override
    public int getItemCount() {
       return players_lists.size();
    }

     class PlayerViewHolder extends RecyclerView.ViewHolder implements onPlayerListener, View.OnClickListener {
         TextView Player_name;
         onPlayerListener playerListener;
        public PlayerViewHolder(@NonNull View itemView,onPlayerListener playerListener) {
            super(itemView);
            System.out.println("playerviewholder");
            Player_name=itemView.findViewById(R.id.player_name);
            this.playerListener=playerListener;
            itemView.setOnClickListener(this);


        }

        @Override
        public void OnPlayerClick(int position) {
            playerListener.OnPlayerClick(getAdapterPosition());

        }

         @Override
         public void onClick(View v) {

         }
     }
    public interface onPlayerListener{
         void OnPlayerClick(int position);
    }
}
