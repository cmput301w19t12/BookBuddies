/*
*AddLocationActivity
*
* version 1.0
*
* Dec 3, 2017
*
*Copyright (c) 2017 Team 16 ( Jonah Cowan, Alexander Mackenzie, Hao Yuan, Jacy Mark, Shu-Ting Lin), CMPUT301, University of Alberta - All Rights Reserved.
*You may use, distribute, or modify this code under terms and conditions of the Code of Student Behavior at University of Alberta.
*You can find a copy of the license in this project. Otherwise please contact contact@abc.ca.
*
*/

package com.example.habittracker2017;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * AddLocationActivity lets user add location to the event they are creating
 * @author team 16
 * @version 1.0
 * @see CreateEventActivity
 * @since 1.0
 */
public class AddLocationActivity extends AppCompatActivity {
    private Button button;
    private Button B_new;
    private Button B_confirm;
    private TextView T_address;
    private TextView T_coord;
    private LocationManager locationManager;
    private LocationListener listener;
    private EditText E_address;
    private String knownName;
    private double latitude;
    private double longitude;

    /**
     * Called when activity created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        T_coord = (TextView) findViewById(R.id.t_coordination);
        T_address = (TextView) findViewById(R.id.t_address);
        button = (Button) findViewById(R.id.b_Current);
        B_new = (Button) findViewById(R.id.b_New);
        B_confirm = (Button) findViewById(R.id.b_confirm);
        E_address = (EditText) findViewById(R.id.e_address);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            /**
             *Get location user enters and display one address
             * @param location
             */
            public void onLocationChanged(Location location) {
                T_coord.setText("");
                T_address.setText("");

                T_coord.append("\n " + location.getLongitude() + " " + location.getLatitude());
                Geocoder geocoder = new Geocoder(AddLocationActivity.this);

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address myAddress = addressList.get(0);

                    knownName = myAddress.getAddressLine(0) + "\n" +
                            myAddress.getAddressLine(1) + "\n";
                    if (myAddress.getAddressLine(2) != null) {
                        knownName = knownName + myAddress.getAddressLine(2);
                    }
                    latitude = myAddress.getLatitude();
                    longitude = myAddress.getLongitude();

                    T_coord.setText("");
                    T_address.setText("");

                    T_coord.append("Coordinates:" + myAddress.getLatitude() + " " + myAddress.getLongitude());

                    T_address.append(knownName);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             *
             * @param s
             * @param i
             * @param bundle
             */
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            /**
             *
             * @param s
             */
            @Override
            public void onProviderEnabled(String s) {

            }

            /**
             *Called when provider is disabled by the user
             * Show setting to allow configuration of current location
             * @param s
             */
            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
        newplace_button();
        confirm_button();
    }


    /**
     *Receive result for permission request
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }
    /**
     *
     * Button method it will go back to AddNewHabitEventActivity
     */

    void confirm_button(){
        B_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent returnIntent = new Intent();
                returnIntent.putExtra("new_address", knownName);
                returnIntent.putExtra("new_latitude", latitude);
                returnIntent.putExtra("new_longitude", longitude);

                setResult(RESULT_OK, returnIntent);
                finish();
            }

        });
    }

    /**
     *
     * Button method it will get the location which is entered by user
     */

    void newplace_button(){
        B_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                if (ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                List<Address> addressList = null;


                //use Geocoder class here
                Geocoder geocoder = new Geocoder(AddLocationActivity.this);
                try {
                    String location = E_address.getText().toString();

                    addressList = geocoder.getFromLocationName(location, 1);
                    //check if the input address can be found
                    if (addressList.size() != 0){

                        location = E_address.getText().toString();
                        addressList = geocoder.getFromLocationName(location, 1);

                        Address myAddress = addressList.get(0);

                        knownName = myAddress.getAddressLine(0) + "\n" +
                                myAddress.getAddressLine(1) + "\n";
                        if (myAddress.getAddressLine(2) != null) {
                            knownName = knownName + myAddress.getAddressLine(2);
                        }


                        latitude = myAddress.getLatitude();
                        longitude = myAddress.getLongitude();

                        T_coord.setText("");
                        T_address.setText("");

                        T_coord.append("Coordinates:" + myAddress.getLatitude() + " " + myAddress.getLongitude());

                        T_address.append(knownName);


                    }

                    else{
                        Toast.makeText(getApplicationContext(), "Invalid Address", Toast.LENGTH_LONG).show();

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Invalid Address", Toast.LENGTH_LONG).show();

                }
            }
        });
    }



    /**
     *
     * Button method it will check the permission and get the current location
     */

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                if (ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Geocoder geocoder = new Geocoder(AddLocationActivity.this);
                try{
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Cannot get current location", Toast.LENGTH_LONG).show();
                }

                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address myAddress = addressList.get(0);

                    knownName = myAddress.getAddressLine(0) + "\n" +
                            myAddress.getAddressLine(1) + "\n";
                    if (myAddress.getAddressLine(2) != null) {
                        knownName = knownName + myAddress.getAddressLine(2);
                    }
                    latitude = myAddress.getLatitude();
                    longitude = myAddress.getLongitude();

                    T_coord.setText("");
                    T_address.setText("");

                    T_coord.append("Coordinates:" + myAddress.getLatitude() + " " + myAddress.getLongitude());

                    T_address.append(knownName);


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Cannot get current location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
