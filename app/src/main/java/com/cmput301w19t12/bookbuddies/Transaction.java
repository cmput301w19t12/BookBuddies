package com.cmput301w19t12.bookbuddies;

import android.location.Location;
import java.util.Date;

/**Transaction class represents a book transaction between two users
 * A book transaction has an owner, borrower, location, book and time
 *
 * @author Team 12
 * @version 1.0*/

public class Transaction {
    private User owner;
    private User borrower;
    private Location location;
    private Book book;
    private Date time;

    /**Base constructor for a Transaction
     * @param owner User
     * @param borrower User
     * @param book Book*/
    Transaction(User owner, User borrower, Book book, Location location, Date time){
        this.owner = owner;
        this.borrower = borrower;
        this.book = book;
        this.location = location;
        this.time = time;
    }

    /**Gets the time of the transaction
     * @@return time Date*/
    public Date getTime() {
        return this.time;
    }

    /**Sets the time of the transaction
     * @param time Date*/
    public void setTime(Date time) {
        this.time = time;
    }

    /**Gets the book involved in the transaction
     * @return book Book*/
    public Book getBook() {
        return this.book;
    }

    /**Sets the book involved in the transaction
     * @param book Book*/
    public void setBook(Book book) {
        this.book = book;
    }

    /**Gets the location of the transaction
     * @return location Location*/
    public Location getLocation() {
        return this.location;
    }

    /**Sets the location of the transaction
     * @param  location Location*/
    public void setLocation(Location location) {
        this.location = location;
    }

    /**Gets the borrower user of the transaction
     * @return borrower User*/
    public User getBorrower() {
        return this.borrower;
    }

    /**Sets the borrower user of the transaction
     * @param borrower User*/
    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    /**Get the owner user of the transaction
     * @return owner User*/
    public User getOwner() {
        return this.owner;
    }

    /**Sets the owner user of the transaction
     * @param owner User*/
    public void setOwner(User owner) {
        this.owner = owner;
    }

}
