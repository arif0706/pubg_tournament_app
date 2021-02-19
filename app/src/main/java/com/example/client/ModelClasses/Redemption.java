package com.example.client.ModelClasses;

public class Redemption {
    public  String BHIM_ID,PUBG_USERNAME,amount,email,PUBGID,Date_of_req;
    public long seconds;
    public Redemption(){}


    public Redemption(String BHIM_ID,String PUBG_USERNAME,String amount,String email,String PUBGID,String Date_of_req,long seconds){
        this.BHIM_ID=BHIM_ID;
        this.PUBGID=PUBGID;
        this.Date_of_req=Date_of_req;
        this.amount=amount;
        this.PUBG_USERNAME=PUBG_USERNAME;
        this.email=email;
        this.seconds=seconds;
    }
}
