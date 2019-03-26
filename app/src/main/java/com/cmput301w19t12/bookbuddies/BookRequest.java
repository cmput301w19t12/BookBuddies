package com.cmput301w19t12.bookbuddies;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }


    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }


    public Book getRequestedBook() {
        return requestedBook;
    }

    public void setRequestedBook(Book requestedBook) {
        this.requestedBook = requestedBook;
    }

    public void Send(){
        // TODO send a push notification
        FirebaseDatabase fire = FirebaseDatabase.getInstance();
        String bookID = requestedBook.getBookDetails().getUniqueID();
        DatabaseReference ref = fire.getReference("Notifications").child("BookRequests");
        ref.child(requestID).setValue(this);
        fire.getReference("Books").child("Available").child(bookID).removeValue();
        requestedBook.setStatus("Requested");
        fire.getReference("Books").child("Requested").child(bookID).setValue(requestedBook);
    }

    public void Accept(){
        FirebaseDatabase fire = FirebaseDatabase.getInstance();
        String bookID = requestedBook.getBookDetails().getUniqueID();
        fire.getReference("Books").child("Requested").child(bookID).removeValue();
        fire.getReference("Books").child("Accepted").child(bookID).setValue(requestedBook);
        deleteRequests();
        openTransaction();
    }

    private void deleteRequests(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child("BookRequests");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    BookRequest request = snap.getValue(BookRequest.class);
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

    private void openTransaction(){
        final Transaction newTransaction = new Transaction();
        FirebaseDatabase fire = FirebaseDatabase.getInstance();
        DatabaseReference requesterRef = fire.getReference("Users").child(requesterID);
        DatabaseReference ownerRef = fire.getReference("Users").child(requestedBook.getOwner());
        newTransaction.setBook(requestedBook);

        requesterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                newTransaction.setBorrower(temp);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                newTransaction.setOwner(temp);
                newTransaction.setTransactionType("borrowing");
                String key = FirebaseDatabase.getInstance().getReference("Transactions").push().getKey();
                newTransaction.setTransactionID(key);
                newTransaction.transactionToDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
