package com.example.canteenchecker.canteenmanager.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;

public class CanteenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check login
        if (!CanteenManagerApplication.getInstance().isAuthenticated()) {
            startActivity(LoginActivity.createIntent(getBaseContext()));
        }
    }
}