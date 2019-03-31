package com.cmput301w19t12.bookbuddies;


/**User made LatLng class to temporarily replace built in LatLng class as it has no empty
 * constructor and cannot be used to store locations in firebase as it will crash on retrieval
 *
 * @author bgrenier
 * @version 1.0
 *
 * @see com.google.android.gms.maps.model.LatLng
 * @see MapsActivity*/

public class MyLatLng {
    private Double latitude;
    private Double longitude;


    public MyLatLng() {}

    /**complete constructor
     * @param latitude Double
     * @param longitude Double*/
    public MyLatLng(Double latitude,Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**returns longitude
     * @return longitude Double*/
    public Double getLongitude() {
        return longitude;
    }

    /**Sets longitude
     * @param longitude Double*/
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**gets latitude
     * @return latitude double*/
    public Double getLatitude() {
        return latitude;
    }

    /**sets latitude
     * @param latitude Double*/
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
