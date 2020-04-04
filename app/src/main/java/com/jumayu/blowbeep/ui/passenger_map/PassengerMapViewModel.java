package com.jumayu.blowbeep.ui.passenger_map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PassengerMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PassengerMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Daily Ride Clicked.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}