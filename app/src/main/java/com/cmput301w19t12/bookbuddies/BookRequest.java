package com.cmput301w19t12.bookbuddies;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**Represents a book request event
 * contains requesterID and username, the requested book and the request ID
 *
 * @author bgrenier
 * @version 1.0
 *
 * @see RequestViewActivity
 * @see RequestViewAdapter*/

public class BookRequest {
    private String requesterID;
    private Book requestedBook;
    private String requestID;
    private String requesterUsername;


    BookRequest(){}

    BookRequest(String requesterID, Book requestedBook, String requestID, String requesterUsername){
        this.requesterID = requesterID;
        this.requestedBook = requestedBook;
        this.requestID = requestID;
        this.requesterUsername = requesterUsername;
    }

    /**returns the requester username
     * @return requesterUsername String*/
    public String getRequesterUsername() {
        return requesterUsername;
    }

    /**sets the requester username
     * @param requesterUsername String*/
    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    /**returns the requesterID
     * @return requesterID String*/
    public String getRequesterID() {
        return requesterID;
    }

    /**Sets the requesterID
     * @param requesterID String*/
    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    /**returns the request ID
     * @return requestID String*/
    public String getRequestID() {
        return requestID;
    }

    /**sets the request ID
     * @param requestID String*/
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    /**returns the requested book
     * @return requestedBook Book*/
    public Book getRequestedBook() {
        return requestedBook;
    }

    /**sets the requested book
     * @param requestedBook Book*/
    public void setRequestedBook(Book requestedBook) {
        this.requestedBook = requestedBook;
    }

    /**Sends the request to the database and notifies the appropriate user*/
    public void Send(){
        // TODO send a push notification
        FirebaseDatabase fire = FirebaseDatabase.getInstance();
        String bookID = requestedBook.getBookDetails().getUniqueID();
        // add request to database
        DatabaseReference ref = fire.getReference("Notifications").child("BookRequests");
        ref.child(requestID).setValue(this);
        // changes the book from available to requested
        fire.getReference("Books").child("Available").child(bookID).removeValue();
        requestedBook.setStatus("Requested");
        fire.getReference("Books").child("Requested").child(bookID).setValue(requestedBook);
    }

    /**Accepts this request on the book, deletes all other request, and opens a transaction*/
    public void Accept(LatLng transactionLocation){
        FirebaseDatabase fire = FirebaseDatabase.getInstance();
        String bookID = requestedBook.getBookDetails().getUniqueID();
        // change the book from requested to accepted
        fire.getReference("Books").child("Requested").child(bookID).removeValue();
        requestedBook.setCurrentBorrower(requesterID);
        requestedBook.setStatus("Accepted");
        fire.getReference("Books").child("Accepted").child(bookID).setValue(requestedBook);
        deleteRequests();
        openTransaction(transactionLocation);
    }

    /**deletes this request*/
    public void deleteThisRequest(){
        FirebaseDatabase fire = FirebaseDatabase.getInstance();
        String bookID = requestedBook.getBookDetails().getUniqueID();
        // delete this request from firebase and remove the book from requested
        final DatabaseReference ref = fire.getReference("Notifications").child("BookRequests").child(requestID);
        ref.removeValue();
        fire.getReference("Books").child("Requested").child(bookID).removeValue();
        bookHasRequests();
    }

    // checks if the book has any other requests attached to it, so that it should remain requested
    private void bookHasRequests(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BookRequests");
        final String thisBookID = requestedBook.getBookDetails().getUniqueID();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    BookRequest request = snap.getValue(BookRequest.class);
                    // if a request for this book exists, add it back to requested
                    if(request.getRequestedBook().getBookDetails().getUniqueID().equals(thisBookID)){
                        FirebaseDatabase.getInstance().getReference("Books").child("Requested").child(thisBookID).setValue(requestedBook);
                        return;
                    }
                }
                // if no other requests exists, it is safe to add it back to available
                requestedBook.setStatus("Available");
                FirebaseDatabase.getInstance().getReference("Books").child("Available").child(thisBookID).setValue(requestedBook);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // deletes all request associated with this book
    private void deleteRequests(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child("BookRequests");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    BookRequest request = snap.getValue(BookRequest.class);
                    // delete if the request book id matches this one
                    if(request.getRequestedBook().getBookDetails().getUniqueID()
                            .equals(requestedBook.getBookDetails().getUniqueID())){
                        String key = snap.getKey();
                        ref.child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // open a new transaction after this request has been accepted
    private void openTransaction(LatLng transactionLocation){
        final Transaction newTransaction = new Transaction();
        FirebaseDatabase fire = FirebaseDatabase.getInstance();
        DatabaseReference requesterRef = fire.getReference("Users").child(requesterID);
        DatabaseReference ownerRef = fire.getReference("Users").child(requestedBook.getOwner());
        newTransaction.setBook(requestedBook);
        newTransaction.setLocation(new MyLatLng(transactionLocation.latitude,transactionLocation.longitude));
        String key = FirebaseDatabase.getInstance().getReference("Transactions").push().getKey();
        newTransaction.setTransactionID(key);

        // get borrower information
        requesterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                newTransaction.setBorrower(temp);
                newTransaction.transactionToDatabase();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // get owner information
        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                newTransaction.setOwner(temp);
                newTransaction.setTransactionType("borrowing");
                newTransaction.transactionToDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
