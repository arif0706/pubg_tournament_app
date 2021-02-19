package com.example.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.ModelClasses.MatchList;
import com.example.client.ModelClasses.Players;
import com.example.client.ModelClasses.User;
import com.example.client.ModelClasses.internet_receiver;
import com.example.client.Notification.Receiver;
import com.example.client.ModelClasses.Transactions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> implements JoinBottomSheet.joinBottomSheetListener, internet_receiver.getConnection {
   private GoogleSignInAccount account;
    private Context context;
    private List<MatchList> matchLists;
    private OnMatchListener onMatchListener;
   private View view;
    private User user;
    FragmentManager fragmentManager;
    BottomSheetDialogFragment bottomSheetDialogFragment;
    ProgressDialog progressDialog;

    BroadcastReceiver internet_receiver = null;
    int internet_check=0;
    String PUBGID;
    Snackbar snackbar;
      String Entry_Fee,match_Id,time;
    public MatchAdapter(Context context, List<MatchList> matchLists, OnMatchListener onMatchListener){
        this.context=context;
        this.matchLists=matchLists;
        this.onMatchListener=onMatchListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       view = LayoutInflater.from(context).inflate(R.layout.recyclerview_list,parent,false);
        internet_receiver = new internet_receiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(internet_receiver, intentFilter);
       return new MatchViewHolder(view,onMatchListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        final int[] flag = {0};
        Calendar calendar=Calendar.getInstance();
        MatchList matchList=matchLists.get(position);
        holder.MatchId.append(matchList.matchId);
        holder.Prize.setText(matchList.getPrize()+"/-");
        System.out.println("Onbind: "+matchList.getPrize());
        holder.Version.setText(matchList.version);
        holder.Type.setText(matchList.type);
        holder.PerKill.setText(matchList.perKill+"/-");
        holder.EntryFee.setText(matchList.entryFee+"/-");
        holder.Map.setText(matchList.map);
        time = matchList.date+" "+matchList.time;
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy kk:mm:ss ");
        SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy kk:mm");
        try {
            Date setTime=sdf1.parse(time);
            String time=DateUtils.formatDateTime(context, setTime.getTime(),  DateUtils.FORMAT_SHOW_TIME);
            holder.Date.append(matchList.date+"  "+time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.join.setEnabled(true);
            holder.join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Entry_Fee = matchList.entryFee;
                    time = matchList.date+" "+matchList.time;
                    match_Id = matchList.matchId;
                    fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                     if(internet_check==1)
                        join_match2(matchList.matchId, Entry_Fee);

                }
            });

        String time2;
        Date obj2=new Date();
        time2=sdf.format(obj2);
        long diff=0;
        try {
            Date obj3=sdf.parse(time2);
            Date obj1=sdf1.parse(time);
            diff = obj1.getTime() - obj3.getTime();
            System.out.println("difference between milliseconds: " + diff);
            String date1 = DateUtils.formatDateTime(context, obj1.getTime(),  DateUtils.FORMAT_SHOW_TIME);
            System.out.println("Actual Time:"+date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new CountDownTimer(diff,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                flag[0] =0;
            }
            @Override
            public void onFinish() {
                holder.join.setEnabled(false);
                holder.count.setTextColor(Color.RED);
                holder.count.setText("Match has started");
                flag[0] =1;
            }
        }.start();


        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Matches_Going");
        databaseReference.child(matchList.matchId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                holder.join.setText("Join");
                holder.checkBox.setVisibility(View.INVISIBLE);
                holder.cardView.setCardBackgroundColor(Color.WHITE);
                if(matchList.map.equals("TDM")){
                    count=8;
                    holder.players_joined.setMax(8);
                }
                else{
                    count=100;
                    holder.players_joined.setMax(100);
                }
                if(flag[0] ==0) {

                    if (matchList.map.equals("TDM")) {
                        holder.count.setText(String.valueOf((int) dataSnapshot.getChildrenCount()) + "/8");
                        holder.players_joined.setMax(8);
                        count=8;
                    }
                    else{
                        holder.count.setText(String.valueOf((int) dataSnapshot.getChildrenCount()) + "/100");
                        holder.players_joined.setMax(100);
                        count=100;
                    }
                    holder.players_joined.setProgress((int) dataSnapshot.getChildrenCount());
                }
                holder.players_joined.setProgress((int) dataSnapshot.getChildrenCount());
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Players players = ds.getValue(Players.class);


                    if(dataSnapshot.getChildrenCount()>=count)
                    {
                        holder.join.setEnabled(false);
                        holder.count.setTextColor(Color.RED);
                        holder.count.setText("Room is Full");
                    }

                    if (account.getEmail().equals(players.email)) {
                        holder.checkBox.setVisibility(View.VISIBLE);
                        holder.cardView.setCardBackgroundColor(Color.rgb(204,204,255));
                        holder.checkBox.setChecked(true);
                        holder.checkBox.setHighlightColor(Color.GREEN);
                        holder.checkBox.setClickable(false);
                        holder.join.setEnabled(false);
                        holder.join.setText("Joined");
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        view.setActivated(true);
    }

    @Override
    public void getNoConnectionValue(String text) {
        internet_check=0;
    }

    @Override
    public void getYesConnectionValue(String online, String text) {
        internet_check=1;

    }


    class MatchViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        TextView MatchId,Date,EntryFee,Map,PerKill,Type,Version,Prize,count;
        Button join;
        ProgressBar players_joined;
        ImageView imageView;
        CheckBox checkBox;
        MaterialCardView cardView;

        OnMatchListener onMatchListener;

        public MatchViewHolder(@NonNull View itemView,OnMatchListener onMatchListener) {
            super(itemView);
            account= GoogleSignIn.getLastSignedInAccount(itemView.getContext());
            MatchId=itemView.findViewById(R.id.match);
            Date=itemView.findViewById(R.id.date);
            EntryFee=itemView.findViewById(R.id.EntryFee);
            Map=itemView.findViewById(R.id.Map);
            PerKill=itemView.findViewById(R.id.perKill);
            Type=itemView.findViewById(R.id.Type);
            Version=itemView.findViewById(R.id.Version);
            Prize=itemView.findViewById(R.id.prize);
            join=itemView.findViewById(R.id.join);
            players_joined=itemView.findViewById(R.id.players_count);
            count=itemView.findViewById(R.id.count);
            cardView=itemView.findViewById(R.id.card);
            checkBox=itemView.findViewById(R.id.checkBox);
            imageView=itemView.findViewById(R.id.image);
                this.onMatchListener=onMatchListener;

            itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public void onClick(View v) {
            onMatchListener.OnMatchClick(getAdapterPosition(), imageView,ViewCompat.getTransitionName(imageView));
        }
    }
    public interface OnMatchListener{
        void OnMatchClick(int position,View view,String name);
    }


    public void join_match2(String match_Id, String entry_Fee){
        DatabaseReference usersClient=FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
        DatabaseReference matches_Going=FirebaseDatabase.getInstance().getReference("Matches_Going");
        usersClient.orderByChild("email").equalTo(account.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 user=null;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds:dataSnapshot.getChildren())
                            user=ds.getValue(User.class);
                        PUBGID=user.PUBG_ID;
                    bottomSheetDialogFragment = new JoinBottomSheet(context, MatchAdapter.this, match_Id, entry_Fee, user.Balance,user.PUBG_ID);
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                } else {
                    new MaterialAlertDialogBuilder(context).setTitle("No account").setMessage("Go to My account and update your details").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setCancelable(false).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }







    @Override
    public int getItemCount() {

        return matchLists.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPositiveButtonClick(String balance,String PUBG_ID ) {
        if(internet_check==1) {
            deduct_amount(context);
        }
        else {
            new MaterialAlertDialogBuilder(context).setTitle("No internet connection").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            }).show();
        }
            bottomSheetDialogFragment.dismiss();
    }

    @Override
    public void onNegativeButtonClick(String text) {
        bottomSheetDialogFragment.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deduct_amount(Context context){
        System.out.println(user.Balance);
        System.out.println(Entry_Fee);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Registering");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();


        final Double[] newWalletMoney = new Double[1];
        LocalDateTime localDateTime=LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        if (Double.parseDouble(user.Balance)>=Double.parseDouble(Entry_Fee))
        {
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            DocumentReference documentReference=db.collection("UsersBalance").document(user.PUBG_ID);
            db.runTransaction(new Transaction.Function<Double>() {
                @Nullable
                @Override
                public Double apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot documentSnapshot=transaction.get(documentReference);
                  newWalletMoney[0] =documentSnapshot.getDouble("Balance")-Double.parseDouble(Entry_Fee);
                    transaction.update(documentReference,"Balance", newWalletMoney[0]);

                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Double>() {
                @Override
                public void onSuccess(Double aDouble) {
                    Players players=new Players(user.PUBG_USERNAME,account.getEmail(),false,user.PUBG_ID,user.Token_Id);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Matches_Going");
                    DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");


                    databaseReference.child(match_Id).child(PUBGID).setValue(players).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                snackbar=Snackbar.make(view,"Slot registered successfully",Snackbar.LENGTH_SHORT);
                                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                snackbar.show();
                                databaseReference1.child(user.PUBG_ID).child("Balance").setValue(String.valueOf(newWalletMoney[0]));
                                Intent intent=new Intent(context, Receiver.class);
                                PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,intent,0);

                                AlarmManager alarmManager= (AlarmManager) context.getSystemService(ALARM_SERVICE);
                                String time2;

                                SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                                SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy kk:mm");
                                Date obj2=new Date();
                                time2=sdf.format(obj2);
                                long diff = 0;
                                try {
                                    Date obj3=sdf.parse(time2);
                                    Date obj1=sdf1.parse(time);
                                    // Toast.makeText(context, time2, Toast.LENGTH_SHORT).show();
                                    diff= obj1.getTime() - obj3.getTime();
                                    System.out.println("difference between milliseconds: " + diff);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long current_time=System.currentTimeMillis();

                                alarmManager.set(AlarmManager.RTC_WAKEUP, current_time+diff-300000,pendingIntent);
                                Transactions trans=new Transactions(Entry_Fee,dateTimeFormatter.format(localDateTime),"MatchID:"+match_Id,"0");
                                DatabaseReference databaseReference2=FirebaseDatabase.getInstance().getReference("Users_Client");
                                databaseReference2.child("Transactions").child(PUBGID).push().setValue(trans);
                                progressDialog.dismiss();
                            }
                            else{
                                if(!task.isSuccessful()){
                                    new MaterialAlertDialogBuilder(context).setTitle("Failed.").setMessage("There might be a technical problem.The deducted amount will be credited in to your account.Sorry for the inconvenience.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    }).setCancelable(true).show();
                                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                                    DocumentReference documentReference=db.collection("UsersBalance").document(user.PUBG_ID);
                                    db.runTransaction(new Transaction.Function<Double>() {
                                        @Nullable
                                        @Override
                                        public Double apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot documentSnapshot=transaction.get(documentReference);
                                            newWalletMoney[0] =documentSnapshot.getDouble("Balance")+Double.parseDouble(Entry_Fee);
                                            transaction.update(documentReference,"Balance", newWalletMoney[0]);

                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Double>() {
                                        @Override
                                        public void onSuccess(Double aDouble) {
                                            snackbar=Snackbar.make(view,"Refund done.",Snackbar.LENGTH_LONG);
                                            snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                            snackbar.show();
                                        }
                                    });
                                }
                            }

                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    snackbar=Snackbar.make(view,"Registration failed",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            });
        }
        else
        {
            new MaterialAlertDialogBuilder(context).setTitle("In sufficient balance!").setMessage("Recharge your wallet").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }
    }
}