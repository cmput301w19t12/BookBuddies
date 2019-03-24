package com.cmput301w19t12.bookbuddies;

import android.location.Location;
import android.provider.ContactsContract;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;


/**User class represents a user account within the app
 * A user has a username, password, phone number, email address and profile picture path
 *
 * @author dfournier
 * @version 1.0*/

public class User {

    private String username;
    private String password;
    private String phoneNumber;
    private String emailAddress;
    private String profilePicturePath;
    private String userId;
    private String fullName;

    /**Constructor including all attributes
     * @param username String
     * @param password String
     * @param phoneNumber String
     * @param emailAddress String
     * @param profilePicturePath String*/
    public User(String username, String password, String phoneNumber, String emailAddress, String profilePicturePath){
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.profilePicturePath = profilePicturePath;

    }


    /**Constructor including all  but picture path and email address
     * @param username String
     * @param password String
     * @param phoneNumber String*/
    public User(String username, String password, String phoneNumber){
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.emailAddress = null;
        this.profilePicturePath = null;

    }



    /**Base constructor, including only username and password
     * @param username String
     * @param  password String*/
    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.phoneNumber = null;
        this.emailAddress = null;
        this.profilePicturePath = null;

    }

    public User(String userId, String username, String password, String emailAddress){
        this.username = username;
        this.password = password;
        this.phoneNumber = null;
        this.emailAddress = emailAddress;
        this.profilePicturePath = null;
        this.userId = userId;

    }

    public User(){ }

    /**Gets user email address
     * @return emailAddress String*/
    public String getEmailAddress() {
        return this.emailAddress;
    }

    /**Gets user Password
     * @return password String*/
    public String getPassword() {
        return this.password;
    }

    /**Gets user phone number
     * @return phoneNumber String*/
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**Gets the path for the users profile picture
     * @return profilePicturePath*/
    public String getProfilePicturePath() {
        return this.profilePicturePath;
    }

    /**Gets the users username
     * @return username String*/
    public String getUsername() {
        return this.username;
    }

    public String getFullName(){return this.fullName;}

    /**Sets user email address
     * @param emailAddress String*/
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**Sets the users password
     * @param password String*/
    public void setPassword(String password) {
        this.password = password;
    }

    /**Sets the users phone number
     * @param phoneNumber String*/
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**Sets the profile picture path
     * @param profilePicturePath String*/
    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }
    public void setFullName(String fullName){this.fullName = fullName;}

    /**Sets the users username
     * @param username String*/
    public void setUsername(String username) {
        this.username = username;
    }


}

