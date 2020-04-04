package com.jumayu.blowbeep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";
    TextInputEditText nameEt;
    TextInputEditText emailEt;
    RadioGroup idGroup;
    Button continueBtn;
    int personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        nameEt = findViewById(R.id.name_et);
        emailEt = findViewById(R.id.email_et);
        idGroup = findViewById(R.id.id_group);
        continueBtn = findViewById(R.id.continue_btn);

        idGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                personId = checkedId;
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEmpty(nameEt)) {
                    Toast.makeText(InfoActivity.this, "Please fill the name field", Toast.LENGTH_SHORT).show();
                }
                else if(idGroup.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(InfoActivity.this, "Please select your identity", Toast.LENGTH_SHORT).show();
                }
                else {
                    switch (personId) {
                        case R.id.pass_rb:
                            Toast.makeText(InfoActivity.this, "Hello Passenger!", Toast.LENGTH_SHORT).show();
                            Intent passIntent = new Intent(InfoActivity.this, PassengerNavBar.class);
                            startActivity(passIntent);
                            finish();
                            break;
                        case R.id.driver_rb:
                            Toast.makeText(InfoActivity.this, "Hello Driver!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
