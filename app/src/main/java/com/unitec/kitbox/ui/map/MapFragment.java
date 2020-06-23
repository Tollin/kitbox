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
    private TextView textView;
    private ImageView imageView;
    private String imageUrl;
    public void draw () {
        if(dbready && mapready) {
            for (MapItem mi : lmi) {
                Log.d(TAG,  " MapItem=> " + mi.getImages());
                Log.d(TAG,  " MapItem=> " + mi.getItems().get(0).getName());
                Log.d(TAG,  " MapItem=> " + mi.getImages().get(0));
                LatLng myplce = new LatLng(1, 1);
                Marker a = googleMap.addMarker(new MarkerOptions().position(myplce).title(mi.getSiteName()));
                a.setTag(0);
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
        textView = root.findViewById(R.id.text_map);
        imageView = root.findViewById(R.id.imageView);
        textView.setVisibility(View.GONE);
        mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
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
                                List<HashMap<String, Object>> lhm = (List<HashMap<String, Object>> )document.getData().get("Items");
                                for(HashMap<String, Object> hm : lhm){
                                    items.add(new Item(hm));
                                }
                                List<String> images = new ArrayList<String>();
                                List<String> is = (ArrayList<String>)document.getData().get("Images");
                                for(String s : is){
                                    images.add(s);
                                }
//                                Log.d(TAG, document.getId() + " lhm=> " + items.get(0).getName());
                                MapItem mi = new MapItem((String)document.getData().get("SiteName"),
                                        images,
                                        (String)document.getData().get("LastUpdator"),
                                        items,
                                        (String)document.getData().get("Creator"),
                                        (GeoPoint)document.getData().get("SiteLocation"),
                                        (String)document.getData().get("LocationName"));
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
        Integer clickCount = (Integer) marker.getTag();
        Log.d(TAG, "clickCount "+ clickCount+" "+
                marker.getTitle());
        // Check if a click count was set, then display the click count.
        if(clickCount % 2 == 0 ) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        textView.setText(marker.getTitle());
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);

        }

        return false;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mapready = true;
        googleMap = mMap;
        draw();
    }

}
