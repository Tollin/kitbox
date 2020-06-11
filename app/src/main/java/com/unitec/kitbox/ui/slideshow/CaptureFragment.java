package com.unitec.kitbox.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.unitec.kitbox.R;

public class CaptureFragment extends Fragment {

    private CaptureViewModel mCaptureViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mCaptureViewModel =
                ViewModelProviders.of(this).get(CaptureViewModel.class);
        View root = inflater.inflate(R.layout.fragment_capture, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        mCaptureViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
