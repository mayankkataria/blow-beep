package com.jumayu.blowbeep.ui.passenger_map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.jumayu.blowbeep.R;

public class PassengerMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PassengerMapViewModel homeViewModel;
    private static final String tag = "DailyRideMap";
    private Boolean locationPermissionGranted=false;
    private int REQUEST_PERMISSION_CODE=1000;
    private LocationManager locationManager;
    private Location lastLocation;
    FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    TextInputEditText searchStopEt;

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                ViewModelProviders.of(getActivity()).get(PassengerMapViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_passenger_map, container, false);
//        final TextView textView = root.findViewById(R.id.text_passenger_map);
//        homeViewModel.getText().observe(getActivity(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
//    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_passenger_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        searchStopEt = rootView.findViewById(R.id.search_stop_et);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        searchStopEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

    private void fetchLastKnownLocation() {
        mMap.setMyLocationEnabled(true);
        Log.d("In fetch", "In fetch");
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location)
            {
                if(location!=null){
                    lastLocation=location;
                    Log.d("lastLoc", "" + lastLocation);
                    LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void lmRequestLocationUpdates() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("In onLocChange", "In onLocChange");
                lastLocation=location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    private void checkLocationPermissions() {
        // check permission
        Log.d(tag, "checking");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted=false;
            // request for permission
            Log.d(tag, "permission not granted");
            requestPermissions(new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET}, REQUEST_PERMISSION_CODE);

        }
        else {
            //permission granted
            Log.d(tag, "permission granted");
            locationPermissionGranted=true;
            mMap.setMyLocationEnabled(true);
            fetchLastKnownLocation();
            lmRequestLocationUpdates();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(tag, "map ready");
        mMap = googleMap;
        checkLocationPermissions();
        if(locationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            fetchLastKnownLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                Log.d(tag, "requested");
                locationPermissionGranted = true;
                checkLocationPermissions();
            }
        }
    }
}
