/*
*mapsActivity
*
* version 1.0
*
* Dec 3, 2017
*
*Copyright (c) 2017 Team 16 (Jonah Cowan, Alexander Mackenzie, Hao Yuan, Jacy Mark, Shu-Ting Lin), CMPUT301, University of Alberta - All Rights Reserved.
*You may use, distribute, or modify this code under terms and conditions of the Code of Student Behavior at University of Alberta.
*You can find a copy of the license in this project. Otherwise please contact contact@abc.ca.
*
*/

package com.example.habittracker2017;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 *Show google map and marks events
 *
 * @author team 16
 * @version 1.0
 * @see viewMyHistory
 * @since 1.0
 */
public class mapsActivity extends AppCompatActivity implements OnMapReadyCallback{
    private ArrayList<HabitEvent> toBeDisplayed;
    private ArrayList<HabitEvent> toBeHighlighted = new ArrayList<HabitEvent>();
    private ArrayList<HabitEvent> notWithIn5KM = new ArrayList<HabitEvent>();
    private LocationManager locationManager;
    private LocationListener listener;
    private double latitude;
    private double longitude;
    private Button highlightButton;
    private Boolean highlighted = false;
    private Location currentLocation;

    /**
     * Set up for displaying events that's either user's events or following's events
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        highlightButton = (Button) findViewById(R.id.Highlight);
        if (getIntent().getStringExtra("Caller").equals("mine")){
            toBeDisplayed = viewMyHistory.allEvents;
        }else{
            toBeDisplayed = OthersFragment.allEvents;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            /**
             *When location changed, update event to be displayed that's within 5km and not within 5km,
             * so user can always see events that's within 5km
             * @param location
             */
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentLocation = location;
                toBeHighlighted= new ArrayList<HabitEvent>();
                notWithIn5KM = new ArrayList<HabitEvent>();
                for (HabitEvent event : toBeDisplayed) {
                    if (event.getLocation() != null) {
                        if (currentLocation.distanceTo(event.getLocation()) <= 5000) {
                            toBeHighlighted.add(event);
                        } else {
                            notWithIn5KM.add(event);
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            /**
             * Called when provider is disabled and requesting for location updates
             * @param s
             */
            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
    }

    /**
     * Get location of device. If get location request is not permitted, then location may not be uptodate or null
     */
    private void getDeviceLocation() {
        /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */
        if (ActivityCompat.checkSelfPermission(mapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 0, 0, listener);
        Location location = locationManager.getLastKnownLocation("gps");
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * Marking and highlighting events on map
     * Able to highlight events within 5km or beyond 5km
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        highlighted = false;
        LatLng latLng;
        getDeviceLocation();
        currentLocation = new Location("gps");
        currentLocation.setLongitude(longitude);
        currentLocation.setLatitude(latitude);
        for (HabitEvent event : toBeDisplayed) {
            if (event == null){
                toBeDisplayed.remove(event);
            }else {
                if (event.getLocation() != null) {
                    if (currentLocation.distanceTo(event.getLocation()) <= 5000) {
                        toBeHighlighted.add(event);
                    } else {
                        notWithIn5KM.add(event);
                    }
                    latLng = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title(event.getComment())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal)));
                }
            }
        }
        latLng = new LatLng(latitude,longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));

        highlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng;
                googleMap.clear();
                if (highlighted){
                    highlightButton.setText("Highlight Events Within 5KM");
                    for (HabitEvent event : toBeDisplayed) {
                        if (event.getLocation() != null) {
                            latLng = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(latLng)
                                    .title(event.getComment())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal)));
                        }
                    }
                    highlighted = false;
                }else{
                    highlightButton.setText("Stop Highlighting");
                    for (HabitEvent event : notWithIn5KM){
                        if (event.getLocation() != null) {
                            latLng = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(latLng)
                                    .title(event.getComment())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal)));
                        }
                    }
                    for (HabitEvent event : toBeHighlighted){
                        if (event.getLocation() != null) {
                            latLng = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(latLng)
                                    .title(event.getComment())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_highlight)));
                        }
                    }
                    highlighted = true;
                }
                latLng = new LatLng(latitude,longitude);
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

    }




}
