package com.jumayu.blowbeep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AddPhone extends AppCompatActivity {

    private static final String TAG = "AddPhone";
    Button sendOtpBtn;
    String phNo="";
    EditText phNoEt;
    EditText enterOtpEt;
    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    Button signInButton;
    String verificationCode;
    RadioGroup identityRg;
    RadioButton passengerRb;
    RadioButton driverRb;
    String dFakePhoneNum = "+16505551234";
    String dFakeOtpCode = "123456";
    String pFakePhoneNum = "+16505554567";
    String pFakeOtpCode = "456789";
    Boolean userIsPassenger=false, userIsDriver=false;
    String userId, enteredOtp;
    HashMap<String, Object> hashMap = new HashMap<>();
    FirebaseUser user;
    DatabaseReference DriversRegisteredRef, PassengersRegisteredRef, RegisteredDriverIdRef, RegisteredPassengerIdRef;
    DriverInfo driverInfo;
    PassengerInfo passengerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);

        sendOtpBtn = findViewById(R.id.send_otp_btn);
        phNoEt = findViewById(R.id.phno_et);
        signInButton = findViewById(R.id.sign_in_btn);
        enterOtpEt = findViewById(R.id.enter_otp_et);
        identityRg = findViewById(R.id.identity_rg);
        passengerRb = findViewById(R.id.pass_rb);
        driverRb = findViewById(R.id.driver_rb);
        mAuth = FirebaseAuth.getInstance();
        driverInfo = new DriverInfo();
        passengerInfo = new PassengerInfo();
        Log.d(TAG, "created");


        identityRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.pass_rb) {
                    //user is passenger
                    userIsPassenger=true;
                    userIsDriver=false;
//                    phNo=pFakePhoneNum;
//                    enteredOtp=pFakeOtpCode;
                    Log.d(TAG, "phNo : " + phNo);
                    Log.d(TAG, "enteredOtp : " + enteredOtp);
                }
                if(checkedId == R.id.driver_rb) {
                    //user is driver
                    userIsDriver=true;
                    userIsPassenger=false;
//                    phNo=dFakePhoneNum;
//                    enteredOtp=dFakeOtpCode;
                    Log.d(TAG, "phNo : " + phNo);
                }
            }
        });

        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phNo=phNoEt.getText().toString();
                if(identityRg.getCheckedRadioButtonId() == -1)
                    Toast.makeText(AddPhone.this, "Please select identity", Toast.LENGTH_SHORT).show();
                else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phNo,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            AddPhone.this,               // Activity (for callback binding)
                            mCallback);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(AddPhone.this, "Verification Successfull.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(AddPhone.this, "Verification failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(AddPhone.this, "Code sent to phone.", Toast.LENGTH_SHORT).show();
            }
        };

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOtp = enterOtpEt.getText().toString();
                if(identityRg.getCheckedRadioButtonId() == -1)
                    Toast.makeText(AddPhone.this, "Please select identity", Toast.LENGTH_SHORT).show();
                else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                    SignInWithPhone(credential);
                }
            }
        });

    }

    private void SignInWithPhone(PhoneAuthCredential credential) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddPhone.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                                user = task.getResult().getUser();
                                userId = user.getUid();
                                if(userIsDriver) {

                                    driverInfo.setId(userId);
                                    driverInfo.setPhNo(phNo);
                                    DriversRegisteredRef = FirebaseDatabase.getInstance().getReference().child("Drivers Registered");
                                    RegisteredDriverIdRef = DriversRegisteredRef.child(userId);
                                    Map<String, Object> phoneNum = new HashMap<>();
                                    phoneNum.put("Phone Number", phNo);
                                    RegisteredDriverIdRef.updateChildren(phoneNum);

                                    Intent dSignInIntent = new Intent(AddPhone.this, DriverStops.class);
//                                    dSignInIntent.putExtra("phNo", phNo);
                                    dSignInIntent.putExtra("driverId", userId);
                                    startActivity(dSignInIntent);
                                    finish();
                                }
                                if(userIsPassenger) {

                                    passengerInfo.setId(userId);
                                    passengerInfo.setPhNo(phNo);
                                    PassengersRegisteredRef = FirebaseDatabase.getInstance().getReference().child("Drivers Registered");
                                    RegisteredPassengerIdRef = PassengersRegisteredRef.child(userId);
                                    HashMap<String, Object> phoneNum = new HashMap<>();
                                    phoneNum.put("Phone Number", phNo);
                                    RegisteredPassengerIdRef.setValue(phoneNum);

                                    Intent pSignInIntent = new Intent(AddPhone.this, PassengerNavBar.class);
//                                    pSignInIntent.putExtra("phNo", phNo);
                                    pSignInIntent.putExtra("passId", userId);
                                    startActivity(pSignInIntent);
                                    finish();
                                }
//                            Intent singInIntent = new Intent(AddPhone.this, InfoActivity.class);
//                            startActivity(singInIntent);
                            } else {
                                Toast.makeText(AddPhone.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
//                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                    Toast.makeText(AddPhone.this,"The verification code entered was invalid",Toast.LENGTH_SHORT).show();
//
//                                }
                            }
                        }
                    });
    }
}
