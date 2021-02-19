package com.example.client.ModelClasses;


public class User {
    public String email,Name,PUBG_ID,Balance,Kills,Wins,PUBG_USERNAME,BHIM_ID,Token_Id,block,photo_url,referral_code,referral_code_flag,pin;
            public  int no_of_matches,winning_money,total_money_earned;
    public User(){}

    public User(String email,String Name,String PUBG_ID,String Balance,String Kills,String Wins,String PUBG_USERNAME,String BHIM_ID,String Token_Id,String block,int no_of_matches,String photo_url,int winning_money,int total_money_earned,String referral_code,String referral_code_flag,String pin){
        this.pin=pin;
        this.referral_code_flag=referral_code_flag;
        this.total_money_earned=total_money_earned;
        this.winning_money=winning_money;
        this.email=email;
        this.Name=Name;
        this.PUBG_ID= PUBG_ID;
        this.Balance=Balance;
        this.Kills=Kills;
        this.Wins=Wins;
        this.PUBG_USERNAME=PUBG_USERNAME;
        this.BHIM_ID=BHIM_ID;
        this.Token_Id=Token_Id;
        this.no_of_matches=no_of_matches;
        this.block=block;
        this.photo_url=photo_url;
        this.referral_code=referral_code;

    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", Name='" + Name + '\'' +
                ", PUBG_ID='" + PUBG_ID + '\'' +
                ", Balance='" + Balance + '\'' +
                ", Kills='" + Kills + '\'' +
                ", Wins='" + Wins + '\'' +
                ", PUBG_USERNAME='" + PUBG_USERNAME + '\'' +
                ", BHIM_ID='" + BHIM_ID + '\'' +
                ", Token_Id='" + Token_Id + '\'' +
                ", block='" + block + '\'' +
                ", photo_url='" + photo_url + '\'' +
                ", referral_code='" + referral_code + '\'' +
                ", referral_code_flag='" + referral_code_flag + '\'' +
                ", pin='" + pin + '\'' +
                ", no_of_matches=" + no_of_matches +
                ", winning_money=" + winning_money +
                ", total_money_earned=" + total_money_earned +
                '}';
    }
}
