package com.jumayu.blowbeep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverStops extends AppCompatActivity {

    TextInputEditText StopNameEt;
    Button DoneBtn, AddBtn;
    int stopNum = 1;
    DriverInfo driverInfo;
    FirebaseAuth mAuth;
    FirebaseUser driver;
    String driverId;
    Map<String, ArrayList<String>> stopMap;
    List<String> stopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_stops);

        StopNameEt = findViewById(R.id.stop_name_et);
        DoneBtn = findViewById(R.id.done_btn);
        AddBtn = findViewById(R.id.add_stop_btn);
        mAuth = FirebaseAuth.getInstance();
        driver = mAuth.getCurrentUser();
        driverId = driver.getUid();
        stopList = new ArrayList<>();
        stopMap = new HashMap<>();
        driverInfo = new DriverInfo();

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StopNameEt.getText()!=null) {
                    String stopName = StopNameEt.getText().toString();
                    stopList.add(stopName);
                    StopNameEt.getText().clear();
                    StopNameEt.setHint("Stop " + ++stopNum);
                }
                else Toast.makeText(DriverStops.this, "Please enter stop name.", Toast.LENGTH_SHORT).show();

            }
        });

        DoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference driversRegistered = FirebaseDatabase.getInstance().getReference().child("Drivers Registered");
                DatabaseReference RegisteredDriverIdRef = driversRegistered.child(driverId);
                DatabaseReference StopsRef = RegisteredDriverIdRef.child("Stops");
                for(int i=0;i<stopList.size();i++) {
                    StopsRef.push().child("Stop Name").setValue(stopList.get(i));
                }
//                driversRegistered.updateChildren(driverInfo);
                Intent dSignInIntent = new Intent(DriverStops.this, DriverNavBar.class);
                dSignInIntent.putExtra("driverId", driverId);
                startActivity(dSignInIntent);
                finish();
            }
        });
    }
}
