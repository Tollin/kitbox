package com.unitec.kitbox.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unitec.kitbox.R;

public class CardViewAdapter extends RecyclerView.ViewHolder{
    private TextView dateInfo;
    private ImageView picInfo;

    public TextView getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(TextView dateInfo) {
        this.dateInfo = dateInfo;
    }

    public ImageView getPicInfo() {
        return picInfo;
    }

    public void setPicInfo(ImageView picInfo) {
        this.picInfo = picInfo;
    }

    public TextView getDesInfo() {
        return DesInfo;
    }

    public void setDesInfo(TextView desInfo) {
        DesInfo = desInfo;
    }

    private TextView DesInfo;
    public CardViewAdapter(@NonNull View itemView) {
        super(itemView);
        dateInfo = itemView.findViewById(R.id.date_bill);
        picInfo = itemView.findViewById(R.id.pic_address);
        DesInfo = itemView.findViewById(R.id.des_bill);
    }
}
