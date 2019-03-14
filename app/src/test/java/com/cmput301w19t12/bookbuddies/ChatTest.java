package com.cmput301w19t12.bookbuddies;

import android.location.Location;

import org.junit.Test;

import java.util.*;
import org.junit.Assert;
import static org.junit.Assert.*;

/**
 * This Unit Test includes all necessary checks for Chat Class
 * Tests for validity of Chat Class
 */

public class ChatTest {
    Chat testChat = new Chat();
    User testUser = new User("email","2342");

    @Test
    public void GetMessageTest() {
        assertTrue(testChat.getMessages().isEmpty());
        testChat.addMessage(new Message("test",new Date(),testUser));
        assertEquals(testChat.getMessages().get(0).getMessageText(),"test");
    }

    @Test
    public void GetUsersTest() {
        assertTrue(testChat.getUsers().isEmpty());
        testChat.addUser(testUser);
        assertEquals(testChat.getUsers().get(0).getUsername(),"email");
    }


}
