package com.jumayu.blowbeep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

public class PassengerNavBar extends AppCompatActivity {

    private static final String TAG = "PassengerNavBar";
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference PassengersOnlineRef, OnlinePassIdRef;
    private String userId;
    private HashMap<String, Object> hashMap = new HashMap<>();
    String phNo;
    private static final int PERMISSIONS_REQUEST = 1;
    PassengerInfo passInfo;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_navbar);

        if(getIntent()!=null)  phNo = getIntent().getStringExtra("phNo");
        serviceIntent = new Intent(this, OnlinePassService.class);
        passInfo = new PassengerInfo();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        PassengersOnlineRef = FirebaseDatabase.getInstance().getReference().child("Passengers Online");
        LoginPassenger(currentUser);

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
                R.id.nav_passenger_map, R.id.nav_book)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_pnavbar);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_pass_sign_out :
                        LogoutPassenger();
                        AuthUI.getInstance()
                                .signOut(getApplicationContext())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent homeIntent = new Intent(PassengerNavBar.this, AddPhone.class);
                                        startActivity(homeIntent);
                                        finish();
                                    }
                                });
                        Toast.makeText(PassengerNavBar.this, "Signed Out.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    private void LogoutPassenger() {
        stopService(new Intent(PassengerNavBar.this, OnlinePassService.class));
        OnlinePassIdRef.setValue(null);
    }

    private void LoginPassenger(FirebaseUser currentUser) {
        userId = currentUser.getUid();
        Intent passIdIntent = new Intent("PASS_ID_INTENT").putExtra("passId", userId);
        sendBroadcast(passIdIntent);
        OnlinePassIdRef = PassengersOnlineRef.child(userId);
        passInfo.setPhNo(phNo);
        OnlinePassIdRef.setValue(passInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.passenger_navbar, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_pnavbar);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_pnavbar);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }
}
