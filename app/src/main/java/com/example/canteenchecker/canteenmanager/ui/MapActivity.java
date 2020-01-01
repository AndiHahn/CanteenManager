package com.example.canteenchecker.canteenmanager.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.canteenchecker.canteenmanager.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    //constants
    private static final String TAG = "MapActivity";
    public static final String ADDRESS_KEY = "Address";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    //layout
    private SupportMapFragment mpfMap;
    private FloatingActionButton btnSaveLocation;
    private FloatingActionButton btnNavigateToCurPos;

    private String address;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Marker markerCenter;
    private boolean mapMovedByUser;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;


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
        geocoder = new Geocoder(this);

        mpfMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mpfMap);
        btnSaveLocation = findViewById(R.id.btnSaveLocation);
        btnSaveLocation.setOnClickListener(v -> saveLocation());
        btnNavigateToCurPos = findViewById(R.id.btnNavigateToCurPos);
        btnNavigateToCurPos.setOnClickListener(v -> navigateToCurrentLocation());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();

        updateMap();
    }

    private void saveLocation() {
        getCurrentMarkerAddress();
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(ADDRESS_KEY, this.address);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void getCurrentMarkerAddress() {
        if (markerCenter != null && mapMovedByUser) {
            LatLng position = markerCenter.getPosition();
            try {
                List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
                if(addresses != null && addresses.size() > 0 ) {
                    this.address = addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                Log.e(TAG, "Geocoding for current marker position failed", e);
            }
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                }
            }
        });
    }

    private void navigateToCurrentLocation() {
        if (this.currentLocation == null) {
            fetchLocation();
        }

        if (this.googleMap != null && this.currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            btnNavigateToCurPos.setImageResource(R.drawable.ic_gps_fixed_24px);
            mapMovedByUser = true;
        }
    }

    private void updateMap() {
        new AsyncTask<String, Void, LatLng>() {

            @Override
            protected LatLng doInBackground(String... params) {
                //try to return canteen location (string -> lat, long)
                LatLng location = null;
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
                //update map according to our location
                mpfMap.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapActivity.this.googleMap = googleMap;

                        googleMap.clear();
                        if(location != null) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        } else {
                            navigateToCurrentLocation();
                        }

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(googleMap.getCameraPosition().target);
                        markerCenter = googleMap.addMarker(markerOptions);

                        mapMovedByUser = false;

                        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                            public void onCameraMove() {
                                markerCenter.setPosition(googleMap.getCameraPosition().target);
                            }
                        });

                        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                            @Override
                            public void onCameraMoveStarted(int reason) {
                                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                                    //The user gestured on the map.
                                    mapMovedByUser = true;
                                    btnNavigateToCurPos.setImageResource(R.drawable.ic_gps_not_fixed_24px);
                                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                                        .REASON_API_ANIMATION) {
                                    //The user tapped something on the map.
                                    mapMovedByUser = true;
                                    btnNavigateToCurPos.setImageResource(R.drawable.ic_gps_not_fixed_24px);
                                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                                        .REASON_DEVELOPER_ANIMATION) {
                                    //The app moved the camera.
                                }
                            }
                        });
                    }
                });
            }
        }.execute(address);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }
}
