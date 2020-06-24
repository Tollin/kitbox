package com.unitec.kitbox.Adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.unitec.kitbox.R;
import com.unitec.kitbox.event.ItemClickEvent;
import com.unitec.kitbox.models.ShareItem;
import com.unitec.kitbox.models.SiteModel;

import java.util.ArrayList;

public class HomeRecycleViewAdapter  extends RecyclerView.Adapter<CardViewAdapter> {
    private ArrayList<SiteModel> modelData;
    private ItemClickEvent itemClickEvent;
    public HomeRecycleViewAdapter(ArrayList<SiteModel> models, ItemClickEvent _itemclick) {
        modelData = models;
        itemClickEvent = _itemclick;
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
        final ShareItem item = model.getItems().get(0);
        holder.getTvItemName().setText(item.getName() + ":" + item.getCount());
        ImageView backgroundImage = holder.getImgBackground();
        backgroundImage.setTag(model);
        Picasso.get().load(model.getImages().get(0)).into(backgroundImage);
        holder.getTvCreator().setText(model.getLastUpdator());
        holder.getTvSiteName().setText(model.getSiteName());

        backgroundImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(itemClickEvent != null){
                    ImageView img = (ImageView)view;
                    itemClickEvent.siteClick((SiteModel) img.getTag());
                }
                return false;
            }
        });

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
