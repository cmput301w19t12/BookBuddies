/**
 * ClubRequestNotification
 *
 * @Author team12
 *
 * March 31, 2019
 */
package com.cmput301w19t12.bookbuddies.Notification;

import android.support.annotation.NonNull;

/**Store the data needed to show the user a notification that a user wishes to join their club\
 *
 * @author Ayub
 * @version 1.0
 *
 * @see Notification
 * @see com.cmput301w19t12.bookbuddies.Club*/
 
public class ClubRequestNotification extends Notification {

    private String clubName;
    private String status;

    /**empty constructor*/
    public ClubRequestNotification() {
        clubName = null;
    }

    /**full construtor
     * @param notifiedByUsername String
     * @param  notifiedUsername String
     * @param clubName String
     * @param  status String*/
    public ClubRequestNotification(String notifiedUsername, String notifiedByUsername, String clubName, String status) {
        super(notifiedUsername, notifiedByUsername);
        this.clubName = clubName;
        this.status = status;
    }

    /**returns the club name of the request
     * @return clubName String*/

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

    /**returns the status of the notification
     * @return status String*/
    public String getStatus() {
        return status;
    }

    /**sets the status of this request
     * @param status String*/
    public void setStatus(String status) {
        this.status = status;
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
