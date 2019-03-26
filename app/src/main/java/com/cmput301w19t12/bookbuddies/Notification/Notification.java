package com.cmput301w19t12.bookbuddies.Notification;

import android.support.annotation.NonNull;



public abstract class Notification {
    private String notifiedUsername;
    private String notifiedByUsername;

    public Notification() {
        notifiedByUsername = null;
        notifiedUsername = null;
    }

    public Notification(String notifiedUsername, String notifiedByUsername) {
        this.notifiedUsername = notifiedUsername;
        this.notifiedByUsername = notifiedByUsername;
    }

    public String getNotifiedUsername() {
        return notifiedUsername;
    }

    public void setNotifiedUsername(String notifiedUsername) {
        this.notifiedUsername = notifiedUsername;
    }

    public String getNotifiedByUsername() {
        return notifiedByUsername;
    }

    public void setNotifiedByUsername(String notifiedByUsername) {
        this.notifiedByUsername = notifiedByUsername;
    }

    @NonNull
    public abstract String toString();

}
