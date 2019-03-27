package com.cmput301w19t12.bookbuddies.Notification;

import android.support.annotation.NonNull;

public class ClubRequestNotification extends Notification {

    private String clubName;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public ClubRequestNotification() {
        clubName = null;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }


    public ClubRequestNotification(String notifiedUsername, String notifiedByUsername, String clubName, String status) {
        super(notifiedUsername, notifiedByUsername);
        this.clubName = clubName;
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
       return getNotifiedByUsername() + " would like to join your club!";
    }

    public String acceptedString() {
        return this.getNotifiedByUsername() + "added you to the" + this.getClubName() + "club!";
    }
}
