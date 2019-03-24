package com.cmput301w19t12.bookbuddies;

import android.location.Location;

import org.junit.Test;

import java.util.*;
import org.junit.Assert;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * This Unit Test includes all necessary checks for Event Class
 * Tests for validity of Chat Class
 */

public class EventTest {

    Event TestEvent = new Event();
    Location location = new Location("");
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();



    @Test
    public void LocationTest() {
        TestEvent.setLocation(location);
        assertSame(TestEvent.getLocation().getClass(),Location.class);
    }

    @Test
    public void EventTimeTest() {
        TestEvent.setEventTime(date);
        assertEquals(TestEvent.getEventTime(), date);

    }

    @Test
    public void EventIDTest() {
        int id = 1;
        TestEvent.setEventId(id);
        assertEquals(TestEvent.getEventId(), id);

    }


}
