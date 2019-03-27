package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;



/**
 * Handles the event of a book being passed from one user to the other, either in the event of an
 * initial borrowing, or the return of previously borrowed book.
 * The behaviour of this activity will be chosen dynamically based on whether the current user is the
 * owner or the borrower of the book, and based on if the current transaction is a borrow or a return
 * event
 *
 * @author bgrenier
 * @version 1.0
 *
 * @see Transaction*/


public class BookTransactionActivity extends AppCompatActivity {
    private Transaction transaction;

    // transaction scan codes to modify behaviour of a scan
    private static final int BORROW_OWNER_SCAN = 1;
    private static final int BORROW_BORROWER_SCAN = 2;
    private static final int RETURN_OWNER_SCAN = 3;
    private static final int RETURN_BORROWER_SCAN = 4;

    // relevant fields needed throughout the activity
    private String ISBN;
    private User owner;
    private User borrower;
    private Book book;

    // the only two UI elements associated with this activity
    private TextView instructionBox;
    private Button actionButton;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_transaction);

        instructionBox = findViewById(R.id.instructionBox);
        actionButton = findViewById(R.id.actionButton);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // get the transaction that was passed from the previous activity in JSON format
        Bundle b = getIntent().getExtras();
        transaction = new Gson().fromJson(b.getString("Transaction"),Transaction.class);
        owner = transaction.getOwner();
        borrower = transaction.getBorrower();
        book = transaction.getBook();


        /*The following string of logic will determine what behaviour is required for the rest of
        * the activity*/

        if (transaction.getTransactionType().equals("borrowing")){
            if(user.getEmail().equals(owner.getEmailAddress())){
                instructionBox.setText("Scan book to initiate borrow");
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doOwnerSideBorrow();
                    }
                });
            }
            else if(user.getEmail().equals(borrower.getEmailAddress())){
                instructionBox.setText("Scan after owner to complete transaction");
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doBorrowerSideBorrow();
                    }
                });
            }
        }
        else{
            if(user.getEmail().equals(owner.getEmailAddress())){
                instructionBox.setText("Scan after borrower to complete return");
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doOwnerSideReturn();
                    }
                });
            }
            else if(user.getEmail().equals(borrower.getEmailAddress())){
                instructionBox.setText("Scan to initiate return");
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         doBorrowerSideReturn();
                    }
                });
            }
        }

    }

    /**Initiate the borrowers side of a borrow transaction*/
    public void doBorrowerSideBorrow(){
        if(transaction.hasOwnerScanned()) {
            doScan(BORROW_BORROWER_SCAN);
        }
        else {
            Toast.makeText(this,"WAIT FOR OWNER TO INITIATE TRANSACTION",Toast.LENGTH_LONG).show();
        }
    }

    /**Initiate the owners side of a borrow transaction*/
    public void doOwnerSideBorrow(){
        doScan(BORROW_OWNER_SCAN);
    }

    /**Initiate the owners side of a return transaction*/
    public void doOwnerSideReturn(){
        if(transaction.hasBorrowerScanned()){
            doScan(RETURN_OWNER_SCAN);
        }
        else{
            Toast.makeText(this,"WAIT FOR BORROWER TO INITIATE RETURN",Toast.LENGTH_LONG).show();
        }
    }

    /**Initiate the borrower side of a return transaction*/
    public void doBorrowerSideReturn(){
        doScan(RETURN_BORROWER_SCAN);
    }

    // opens the scanner with a specific intent
    // USER is the scanner intent code
    private void doScan(int USER) {
        Intent intent = new Intent(BookTransactionActivity.this, LivePreviewActivity.class);
        startActivityForResult(intent, USER);

    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if(requestCode == BORROW_OWNER_SCAN){
                ISBN = data.getStringExtra("result");
                verifyOwnership();
            }
            else if(requestCode == BORROW_BORROWER_SCAN){
                ISBN = data.getStringExtra("result");
                verifyBorrower();
                completeBorrow();
            }
            else if(requestCode == RETURN_OWNER_SCAN){
                ISBN = data.getStringExtra("result");
                verifyOwnership();
            }
            else if(requestCode == RETURN_BORROWER_SCAN){
                ISBN = data.getStringExtra("result");
                verifyBorrower();
            }
        }
    }

    // notify the database that the borrower has scanned the book
    private void verifyBorrower(){
        if (ISBN.equals(book.getBookDetails().getISBN())){
            transaction.setBorrowerScanned(true);
            transaction.transactionToDatabase();
        }
    }

    // Finish a return transaction and exit activity
    private void completeReturn(){
        if(transaction.transactionComplete()){
            transaction.completeReturn();
            finish();
        }
    }

    // Finish a borrow transaction and exit activity
    private void completeBorrow(){
        if(transaction.transactionComplete()){
            transaction.completeBorrow();
            finish();
        }
    }

    // Verify that the scanner is the owner of the book
    private void verifyOwnership(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books").child(book.getStatus()).child(book.getBookDetails().getUniqueID());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Book b = dataSnapshot.getValue(Book.class);
                if (b.getBookDetails().getUniqueID().equals(book.getBookDetails().getUniqueID())
                        && b.getOwner().equals(mAuth.getUid())){
                    transaction.setOwnerScanned(true);
                    transaction.transactionToDatabase();
                }

                completeReturn();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
