package com.jumayu.blowbeep.ui.passenger_gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PassengerGalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PassengerGalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}