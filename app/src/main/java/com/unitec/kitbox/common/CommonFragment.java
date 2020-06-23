package com.unitec.kitbox.common;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unitec.kitbox.MainActivity;

public class CommonFragment extends Fragment {
    protected FirebaseDatabase dbInstance;
    protected MainActivity mainActivity;
    protected DatabaseReference SitesCollection;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        dbInstance = mainActivity.getFirebaseDatabase();
        SitesCollection = mainActivity.getSitesCollection();
    }
}
