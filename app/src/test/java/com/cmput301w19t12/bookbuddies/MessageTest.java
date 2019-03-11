package com.cmput301w19t12.bookbuddies;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageTest {

    User TestUser = new User("user1","password1");
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    String message = "test";
    Message TestMessage = new Message(message, date, TestUser);

    
    @Test
    public void GetMessageTextTest() {
        assertEquals(TestMessage.getMessageText(), "test");
        TestMessage.setMessageText("TestMessage2");
        assertEquals(TestMessage.getMessageText(), "TestMessage2");
    }

    @Test
    public void GetMessageTimeTest() {
        Assert.assertEquals(TestMessage.getMessageTime(),date);
    }


    @Test
    public void GetSenderTest() {
        assertEquals(TestMessage.getSender().getUsername(), "user1");

    }


}