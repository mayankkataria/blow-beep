package com.jumayu.blowbeep.ui.passenger_gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.jumayu.blowbeep.R;

public class PassengerGalleryFragment extends Fragment {

    private PassengerGalleryViewModel passengerGalleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        passengerGalleryViewModel =
                ViewModelProviders.of(this).get(PassengerGalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_book, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        passengerGalleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}