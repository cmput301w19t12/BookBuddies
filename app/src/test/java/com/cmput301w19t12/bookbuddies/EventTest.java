package com.cmput301w19t12.bookbuddies;

import java.util.*;

/**
 * This Unit Test includes all necessary checks for Event Class
 * Tests for validity of Chat Class
 * @return Boolean: Correct or Incorrect
 */

public class EventTest {
    
    @Test
    public void LocationTest() {
        
        Event TestEvent = new Event;
        Location TestLocation = new Location;
        String TestLocation = "Edmonton"
        TestEvent.setLocation(TestLocation);
        assertEquals(TestLocation.getlocation(), TestLocation);
      

    }

    @Test
    public void EventTimeTest() {
        
        Event TestEvent = new Event;
        Date TestTime = new Date();
        String TestTime = "11:00 pm";
        TestEvent.setTime(TestTime);
        assertEquals(TestTime.gettime(), TestTime);

    }

    public void EventIDTest() {
        Event TestEvent = new Event;
        
        String id = "1";
        TestEvent.setid(id);
        assertEquals(TestTime.getid(), id);

    }


}
