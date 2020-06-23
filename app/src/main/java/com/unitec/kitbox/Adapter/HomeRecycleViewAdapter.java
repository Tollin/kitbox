package com.unitec.kitbox.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.unitec.kitbox.R;
import com.unitec.kitbox.models.SiteModel;

import java.util.ArrayList;

public class HomeRecycleViewAdapter  extends RecyclerView.Adapter<CardViewAdapter> {
    private ArrayList<SiteModel> modelData;
    public HomeRecycleViewAdapter(ArrayList<SiteModel> models) {
        modelData = models;
    }

    @NonNull
    @Override
    public CardViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_item, parent, false);
        CardViewAdapter pvh = new CardViewAdapter(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewAdapter holder, int position) {
        SiteModel model = modelData.get(position);
        holder.getDesInfo().setText(model.getSiteName());
        Picasso.get().load(model.getImages()[0]).into(holder.getPicInfo());
//        holder.personAge.setText(persons.get(i).age);
//        holder.personPhoto.setImageResource(persons.get(i).photoId);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return modelData.size();
    }
}
