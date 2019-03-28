package com.cmput301w19t12.bookbuddies;
import java.util.Date;

/**Message class represents a single message in a chat sequence
 * A message contains text, time sent, and the sender of the message
 *
 * @author Team 12
 * @version 1.0*/

public class Message {
    private String text;
    private Date time;
    private User sender;

    /**Base constructor
     * @param messageText String
     * @param messageTime Date
     * @param sender User*/
    Message(String messageText, Date messageTime, User sender){
        this.text = messageText;
        this.time = messageTime;
        this.sender = sender;
    }

    Message(){}

    /**sets the text contents of the message
     * @param messageText String*/
    public void setMessageText(String messageText){
        this.text = messageText;
    }

    /**Gets the contents of the message
     * @return text String*/
    public String getMessageText(){
        return this.text;
    }

    /**Sets the time the message was sent
     * @param messageTime Date*/
    public void setMessageTime(Date messageTime){
        this.time = messageTime;
    }

    /**Gets the time the message was sent
     * @return time Date*/
    public Date getMessageTime(){
        return this.time;
    }

    /**gets the sender user of the message
     * @return sender User*/
    public User getSender(){
        return this.sender;
    }

    /**sets the sender user
     * @param sender User*/
    public void setSender(User sender){
        this.sender = sender;
    }
}
