package com.cmput301w19t12.bookbuddies;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**Transaction class represents a book transaction between two users
 * A book transaction has an owner, borrower, location, book and time
 *
 * @author Team 12
 * @version 1.0*/

public class Transaction {
    public User owner;
    private User borrower;
    private Location location;
    private Book book;
    private Date time;
    private String transactionType;
    private String transactionID;
    private boolean ownerScanned;
    private boolean borrowerScanned;

    /**Base constructor for a Transaction
     * @param owner User
     * @param borrower User
     * @param book Book*/
    Transaction(User owner, User borrower, Book book, String transactionType, String key){
        this.owner = owner;
        this.borrower = borrower;
        this.book = book;
        this.ownerScanned = false;
        this.borrowerScanned = false;
        this.transactionType = transactionType;
        this.transactionID = key;

    }

    Transaction(){}


    public void setTransactionType(String transactionType){
        this.transactionType = transactionType;
    }

    public void setTransactionID(String id) {
        this.transactionID = id;
    }

    public String getTransactionID(){
        return this.transactionID;
    }

    public String getTransactionType(){
        return this.transactionType;
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


    public void completeBorrow(){
        if(transactionComplete()) {
            DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("Books").child("Accepted").child(book.getBookDetails().getUniqueID());
            tempRef.removeValue();
            tempRef = FirebaseDatabase.getInstance().getReference("Books").child("Borrowed").child(book.getBookDetails().getUniqueID());
            book.setStatus("Borrowed");
            tempRef.setValue(book);
            FirebaseDatabase.getInstance().getReference("Transactions").child(transactionID).removeValue();
        }
    }

    public void completeReturn(){
        if(transactionComplete()){
            DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("Books").child("Borrowed").child(book.getBookDetails().getUniqueID());
            tempRef.removeValue();
            tempRef = FirebaseDatabase.getInstance().getReference("Books").child("Accepted").child(book.getBookDetails().getUniqueID());
            book.setStatus("Accepted");
            tempRef.setValue(book);
            FirebaseDatabase.getInstance().getReference("Transactions").child(transactionID).removeValue();
        }
    }


    public void transactionToDatabase(){
        FirebaseDatabase.getInstance().getReference("Transactions").child(transactionID).setValue(this);
    }

    public boolean transactionComplete(){
        return ownerScanned && borrowerScanned;
    }

    public void setOwnerScanned(boolean bool){
        this.ownerScanned = bool;
    }

    public boolean getOwnerScanned(){
        return this.ownerScanned;
    }

    public boolean hasOwnerScanned(){
        DatabaseReference temp = FirebaseDatabase.getInstance().getReference("Transactions").child(transactionID);
        temp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Transaction t = dataSnapshot.getValue(Transaction.class);
                ownerScanned = t.getOwnerScanned();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return ownerScanned;
    }

    public void setBorrowerScanned(boolean bool){
        this.borrowerScanned = bool;
    }

    public boolean getBorrowerScanned(){
        return this.borrowerScanned;
    }

    public boolean hasBorrowerScanned(){
        DatabaseReference temp = FirebaseDatabase.getInstance().getReference("Transactions").child(transactionID);
        temp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Transaction t = dataSnapshot.getValue(Transaction.class);
                borrowerScanned = t.getBorrowerScanned();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return borrowerScanned;
    }



}
