package com.example.canteenchecker.canteenmanager.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.google.android.material.textfield.TextInputEditText;

public class CanteenActivity extends AppCompatActivity {

    //layout
    private TextInputEditText edtCanteenName;
    private TextInputEditText edtMenu;
    private TextInputEditText edtMenuPrice;
    private TextInputEditText edtAddress;
    private TextInputEditText edtWebsite;
    private TextInputEditText edtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen);

        //check login
        if (!CanteenManagerApplication.getInstance().isAuthenticated()) {
            startActivity(LoginActivity.createIntent(getBaseContext()));
        }

        //layout
        edtCanteenName = findViewById(R.id.edtCanteenName);
        edtMenu = findViewById(R.id.edtMenu);
        edtMenuPrice = findViewById(R.id.edtMenuPrice);
        edtAddress = findViewById(R.id.edtAddress);
        edtWebsite = findViewById(R.id.edtWebsite);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);

        updateCanteen();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check login
        /*
        if (!CanteenManagerApplication.getInstance().isAuthenticated()) {
            startActivity(LoginActivity.createIntent(getBaseContext()));
        }
        */
    }

    private void updateCanteen() {
        new AsyncTask<Void, Void, Canteen>() {
            @Override
            protected Canteen doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Canteen canteen) {
                super.onPostExecute(canteen);
            }
        }.execute();
    }
}