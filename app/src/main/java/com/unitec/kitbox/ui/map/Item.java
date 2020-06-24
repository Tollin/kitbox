package com.unitec.kitbox.ui.map;

import com.google.firebase.Timestamp;

import java.util.Map;

public class Item implements Cloneable{
    private Long Count;
    private Timestamp ExpireDate;
    private String Name;
    public Item (Map<String, Object> hm) {
        this.Count =   (Long)hm.get("Count");
        this.ExpireDate =  (Timestamp) hm.get("ExpireDate");
        this.Name =   (String) hm.get("Name");
    }
    public Object clone() {
        Item o = null;
        try {
            o = (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    public Long getCount() {
        return Count;
    }
    public void setCount(Long Count) {
        this.Count = Count;
    }
    public Timestamp getExpireDate() {
        return ExpireDate;
    }
    public void setExpireDate(Timestamp ExpireDate) {
        this.ExpireDate = ExpireDate;
    }
    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
}

