package com.jumayu.blowbeep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DriverNavBar extends AppCompatActivity {

    private static final String TAG = "DriverNavBar";
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private DatabaseReference DriversOnlineRef, OnlineDriverIdRef;
    HashMap<String, Object> hashMap = new HashMap<>();
    String phNo;
    private DriverInfo driverInfo;
    private static final int PERMISSIONS_REQUEST = 2;
    private Intent serviceIntent, getIntent = getIntent();
    String driverId;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(serviceIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_navbar);

        if(getIntent!=null) driverId = getIntent.getStringExtra("driverId");

        driverInfo = new DriverInfo();
        serviceIntent = new Intent(this, OnlineDriverService.class);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        DriversOnlineRef = FirebaseDatabase.getInstance().getReference().child("Drivers Online");
        if(driverInfo != null) {
            LoginDriver(currentUser);
        }

        int locationPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(locationPermission == PackageManager.PERMISSION_GRANTED) {
            startService(serviceIntent);
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_driver_map, R.id.nav_gallery,  R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_dnavbar);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_driver_sign_out :
                        LogoutDriver();
                        Toast.makeText(DriverNavBar.this, "Signed Out.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    private void LogoutDriver() {
        OnlineDriverIdRef.setValue(null);
        AuthUI.getInstance()
                .signOut(getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent homeIntent = new Intent(DriverNavBar.this, AddPhone.class);
                        startActivity(homeIntent);
                        finish();
                    }
                });
        stopService(serviceIntent);
    }

    private void LoginDriver(FirebaseUser currentUser) {
        Log.d(TAG, "in LoginDriver");
        userId = currentUser.getUid();
        driverId = userId;
        OnlineDriverIdRef = DriversOnlineRef.child(driverId);
//        driverInfo.setPhNo(phNo);
        OnlineDriverIdRef.setValue(driverInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_navbar, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_dnavbar);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
