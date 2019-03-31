package com.cmput301w19t12.bookbuddies;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    private User testUser;

    @Before
    public void setUp() throws Exception {
        testUser = new User("username", "password", "555-5555", "test@example.com", "photos/selfie.png");
    }

    @Test
    public void usernameTest(){
        assertEquals(testUser.getUsername(), "username");
        testUser.setUsername("test2");
        assertEquals(testUser.getUsername(),"test2");
    }
    @Test
    public void passwordTest(){
        assertEquals(testUser.getPassword(), "password");
        testUser.setUsername("password2");
        assertEquals(testUser.getUsername(),"password2");
    }
    @Test
    public void contactInfoTest(){
        assertEquals(testUser.getPhoneNumber(),"555-5555");
        testUser.setPhoneNumber("911");
        assertEquals(testUser.getPhoneNumber(),"911");
        assertEquals(testUser.getEmailAddress(), "test@example.com");
        testUser.setEmailAddress("student@ualberta.ca");
        assertEquals(testUser.getEmailAddress(), "student@ualberta.ca");
    }
    @Test
    public void profilePictureTest(){
        assertEquals(testUser.getProfilePicturePath(),"photos/selfie.png");
        testUser.setProfilePicturePath("photos/cuteCat.png");
        assertEquals(testUser.getProfilePicturePath(), "photos/cuteCat.png");
    }

    @Test
    public void userIdTest() {
        testUser.setUserId("1234");
        assertEquals(testUser.getUserId(),"1234");
    }

    //Other Tests require database functionality

}
