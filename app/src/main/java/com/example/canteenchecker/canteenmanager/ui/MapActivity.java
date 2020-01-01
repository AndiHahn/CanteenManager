package com.example.canteenchecker.canteenmanager.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class MapActivity extends AppCompatActivity implements LocationListener {

    //constants
    private static final String TAG = "MapActivity";
    public static final String ADDRESS_KEY = "Address" ;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 200;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    //layout
    private SupportMapFragment mpfMap;
    private FloatingActionButton btnSaveLocation;
    private FloatingActionButton btnNavigateToCurPos;

    private LocationManager locationManager;

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

        // get address from CanteenFragment
        Intent intent = getIntent();
        address = intent.getStringExtra(ADDRESS_KEY);
        Log.e("FUD", "Address passed to MapActivity: " + address);

        mpfMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mpfMap);
        btnSaveLocation = findViewById(R.id.btnSaveLocation);
        btnSaveLocation.setOnClickListener(v -> saveLocation());
        btnNavigateToCurPos = findViewById(R.id.btnNavigateToCurPos);
        btnNavigateToCurPos.setOnClickListener(v -> {
            Log.e("TEST", "clicked button location current");
            navigateToCurrentLocation();
        });

        updateMap();
    }

    private void saveLocation() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(ADDRESS_KEY, this.address);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateMap() {
        new AsyncTask<String, Void, LatLng>() {

            @Override
            protected LatLng doInBackground(String... params) {
                //try to return canteen location (string -> lat, long)
                LatLng location = null;
                Geocoder geocoder = new Geocoder(MapActivity.this);
                try {
                    List<Address> addresses = params[0] == null ? null : geocoder.getFromLocationName(params[0], 1);
                    if(addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        location = new LatLng(address.getLatitude(), address.getLongitude());
                    } else {
                        Log.w(TAG, String.format("Resolving of location for location name %s failed.", params[0]));
                    }
                } catch (IOException e) {
                    Log.e(TAG, String.format("Resolving of location for location name %s failed.", params[0]), e);
                }

                return location;
            }

            @Override
            protected void onPostExecute(final LatLng location) {
                //update map racording to our location
                mpfMap.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.clear();
                        if(location != null) {
                            googleMap.addMarker(new MarkerOptions().position(location));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        } else {
                            navigateToCurrentLocation();
                            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0),0));
                        }
                    }
                });
            }
        }.execute(address);
    }

    private void navigateToCurrentLocation() {
        //check if permission for making a phone call has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            //read current location
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToCurrentLocation();
            } else {
                Toast.makeText(this, "No location permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mpfMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.animateCamera(cameraUpdate);
                locationManager.removeUpdates(MapActivity.this);
                btnNavigateToCurPos.setImageResource(R.drawable.ic_gps_fixed_24px);
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
