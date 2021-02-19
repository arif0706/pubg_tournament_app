package com.example.client.ModelClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.NetworkInfo;
import android.os.Build;

import android.widget.Toast;

import androidx.annotation.RequiresApi;


public class internet_receiver extends BroadcastReceiver {
    private getConnection connection;
    int value=0;
    internet_receiver(){}
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        String status =NetworkUtil.getConnectivityStatusString(context);
        value=0;
        if(status.equals("0")) {
            status = "No internet connection";
            connection.getNoConnectionValue(status);
        }
        else
        {
            connection.getYesConnectionValue("Online",status);
        }

    }
    public static boolean isConnectedFast(Context context){
        NetworkInfo info = NetworkUtil.getNetworkInfo(context);
        return (info != null && info.isConnected() && NetworkUtil.isConnectionFast(info.getType(),info.getSubtype()));
    }


    public internet_receiver(getConnection connection){
        this.connection=connection;
        }
   public interface getConnection{
        void getNoConnectionValue(String text);
        void getYesConnectionValue(String online, String text);
   }
}

