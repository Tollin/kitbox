package com.unitec.kitbox.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class ShareItem {
    private int Count;

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public Timestamp getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        ExpireDate = expireDate;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private Timestamp ExpireDate;
    private String Name;
}
