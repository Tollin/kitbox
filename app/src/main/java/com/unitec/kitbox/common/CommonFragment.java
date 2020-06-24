package com.unitec.kitbox.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.unitec.kitbox.MainActivity;

public class CommonFragment extends Fragment {
    protected MainActivity mainActivity;
    protected CollectionReference SitesCollection;
    protected static final String LogTag = "kitbox";
    protected NavController navController;
    protected static final String TransferItemIdKey = "sitemodelkey";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        SitesCollection = mainActivity.getSitesCollection();
        navController = mainActivity.getNavController();
    }
}
