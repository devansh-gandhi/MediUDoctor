package com.example.devanshgandhi.mediu_doctor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HealthFeed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_feed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
