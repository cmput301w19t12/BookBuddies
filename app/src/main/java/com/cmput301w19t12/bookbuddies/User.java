package com.cmput301w19t12.bookbuddies;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

public class User {

    private String username;
    private String password;
    private String phoneNumber;
    private String emailAddress;
    private String profilePicturePath;

    public User(String username, String password, String phoneNumber, String emailAddress, String profilePicturePath){
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.profilePicturePath = profilePicturePath;
    }
    public User(String username, String password, String phoneNumber, String emailAddress){
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.profilePicturePath = null;
    }
    public User(String username, String password, String phoneNumber){
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.emailAddress = null;
        this.profilePicturePath = null;
    }
    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.phoneNumber = null;
        this.emailAddress = null;
        this.profilePicturePath = null;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public String getUsername() {
        return username;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void deleteUser(){
        //do things with database
    }
    public void addBook(String title, String author, String isbn, String description){
        /*
        BookDetails bookDetails = new BookDetails(title, author,isbn, description);
        Book book = new Book(this.username, bookDetails,"available");

        //add the book to the database

        */
    }

    public void createClub(String clubName){
       /*
        ArrayList<User> memberList = new ArrayList<User>();
        memberList.add(this);
        Club club = new Club(this.username, clubName, memberList);

        //add Club to database
        */
    }
/*
    public Transaction tradeBook(User borrower, Location tradeLocation, Book book, Date tradeTime){
        return new Transaction(this, borrower, book, tradeLocation, tradeTime);
        //add transaction to database
    }
*/
}
