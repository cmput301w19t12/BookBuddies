/**
 * ClubRequestNotification
 *
 * @Author team12
 *
 * March 31, 2019
 */
package com.cmput301w19t12.bookbuddies.Notification;

import android.support.annotation.NonNull;

/**
 * The class which represents a Club Request Notification.
 */
public class ClubRequestNotification extends Notification {

    private String clubName;
    private String status;

    public ClubRequestNotification() {
        clubName = null;
    }

    public ClubRequestNotification(String notifiedUsername, String notifiedByUsername, String clubName, String status) {
        super(notifiedUsername, notifiedByUsername);
        this.clubName = clubName;
        this.status = status;
    }

    /**
     *
     * @return status:String
     */
    public String getStatus() {
        return status;
    }

    /**
     * Updates the status of the notification
     * @param status:String
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the name of the club that has a notification
     * @return clubName:String
     */
    public String getClubName() {
        return clubName;
    }

    /**
     * Sets the name of the club that has the notification
     * @param clubName:String
     */
    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    /**
     * Returns a string format of the notification describing which user would like to join
     * your club.
     * @return notification:String
     */
    @NonNull
    @Override
    public String toString() {
       return getNotifiedByUsername() + " would like to join your club!";
    }

    /**
     * A string format of the notification for a user being accepted to the club.
     * @return notification:String
     */
    public String acceptedString() {
        return this.getNotifiedByUsername() + "added you to the" + this.getClubName() + "club!";
    }
}
