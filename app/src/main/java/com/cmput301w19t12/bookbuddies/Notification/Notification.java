/**
 * Notification
 *
 * @Author team 12
 *
 * March 31, 2019
 */
package com.cmput301w19t12.bookbuddies.Notification;

import android.support.annotation.NonNull;


/**
 * An abstract class defining a notification as used within BookBuddies
 */
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

    /**
     * @return the user who has been notified
     */
    public String getNotifiedUsername() {
        return notifiedUsername;
    }

    /**
     * Sets the username of the user who has been notified
     * @param notifiedUsername: String
     */
    public void setNotifiedUsername(String notifiedUsername) {
        this.notifiedUsername = notifiedUsername;
    }

    /**
     * @return the user who has triggered the notification
     */
    public String getNotifiedByUsername() {
        return notifiedByUsername;
    }

    /**
     * Sets the username of the user who has triggered the notification
     * @param notifiedByUsername:String
     */
    public void setNotifiedByUsername(String notifiedByUsername) {
        this.notifiedByUsername = notifiedByUsername;
    }

    /**
     * String format of the notification
     * @return String
     */
    @NonNull
    public abstract String toString();

}
