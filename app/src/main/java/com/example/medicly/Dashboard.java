package com.example.medicly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        Button btn1 = findViewById(R.id.medRecBtn);
        btn1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), HealthRecords.class);
                startActivity(i1);
            }
        });

        Button btn2 = findViewById(R.id.medCliLocBtn);
        btn2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(Dashboard.this, Map.class);
                startActivity(i1);
            }
        });

        Button btn3 = findViewById(R.id.AppSchedBtn);
        btn3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), Schedule.class);
                startActivity(i1);
            }
        });

        Button btn4 = findViewById(R.id.AccSetBtn);
        btn4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), AccountSettings.class);
                startActivity(i1);
            }
        });

        ImageButton btn5 = findViewById(R.id.extBtn);
        btn5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i1);
            }
        });

    }

}