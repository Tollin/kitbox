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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements
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
//    private TextView textView;
//    private ImageView imageView;
    private String imageUrl;
    public void draw () {
        if(dbready && mapready) {
            for (MapItem mi : lmi) {
                GeoPoint gp = mi.getSiteLocation();
                Log.d(TAG,  " MapItem=> " + mi.getId());
                Log.d(TAG,  " MapItem=> " + gp.getLatitude());
                Log.d(TAG,  " MapItem=> " + gp.getLongitude());
                LatLng myplce = new LatLng(gp.getLatitude(), gp.getLongitude());
                Marker a = googleMap.addMarker(new MarkerOptions().position(myplce).title(mi.getSiteName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(myplce));
                a.setTag(mi.getId());

            }


        } else {
            return;
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
                                List<Item> items = new ArrayList<Item>();
                                List<HashMap<String, Object>> lhm = (List<HashMap<String, Object>> )document.getData().get("items");
                                for(HashMap<String, Object> hm : lhm){
                                    items.add(new Item(hm));
                                }
                                List<String> images = new ArrayList<String>();
                                List<String> is = (ArrayList<String>)document.getData().get("images");
                                for(String s : is){
                                    images.add(s);
                                }
//                                Log.d(TAG, document.getId() + " lhm=> " + items.get(0).getName());
                                MapItem mi = new MapItem((String)document.getId(),
                                        (String)document.getData().get("siteName"),
                                        images,
                                        (String)document.getData().get("lastUpdator"),
                                        items,
                                        (String)document.getData().get("creator"),
                                        (GeoPoint)document.getData().get("siteLocation"),
                                        (String)document.getData().get("locationName"));
                                lmi.add(mi);
//                                Log.d(TAG, document.getId() + " values=> " + document.getData().values());
//                                Log.d(TAG, document.getId() + " keys=> " + document.getData().keySet());

                                Log.d(TAG, document.getId() + " Images=> " + images.get(0));
//                                Log.d(TAG, document.getId() + " Images=> " + document.getData().get("Images"));
//                                Log.d(TAG, document.getId() + " SiteName=> " + document.getData().get("SiteName"));
                            }
                            draw();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return root;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
////        Integer clickCount = (Integer) marker.getTag();
//        Log.d(TAG, "clickCount "+ clickCount+" "+
//                marker.getTitle());
        for(MapItem mi : lmi) {
//            Log.d(TAG, "clickCount "+mi.getId()+" "+
//                    marker.getTag());
            if(mi.getId().equals(marker.getTag())) {
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
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(1000), 1000, null);
        draw();
    }

}
