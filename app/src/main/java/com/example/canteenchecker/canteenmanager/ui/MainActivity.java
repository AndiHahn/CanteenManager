package com.example.canteenchecker.canteenmanager.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check login
        if (!CanteenManagerApplication.getInstance().isAuthenticated()) {
            startActivity(LoginActivity.createIntent(getBaseContext()));
        } else {
            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabLayout);
            adapter = new TabAdapter(getSupportFragmentManager());
            adapter.addFragment(new CanteenFragment(), "Canteen");
            adapter.addFragment(new ReviewsFragment(), "Reviews");
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }
    }
}