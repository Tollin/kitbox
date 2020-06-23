package com.unitec.kitbox.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unitec.kitbox.R;

public class HomeRecycleViewAdapter extends RecyclerView.ViewHolder{
    private TextView dateInfo;
    private ImageView picInfo;
    private TextView DesInfo;
    public HomeRecycleViewAdapter(@NonNull View itemView) {
        super(itemView);
        dateInfo = itemView.findViewById(R.id.date_bill);
        picInfo = itemView.findViewById(R.id.pic_address);
        DesInfo = itemView.findViewById(R.id.des_bill);
    }
}
