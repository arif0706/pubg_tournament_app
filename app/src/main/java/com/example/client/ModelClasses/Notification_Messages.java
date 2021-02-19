package com.example.client.ModelClasses;

public class Notification_Messages {
    public String Date,message,title,key;
    public boolean expanded,seen;
    Notification_Messages(){
        this.expanded=false;

    }
    public Notification_Messages(String Date, String message, String title, boolean seen){
        this.Date=Date;
        this.message=message;
        this.title=title;
        this.seen=seen;
    }
    public boolean isExpanded() {
        return expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
