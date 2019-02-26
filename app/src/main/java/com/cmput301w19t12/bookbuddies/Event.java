package com.cmput301w19t12.bookbuddies;

import android.location.Location;

import java.util.Date;

public class Event {
    private Location location;
    private Date eventTime;
    private int eventId;

    Event(Location location, Date eventTime){
        this.location = location;
        this.eventTime = eventTime;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public Location getLocation(){
        return this.location;
    }

    public void setEventTime(Date eventTime){
        this.eventTime = eventTime;
    }

    public Date getEventTime(){
        return this.eventTime;
    }

    public void setEventId(int eventId){
        this.eventId = eventId;
    }

    public int getEventId(){
        return this.eventId;
    }
}
