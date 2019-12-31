package com.example.canteenchecker.canteenmanager.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.canteenchecker.canteenmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapActivity extends AppCompatActivity {

    //constants
    public static final String ADDRESS_KEY = "Address" ;

    //layout
    private FloatingActionButton btnSaveLocation;

    private String address;

    public static Intent createIntent(Context context, String address) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(ADDRESS_KEY, address);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnSaveLocation = findViewById(R.id.btnSaveLocation);
        btnSaveLocation.setOnClickListener(v -> saveLocation());

        this.address = "Test Addresse von Map";
    }

    private void saveLocation() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(ADDRESS_KEY, this.address);
        setResult(RESULT_OK, intent);
        finish();
    }

}
