package com.jumayu.blowbeep;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OnlinePassService extends Service {

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String userId;
    private PassengerInfo passengerInfo;
    Location location;
    FusedLocationProviderClient client;
    LocationCallback myLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG, "service is still on");
            DatabaseReference passLoc = FirebaseDatabase.getInstance().getReference().child("Passengers Online").child(userId).child("location");
            location = locationResult.getLastLocation();
            passengerInfo.setLocation(location);
            if (location != null) {
                passLoc.setValue(location);
            }
        }
    };

    private static final String TAG = "OnlinePassService";

    @Override
    public void onDestroy() {
        client.removeLocationUpdates(myLocationCallback);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        passengerInfo = new PassengerInfo();
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        userId=currentUser.getUid();
        requestLocationUpdates();
        Log.d(TAG, "pass service started");
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            Log.d(TAG, "before requesting loc");
            client.requestLocationUpdates(request, myLocationCallback, null);
        }
    }
}
