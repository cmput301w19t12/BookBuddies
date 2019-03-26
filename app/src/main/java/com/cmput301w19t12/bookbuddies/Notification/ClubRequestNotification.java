package com.cmput301w19t12.bookbuddies.Notification;

import android.support.annotation.NonNull;

public class ClubRequestNotification extends Notification {

    public ClubRequestNotification() {
        clubName = null;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    private String clubName;

    public ClubRequestNotification(String notifiedUsername, String notifiedByUsername, String clubName) {
        super(notifiedUsername, notifiedByUsername);
        this.clubName = clubName;
    }

    @NonNull
    @Override
    public String toString() {
       return getNotifiedByUsername() + " would like to join your club!";
    }
}
