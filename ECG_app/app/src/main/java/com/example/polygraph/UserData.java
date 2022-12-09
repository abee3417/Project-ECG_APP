package com.example.polygraph;

public class UserData {

    public UserData() { }

    private String UserTime;
    private String memo;
    private String lie;
    private String Uid;

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public String getMemo() {return memo;}

    public void setMemo(String memo) {this.memo = memo;
    }
    public String getLie() {
        return lie;
    }

    public void setLie(String lie) {
        this.lie = lie;
    }

    public void setUserTime(String userTime) {
        UserTime = userTime;
    }

    public String getUserTime() {
        return UserTime;
    }



}

