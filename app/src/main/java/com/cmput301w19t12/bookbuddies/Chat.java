package com.cmput301w19t12.bookbuddies;

import java.util.ArrayList;

/**Chat class represents a chat object between two or more users
 * A chat has a list of messages and users
 *
 * @author Team 12
 * @version 1.0*/

public class Chat {
    private ArrayList<Message> messageList;
    private ArrayList<User> userList;

    /**Base constructor*/
    Chat(){
        this.messageList = new ArrayList<>();
        this.userList = new ArrayList<>();
    }

    /**sends a message to all members of a chat*/
    public void sendMessage(){
        // Stuff
    }

    /**Adds a user to the chat group
     * @param user User*/
    public void addUser(User user){
        this.userList.add(user);
    }

    /**Gets user list for the chat
     * @return userList ArrayList<User>*/
    public ArrayList<User> getUsers(){
        return this.userList;
    }

    /**Gets all messages that belong to this chat
     * @return messsageList ArrayList<Messages>*/
    public ArrayList<Message> getMessages(){
        return this.messageList;
    }
}
