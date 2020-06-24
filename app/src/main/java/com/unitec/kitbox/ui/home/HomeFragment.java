package com.unitec.kitbox.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unitec.kitbox.Adapter.HomeRecycleViewAdapter;
import com.unitec.kitbox.R;
import com.unitec.kitbox.common.CommonFragment;
import com.unitec.kitbox.event.ItemClickEvent;
import com.unitec.kitbox.models.ShareItem;
import com.unitec.kitbox.models.SiteModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HomeFragment extends CommonFragment implements View.OnClickListener, ItemClickEvent {

    private ImageButton imgBtnRefresh;
    private RecyclerView recyclerViewList;
    private ArrayList<SiteModel> sites = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        imgBtnRefresh = root.findViewById(R.id.imgBtn_Refresh);
        recyclerViewList = root.findViewById(R.id.home_recycleview);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());

        recyclerViewList.setLayoutManager(llm);
        // bring to top of screen
        imgBtnRefresh.bringToFront();
        imgBtnRefresh.setOnClickListener(this);
        sites = new ArrayList();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase(){
       final ItemClickEvent eventListener = this;
       SitesCollection
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           sites.clear();
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               Log.i(LogTag, document.getString(SiteModel.CreatorKey));
                                SiteModel site = new SiteModel();
                                site.setSiteModelId(document.getId());
                                site.setCreator(document.getString(SiteModel.CreatorKey));
                                site.setLastUpdator(document.getString(SiteModel.LastUpdatorKey));
                               site.setLocationName(document.getString(SiteModel.LocationNameKey));
                               site.setSiteName(document.getString(SiteModel.SiteNameKey));
                               site.setSiteLocation(document.getGeoPoint(SiteModel.SiteLocationKey));
                               ArrayList<String> images =  (ArrayList<String>) document.get(SiteModel.ImagesKey);
                               if(images!=null){
                                   ArrayList<String> imageUrls = new ArrayList<String>();
                                   site.setImages(imageUrls);
                                for (String imageUrl: images){
                                    imageUrls.add(imageUrl);
                                }
                               }
                               ArrayList<HashMap> sharedItems = (ArrayList<HashMap>) document.get(SiteModel.ItemsKey);
                               if(sharedItems != null){
                                   ArrayList<ShareItem> items = new ArrayList<ShareItem>();
                                   site.setItems(items);
                                   for (HashMap<String, Object> sharedItem: sharedItems){
                                       ShareItem item = new ShareItem();
                                       item.setCount(Integer.parseInt(sharedItem.get(ShareItem.CountKey).toString()));
                                       item.setName(sharedItem.get(ShareItem.NameKey).toString());
                                       item.setExpireDate(((Timestamp)sharedItem.get(ShareItem.ExpireDateKey)));
                                       items.add(item);
                                   }
                               }
                               sites.add(site);
                           }

                           HomeRecycleViewAdapter adapter = new HomeRecycleViewAdapter(sites, (ItemClickEvent) eventListener);
                           recyclerViewList.setAdapter(adapter);
                       } else {
                           Toast.makeText(getContext(), "faild to load data from server", Toast.LENGTH_LONG)
                                   .show();
                       }
                   }
               });
    }

    /**
     * refresh click
     * @param view
     */
    @Override
    public void onClick(View view) {
        loadDataFromFirebase();
    }

    @Override
    public void siteClick(SiteModel site) {
        Bundle bundle = new Bundle();
        bundle.putString(TransferItemIdKey, site.getSiteModelId());
        navController.navigate(R.id.nav_map, bundle);
    }
}
