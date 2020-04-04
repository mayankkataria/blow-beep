package com.jumayu.blowbeep.ui.driver_map;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jumayu.blowbeep.R;

import java.util.HashMap;

public class DriverMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "DriverMapFragment";
    private DriverMapViewModel driverMapViewModel;
    private GoogleMap mMap;
    private static final String tag = "DailyRideMap";
    private Boolean locationPermissionGranted=false;
    private int REQUEST_PERMISSION_CODE=1000;
    private LocationManager locationManager;
    private Location lastLocation;
    private Fragment hostFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private Button findPassBtn;
    private DatabaseReference passOnlineRef;
    private String passOnlineId;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private Context mContext;
    private Activity activity;
    IntentFilter filter = new IntentFilter("PASS_ID_INTENT");
    private BroadcastReceiver mReceiver;

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getActivity().registerReceiver(mReceiver, filter);
                passOnlineId = intent.getStringExtra("pass_id");
                Log.d(TAG, "receiver registered, passId = " + passOnlineId);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    //    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        driverMapViewModel =
//                ViewModelProviders.of(this).get(DriverMapViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_driver_map, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        driverMapViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;

        if (context instanceof Activity){
            activity=(Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_driver_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter("PASS_ID_INTENT"));
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        hostFragment = getFragmentManager().findFragmentById(R.id.nav_host_fragment_dnavbar);
        passOnlineRef = FirebaseDatabase.getInstance().getReference().child("Passengers Online");
        findPassBtn = rootView.findViewById(R.id.find_pass_btn);
        findPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPassenger();
            }
        });
        return rootView;
    }

    private void findPassenger() {
        passOnlineRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                passOnlineId = dataSnapshot.getKey();
                DatabaseReference passOnlineIdRef = passOnlineRef.child(passOnlineId);
                DatabaseReference locRef = passOnlineIdRef.child("location");
                locRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        setMarker(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setMarker(DataSnapshot locSnapshot) {
        HashMap<String, Object> passOnline = (HashMap<String, Object>) locSnapshot.getValue();
        double lat = Double.parseDouble(passOnline.get("latitude").toString());
        double lng = Double.parseDouble(passOnline.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().title("pass 1").position(location));
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

//    @SuppressLint("MissingPermission")
//    private void lmRequestLocationUpdates() {
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                Log.d("In onLocChange", "In onLocChange");
//                lastLocation=location;
//            }

//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        });
//    }

    private boolean checkLocationPermissions() {
        // check permission
        Log.d(tag, "checking");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // request for permission
            Log.d(tag, "permission not granted");
            requestPermissions(new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET}, REQUEST_PERMISSION_CODE);
            return false;

        }
        else {
            //permission granted
            Log.d(tag, "permission granted");
            locationPermissionGranted=true;
            mMap.setMyLocationEnabled(true);
            fetchLastKnownLocation();
//            lmRequestLocationUpdates();
            return true;
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
                getFragmentManager().beginTransaction()
                        .detach(hostFragment)
                        .attach(hostFragment)
                        .commit();
            }
        }
    }
}
