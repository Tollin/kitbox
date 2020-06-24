package com.unitec.kitbox.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unitec.kitbox.R;

public class CardViewAdapter extends RecyclerView.ViewHolder{
    private TextView tvSiteName;
    private ImageView imgBackground;
    private TextView tvItemName;
    private TextView tvCreator;

    public TextView getTvCreator() {
        return tvCreator;
    }

    public void setTvCreator(TextView tvCreator) {
        this.tvCreator = tvCreator;
    }

    public TextView getTvSiteName() {
        return tvSiteName;
    }

    public void setTvSiteName(TextView tvSiteName) {
        this.tvSiteName = tvSiteName;
    }

    public ImageView getImgBackground() {
        return imgBackground;
    }

    public void setImgBackground(ImageView imgBackground) {
        this.imgBackground = imgBackground;
    }

    public TextView getTvItemName() {
        return tvItemName;
    }

    public void setTvItemName(TextView tvItemName) {
        this.tvItemName = tvItemName;
    }

    public CardViewAdapter(@NonNull View itemView) {
        super(itemView);
        tvCreator = itemView.findViewById(R.id.tv_creator);
        imgBackground = itemView.findViewById(R.id.img_background);
        tvItemName = itemView.findViewById(R.id.tv_itemname);
        tvSiteName = itemView.findViewById(R.id.tv_sitename);
    }
}
