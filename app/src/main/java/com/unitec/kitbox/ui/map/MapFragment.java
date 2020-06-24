package com.unitec.kitbox.ui.map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.unitec.kitbox.R;
import com.unitec.kitbox.common.CommonFragment;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapFragment extends CommonFragment implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {
    private boolean dbready;
    private boolean mapready;
    private static final String TAG = "kitbox";
    private MapViewModel mapViewModel;
    private GoogleMap googleMap;
    private FirebaseFirestore db;
    private List<MapItem> lmi;
    private double lat;
    private double lon;
    private  SupportMapFragment mapFragment;
    private TextView closeBtn;
    private show sh;
    private String siteName ="";
    private String lastUpdator ="";
    private String creator = "";
    private GeoPoint siteLocation = new GeoPoint(0, 0);
    private String locationName = "";
    private String SelectedSiteId = "";
    private String imageUrl;
    public void draw () {
        Marker selectItem = null;
        if(dbready && mapready) {
            for (MapItem mi : lmi) {
                GeoPoint gp = mi.getSiteLocation();
                Log.d(TAG,  " MapItem=> " + mi.getId()+" "+mi.getImages().get(0));
                Double lat = 0.0;
                Double lon = 0.0;
                if (gp != null) {
                    try{
                        lat = gp.getLatitude();
                        lon = gp.getLongitude();
                    }catch(NullPointerException e){
                    }
                }
                LatLng myplce = new LatLng(lat, lon );
                Marker a = googleMap.addMarker(new MarkerOptions().position(myplce).title(mi.getSiteName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(myplce));
                a.setTag(mi.getId());
                if(SelectedSiteId != "" && SelectedSiteId.equals(mi.getId())){
                    selectItem = a;
                }
            }


        } else {
            return;
        }
        if(selectItem != null){
            onMarkerClick(selectItem);
            int padding = 15; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom (selectItem.getPosition(), padding);

            googleMap.moveCamera(cu);
            googleMap.animateCamera(cu);
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapready = false;
        dbready = false;
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
//        textView = root.findViewById(R.id.text_map);
//        imageView = root.findViewById(R.id.imageView);
        sh = root.findViewById(R.id.show0);
        closeBtn = sh.getT0();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sh.setVisibility(View.GONE);
            }
        });
//        textView.setVisibility(View.GONE);
        mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        mapFragment =  (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMapInstance) {
//                googleMap = googleMapInstance;
//            }
//        });
        //////////////////////////
        lmi = new ArrayList<MapItem>();
        db = FirebaseFirestore.getInstance();
        db.collection("Sites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dbready = true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> images = new ArrayList<String>();
                                List<Item> items = new ArrayList<Item>();
                                try{
                                    images.clear();
                                    items.clear();
                                    List<HashMap<String, Object>> lhm = (List<HashMap<String, Object>> )document.getData().get("items");
                                    for(HashMap<String, Object> hm : lhm){
                                        items.add(new Item(hm));
                                    }
                                    List<String> is = (ArrayList<String>)document.getData().get("images");
                                    for(String s : is){
                                        images.add(s);
                                    }
                                    siteName = (String)document.getData().get("siteName");
                                    lastUpdator = (String)document.getData().get("lastUpdator");
                                    creator = (String)document.getData().get("creator");
                                    siteLocation =(GeoPoint)document.getData().get("siteLocation");
                                    locationName = (String)document.getData().get("locationName");
//                                Log.d(TAG, document.getId() + " lhm=> " + items.get(0).getName());
//                                Log.d(TAG, document.getId() + " values=> " + document.getData().values());
//                                Log.d(TAG, document.getId() + " keys=> " + document.getData().keySet());

                                    Log.d(TAG, document.getId() + " Images=> " + images.get(0));
//                                Log.d(TAG, document.getId() + " Images=> " + document.getData().get("Images"));
//                                Log.d(TAG, document.getId() + " SiteName=> " + document.getData().get("SiteName"));
                                }catch(NullPointerException e){
                                }

                                MapItem mi = new MapItem((String)document.getId(),
                                        siteName,
                                        images,
                                        lastUpdator,
                                        items,
                                        creator,
                                        siteLocation,
                                        locationName);
                                lmi.add(mi);

                            }
                            draw();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        Bundle bundle = getArguments();
        if(bundle != null){
            SelectedSiteId = bundle.getString(TransferItemIdKey);
        }
        return root;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
////        Integer clickCount = (Integer) marker.getTag();
//        Log.d(TAG, "clickCount "+ clickCount+" "+
//                marker.getTitle());
        for(MapItem mi : lmi) {
            Log.d(TAG, "clickCount "+mi.getId()+" "+
                    marker.getTag());
            if(mi.getId().equals(marker.getTag())) {
                Log.d(TAG, "clickCount "+mi.getImages().get(0));
                Picasso.get().load(mi.getImages().get(0)).into(sh.getI0());
                sh.setVisibility(View.VISIBLE);
                sh.fill(mi.getSiteName(), mi.getCreator(),
                        ""+mi.getSiteLocation().getLatitude()+", "+mi.getSiteLocation().getLongitude());
            }
        }

        return false;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mapready = true;
        googleMap = mMap;
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        draw();
    }

}
