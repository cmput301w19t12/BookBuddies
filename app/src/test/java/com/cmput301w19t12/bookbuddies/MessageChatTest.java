package com.cmput301w19t12.bookbuddies;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;

public class MessageChatTest {
    private ArrayList<Message> messages;
    private Chat chat;
    private Message message;
    private String text;
    private Date time;
    private User sender;
    private String Username;
    private String Password;


    @Before
    public void setUp() throws Exception {
        Username = "username";
        Password = "password";
        chat = new Chat();
        text = "test message";
        time = new Date();
        sender = new User(Username, Password);
        message = new Message();
        message.setMessageText(text);
        message.setMessageTime(time);
        message.setSender(sender);
    }

    @Test
    public void messageTest() {
        assertEquals(message.getMessageText(), text);
        assertEquals(message.getMessageTime(), time);
        assertEquals(message.getSender().getUsername(), Username);
        assertEquals(message.getSender().getPassword(), Password);
    }

    @Test
    public void addMessageTest() {
        ArrayList<Message> messages = chat.getMessageList();
        messages.add(message);
        chat.setMessageList(messages);
        assertEquals(chat.getMessageList().get(0), message);
    }
}
