package com.jumayu.blowbeep.ui.driver_map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DriverMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DriverMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}