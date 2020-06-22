package com.unitec.kitbox.ui.capture;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.camerakit.CameraKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.unitec.kitbox.R;
import com.unitec.kitbox.tensorflowlite.Classifier;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CaptureFragment extends Fragment {

    private CaptureViewModel mCaptureViewModel;

    private static final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    private static final boolean QUANT = true;
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private ImageView imageViewResult;
    private CameraKitView cameraView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_capture, container, false);

//        cameraView = root.findViewById(R.id.cameraView);
        imageViewResult = root.findViewById(R.id.imageViewResult);
        textViewResult = root.findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnToggleCamera = root.findViewById(R.id.btnToggleCamera);
        btnDetectObject = root.findViewById(R.id.btnDetectObject);

        return root;
    }
//        mCaptureViewModel =
//                ViewModelProviders.of(this).get(CaptureViewModel.class);
//
//        View root = inflater.inflate(R.layout.fragment_capture, container, false);
//
//        final TextView textView = root.findViewById(R.id.text_capture);
//
//        mCaptureViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
//    }


}
