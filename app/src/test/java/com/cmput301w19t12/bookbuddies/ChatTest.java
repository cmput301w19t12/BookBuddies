package com.cmput301w19t12.bookbuddies;

import java.util.*;

/**
 * This Unit Test includes all necessary checks for Chat Class
 * Tests for validity of Chat Class
 * @return Boolean: Correct or Incorrect
 */

public class ChatTest {
    ArrayList<String> MessageList = new ArrayList<String>();
    MessageList.add("msg1");
    Club TestMessage = new Club(MessageList);
    ArrayList<String> UserList = new ArrayList<String>();
    UserList.add("user1");
    User TestUser = new User(UserList);
    @Test
    public void GetMessageTest() {
        Club TestMessage = new Club(MessageList);
        assertEquals(TestMessage.getMessage(), "msg1");
        TestMessage.sendMessage("msg2");
        assertEquals(TestMessage.getMessage(), "msg2");

    }

    @Test
    public void GetUsersTest() {
        User TestUser = new User(UserList);
        assertEquals(TestUser.getMessage(), "user1");
        TestMessage.addUser("user2");
        assertEquals(TestMessage.getMessage(), "user2");

    }


}
