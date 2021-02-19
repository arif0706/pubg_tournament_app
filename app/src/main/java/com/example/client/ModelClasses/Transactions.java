package com.example.client.ModelClasses;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transactions implements Comparable<Transactions>{
    public String amount,time,paid_to,From;
    Transactions(){}
    public Transactions(String amount, String time, String paid_to, String From){
        this.amount=amount;
        this.time=time;
        this.paid_to=paid_to;
        this.From=From;
    }

    @NonNull
    @Override
    public String toString() {
        return  amount+ " "+time+" "+paid_to+" "+From;
    }

    @Override
    public int compareTo(Transactions o) {

        long  diff=0;
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy kk:mm:ss ");
        SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy kk:mm");

        try {
            Date obj3=sdf.parse(this.time);
            Date obj1 = sdf1.parse(o.time);
            diff = obj1.getTime() - obj3.getTime();
            System.out.println("difference between milliseconds: " + diff);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (int) diff;
    }
}
//