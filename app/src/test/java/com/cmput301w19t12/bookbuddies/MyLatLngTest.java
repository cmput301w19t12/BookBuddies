package com.cmput301w19t12.bookbuddies;

import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

public class MyLatLngTest {
    private Double lat = 1.0;
    private Double longi = 1.0;
    private MyLatLng testLatLng = new MyLatLng(lat,longi);

    @Test
    public void latitudeTest(){
        assertEquals(testLatLng.getLatitude(),lat);
        testLatLng.setLatitude(2.0);
        assertEquals(testLatLng.getLatitude(),2.0);
    }

    @Test
    public void longitudeTest(){
        assertEquals(testLatLng.getLongitude(),longi);
        testLatLng.setLongitude(3.0);
        assertEquals(testLatLng.getLongitude(),3.0);
    }
}
