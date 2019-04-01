package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BookRequest request;
    private LatLng meetingLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle b = getIntent().getExtras();
        request = new Gson().fromJson(b.getString("request"),BookRequest.class);
        meetingLocation = b.getParcelable("location");



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Initialize Places.
        Places.initialize(getApplicationContext(),"AIzaSyC1tManypjvozXL45sHpXrKnzXnhk-q18g");

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);


        if(meetingLocation == null) {
            String defaultPlaceID = "ChIJoxCPYfUhoFMRE7JXmPJlW8s";
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,Place.Field.NAME, Place.Field.LAT_LNG);
            FetchPlaceRequest defaultRequest = FetchPlaceRequest.builder(defaultPlaceID,placeFields).build();
            placesClient.fetchPlace(defaultRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                    Place place = fetchPlaceResponse.getPlace();
                    meetingLocation = place.getLatLng();
                    Log.i("MAPS", "Place found: " + place.getName());
                }
            });
        }
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
                meetingLocation = place.getLatLng();
                if(meetingLocation != null) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(meetingLocation).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(meetingLocation));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MAPS", "An error occurred: " + status);
            }
        });
    }


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
        if (meetingLocation != null) {
            mMap.addMarker(new MarkerOptions().position(meetingLocation).title("Meeting Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetingLocation,10));
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("request",new Gson().toJson(request));
        intent.putExtra("location",meetingLocation);
        setResult(4,intent);
        finish();
    }

}