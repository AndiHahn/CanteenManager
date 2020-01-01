package com.example.canteenchecker.canteenmanager.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new CanteenFragment(), "Canteen");
        adapter.addFragment(new ReviewsFragment(), "Reviews");
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check login
        if (!CanteenManagerApplication.getInstance().isAuthenticated()) {
            startActivity(LoginActivity.createIntent(getBaseContext()));
        } else {
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mniSignout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        CanteenManagerApplication.getInstance().deleteAuthToken();
        startActivity(LoginActivity.createIntent(getBaseContext()));
    }
}