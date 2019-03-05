package com.cmput301w19t12.bookbuddies;

import android.location.Location;
import java.util.Date;

/**Event Class represents an Event Object to be associated with a class
 * An event has a location, time, and id
 *
 * @author Team 12
 * @version 1.0*/

public class Event {
    private Location location;
    private Date eventTime;
    private int eventId;

    /**Base constructor for an event
     * @param location Location
     * @param eventTime Date*/
    Event(Location location, Date eventTime){
        this.location = location;
        this.eventTime = eventTime;
    }

    /**Sets the location for the event
     * @param location Location*/
    public void setLocation(Location location){
        this.location = location;
    }

    /**Gets location for the event
     * @return location Location*/
    public Location getLocation(){
        return this.location;
    }

    /**Sets the time for the event
     * @param eventTime Date*/
    public void setEventTime(Date eventTime){
        this.eventTime = eventTime;
    }

    /**Gets the time of the event
     * @return eventTime Date*/
    public Date getEventTime(){
        return this.eventTime;
    }

    /**Sets the Id number of the event
     * @param eventId int*/
    public void setEventId(int eventId){
        this.eventId = eventId;
    }

    /**Gets the Id of the event
     * @return eventId int*/
    public int getEventId(){
        return this.eventId;
    }
}
