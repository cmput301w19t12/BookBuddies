package com.cmput301w19t12.bookbuddies;

import java.util.ArrayList;
import java.util.Calendar;

/**Chat class represents a chat object between two or more users
 * A chat has a list of messages and users
 *
 * @author Team 12
 * @version 1.0*/

public class Chat {
    private ArrayList<Message> messageList;

    /**Base constructor*/
    Chat(){
        this.messageList = new ArrayList<>();
    }

    /**Gets all messages that belong to this chat
     * @return messsageList ArrayList<Messages>*/
    public ArrayList<Message> getMessageList(){
        return this.messageList;
    }

    /**Adds a message to the chat
     * @param messages ArrayList<Message>*/
    public void setMessageList(ArrayList<Message> messages){
        messageList = messages;
    }


}
