package com.example.client.ModelClasses;

import android.icu.text.UnicodeSetSpanner;

public class Players {
   public String PUBG_ID,email,PUBG_USERNAME,Token_Id;
  public Boolean rewarded;
    public Players(String PUBG_USERNAME, String email, Boolean rewarded, String PUBG_ID, String Token_Id){
        this.PUBG_USERNAME=PUBG_USERNAME;
        this.Token_Id= Token_Id;
        this.PUBG_ID=PUBG_ID;
        this.email=email;
        this.rewarded=rewarded;
    }
    Players(){}

}
