package com.cmput301w19t12.bookbuddies;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    User testUser = new User("test", "password");

    @Test
    public void usernameTest(){
        User testUser = new User("test", "password");
        assertEquals(testUser.getUsername(), "test");
        testUser.setUsername("test2");
        assertEquals(testUser.getUsername(),"test2");
    }
    @Test
    public void passwordTest(){
        User testUser = new User("test", "password");
        assertEquals(testUser.getPassword(), "password");
        testUser.setUsername("password2");
        assertEquals(testUser.getUsername(),"password2");
    }
    @Test
    public void contactInfoTest(){
        User testUser = new User("username", "password", "555-5555", "test@example.com", "photos/selfie.png");
        assertEquals(testUser.getPhoneNumber(),"555-5555");
        testUser.setPhoneNumber("911");
        assertEquals(testUser.getPhoneNumber(),"911");
        assertEquals(testUser.getEmailAddress(), "test@example.com");
        testUser.setEmailAddress("student@ualberta.ca");
        assertEquals(testUser.getEmailAddress(), "student@ualberta.ca");
    }
    @Test
    public void profilePictureTest(){
        User testUser = new User("username", "password", "555-5555", "test@example.com", "photos/selfie.png");
        assertEquals(testUser.getProfilePicturePath(),"photos/selfie.png");
        testUser.setProfilePicturePath("photos/cuteCat.png");
        assertEquals(testUser.getProfilePicturePath(), "photos/cuteCat.png");
    }

    //Other Tests require database functionality

}
