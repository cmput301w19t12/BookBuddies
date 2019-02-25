package com.cmput301w19t12.bookbuddies;

import java.util.*;

/**
 * This Unit Test includes all necessary checks for Chart Class
 * Tests for validity of Chart Class
 * @return Boolean: Correct or Incorrect
 */

public class ChartTest {
    ArrayList<String> MessageList = new ArrayList<String>();
    MessageList.add("msg1");
    MessageListDetails TestMessage = new MessageLisDetails(MessageList);
    ArrayList<String> UserList = new ArrayList<String>();
    UserList.add("user1");
    UserListDeatils TestUser = new UserListDetails(UserList);
    @Test
    public void GetMessageTest() {
        MessageListDeatils TestMessage = new MessageListDetails(MessageList);
        assertEquals(TestMessage.getMessage(), "msg1");
        TestMessage.sendMessage("msg2");
        assertEquals(TestMessage.getMessage(), "msg2");

    }

    @Test
    public void GetUsersTest() {
        UserListDeatils TestUser = new UserListDetails(UserList);
        assertEquals(TestUser.getMessage(), "user1");
        TestMessage.addUser("user2");
        assertEquals(TestMessage.getMessage(), "user2");

    }


}
