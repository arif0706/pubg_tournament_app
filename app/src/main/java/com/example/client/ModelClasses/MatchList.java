package com.example.client.ModelClasses;

public class MatchList {
    public String date,entryFee,fprize,map,matchId,perKill,time,type,version,password,roomId;
    public MatchList(){}

    public MatchList(String date, String entryFee, String fprize, String map, String matchId, String perKill, String time, String type, String version,String password,String roomId) {
        this.password=password;
        this.roomId=roomId;
        this.date = date;
        this.entryFee = entryFee;
        this.fprize= fprize;
        System.out.println("onConstructor: "+fprize);
        this.map = map;
        this.matchId = matchId;
        this.perKill = perKill;
        this.time = time;
        this.type = type;
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(String entryFee) {
        this.entryFee = entryFee;
    }

    public String getPrize() {
        return fprize;
    }

    public void setPrize(String prize) {
        this.fprize = prize;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getPerKill() {
        return perKill;
    }

    public void setPerKill(String perKill) {
        this.perKill = perKill;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
