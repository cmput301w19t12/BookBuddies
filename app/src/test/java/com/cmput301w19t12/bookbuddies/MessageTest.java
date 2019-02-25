package com.cmput301w19t12.bookbuddies;

import java.util.*;

/**
 * This Unit Test includes all necessary checks for Message Class
 * Tests for validity of Chat Class
 * @return Boolean: Correct or Incorrect
 */

public class MessageTest {
    
   Message TestMessage = new Message("TestMessage", "Time", Sender);

    
    @Test
    public String GetMessageTextTest() {
        User TestUser = new User("user1","password1");
        Message TestMessage = new Message("TestMessage1", "Time1", "user1")
        assertEquals(TestMessage.getMessageText(), "TestMessage1");
        TestMessage.setMessageText("TestMessage2", "Time2", "user1");
        assertEquals(TestMessage.getMessageText(), "TestMessage2");

    }

    public void GetMessageTimetest() {
        User TestUser = new User("user2","password2");
        Message TestMessage = new Message("TestMessage3", "Time2", "user2");
        assertEquals(TestUser.getTime(), "Time2");
        

    }


    @Test
    public User GetSenderTest() {
        User TestUser = new User("User4", "password4");
        assertEquals(TestUser.getsender(), "user4");

       

    }


}