package com.cmput301w19t12.bookbuddies.Notification;

import com.cmput301w19t12.bookbuddies.Book;

/**BookRequestNotification contains all the information needed to show notifications to the user
 * pertaining to book requests
 *
 * @author bgrenier
 * @verion 1.0
 *
 *@see MyNotificationsActivity*/

public class BookRequestNotification  extends Notification{
    private Book book;
    private String ID;
    private String type;

    BookRequestNotification(){}

    /**full constructor*/
    public BookRequestNotification(String notifiedUsername, String notifiedByUsername, Book book, String ID,String type) {
        super(notifiedUsername, notifiedByUsername);
        this.book = book;
        this.ID = ID;
        this.type = type;
    }

    /**returns the request notification type
     * @return type String*/
    public String getType() {
        return this.type;
    }

    /**sets the request notification type
     * @param type String*/
    public void setType(String type) {
        this.type = type;
    }

    /**sets the ID for this notification
     * @param ID String*/
    public void setID(String ID){
        this.ID = ID;
    }

    /**returns the ID for this notification
     * @return ID String*/
    public String getID(){
        return this.ID;
    }

    /**returns the book associated with this notification
     * @return book Book*/
    public Book getBook(){
        return this.book;
    }

    /**sets the book associated with this book
     * @param book Book*/
    public void setBook(Book book){
        this.book = book;
    }

    /**returns the string representation of this notification for a request notification*/
    public String toStringRequest(){
        return String.format("%s would like to borrow %s",getNotifiedByUsername(),book.getBookDetails().getTitle());
    }

    /**returns the string representation of this notification for an accept notification*/
    public String toStringAccept(){
        return String.format("%s has accepted your request for %s",getNotifiedUsername(),book.getBookDetails().getTitle());
    }

    @Override
    public String toString(){
        return null;
    }

}
