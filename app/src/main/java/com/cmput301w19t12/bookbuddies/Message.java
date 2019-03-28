package com.cmput301w19t12.bookbuddies;
import java.util.Date;

/**Message class represents a single message in a chat sequence
 * A message contains text, time sent, and the sender of the message
 *
 * @author Team 12
 * @version 1.0*/

public class Message {
    private User messageSender;
    private String messageText;
    private Date messageTime;

    Message(String text, Date time, User sender){
        this.messageSender = sender;
        this.messageText = text;
        this.messageTime = time;
    }

    public void setMessageText(String text){
        this.messageText = text;
    }

    public String getMessageText(){
        return this.messageText;
    }

    public void setMessageTime(Date time){
        this.messageTime = time;
    }

    public Date getMessageTime(){
        return this.messageTime;
    }

    public User getSender(){
        return this.messageSender;
    }

    public String getSenderUsername() {
        return this.messageSender.getUsername();
    }
}
