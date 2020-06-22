package com.unitec.kitbox.ui.capture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CaptureViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CaptureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Capture fragment");
        mText.setValue("Fuck");
    }



    public LiveData<String> getText() {
        return mText;
    }
}