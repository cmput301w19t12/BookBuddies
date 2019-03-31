package com.cmput301w19t12.bookbuddies;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //Permission and GoogleMap
    private static final String TAG = "MapsActivity";
    private static final int PLACE_PICKER_REQUEST = 1234;
    private static final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String COARSE = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    private GoogleApiClient ApiClient;
    private GoogleMap mMap;
    private Boolean mPermissionGranted = false;
    //Android Location Services
    private LocationManager locationManager;
    private LocationListener listener;
    private double lon;
    private double lat;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Bundle b = getIntent().getExtras();
        Places.initialize(getApplicationContext(), "AIzaSyC1tManypjvozXL45sHpXrKnzXnhk-q18g");
        PlacesClient placesClient = Places.createClient(this);

    }

    //Getting Permission
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE) == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, PLACE_PICKER_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionGranted = false;
        getPermission();
        switch (requestCode) {
            case PLACE_PICKER_REQUEST: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPermissionGranted = false;
                            return;
                        }
                    }
                    mPermissionGranted = true;
                    init();
                }
            }
        }
    }

    public void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                Toast.makeText(MapsActivity.this, String.format("map init okay"), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Confirm(View v){
        Intent back = new Intent(MapsActivity.this, RequestViewActivity.class);
        startActivity(back);
    }

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.

// Create a new Places client instance.
        /*

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("MAPS", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                LatLng pos = place.getLatLng();
                if(pos != null) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(pos).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MAPS", "An error occurred: " + status);
            }
        });
    }*/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}