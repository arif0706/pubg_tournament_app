package com.example.client.Notification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.client.ListOfPlayers;
import com.example.client.Logged1;
import com.example.client.MainActivity;
import com.example.client.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            Intent intent1=new Intent( context, Logged1.class);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.navigation_icon)
                        .setContentTitle("Reminder")
                        .setContentText("Match is about to begin!")
                        .setAutoCancel(true)
                        .setContentIntent(PendingIntent.getActivity(context,0,intent1,0))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, mBuilder.build());
    }
}
