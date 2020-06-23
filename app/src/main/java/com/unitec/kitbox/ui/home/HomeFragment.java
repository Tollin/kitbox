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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unitec.kitbox.R;
import com.unitec.kitbox.common.CommonFragment;

public class HomeFragment extends CommonFragment implements View.OnClickListener {

    private ImageButton imgBtnRefresh;
    private RecyclerView recyclerViewList;

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
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase(){
       SitesCollection
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               Log.i(LogTag, document.get("Creator").toString());
                               
                           }
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
}
