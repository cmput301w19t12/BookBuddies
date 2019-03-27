package com.cmput301w19t12.bookbuddies;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**Presents the user with a list of pending transactions attached to their account
 *
 * @author bgrenier
 * @version 1.0
 *
 * @see Transaction
 * @see PendingTransactionsAdapter*/


public class PendingTransactionsActivity extends AppCompatActivity {

    ArrayList<Transaction> borrowsListFromMe;
    ArrayList<Transaction> returnsListFromMe;
    ArrayList<Transaction> myReturnsList;
    ArrayList<Transaction> myBorrowsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_transactions);
        DatabaseReference tRef = FirebaseDatabase.getInstance().getReference("Transactions");
        borrowsListFromMe = new ArrayList<>();
        returnsListFromMe = new ArrayList<>();
        myReturnsList = new ArrayList<>();
        myBorrowsList = new ArrayList<>();
        getLists(tRef);
    }

    private void getLists(DatabaseReference ref){
        final String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*Get the transactions this user is participating in, and organize them into the
                * appropriate lists*/
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Transaction t = snap.getValue(Transaction.class);
                    String tOwnerEmail = t.getOwner().getEmailAddress();
                    String tBorrowerEmail = t.getBorrower().getEmailAddress();
                    String tType = t.getTransactionType();
                    if(tOwnerEmail.equals(myEmail) && tType.equals("borrowing")){
                        borrowsListFromMe.add(t);
                    }
                    else if(tOwnerEmail.equals(myEmail) && tType.equals("returning")){
                        returnsListFromMe.add(t);
                    }
                    else if(tBorrowerEmail.equals(myEmail) && tType.equals("borrowing")){
                        myBorrowsList.add(t);
                    }
                    else if(tBorrowerEmail.equals(myEmail) && tType.equals("returning")){
                        myReturnsList.add(t);
                    }
                }
                setAdapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setAdapters(){
        ListView myBorrows = findViewById(R.id.borrowsListTo);
        ListView myReturns = findViewById(R.id.returningListTo);
        ListView borrowsFrom = findViewById(R.id.borrowingListFrom);
        ListView returnsFrom = findViewById(R.id.returningListFrom);

        PendingTransactionsAdapter myBorrowsAdapter = new PendingTransactionsAdapter(this,myBorrowsList);
        PendingTransactionsAdapter myReturnsAdapter = new PendingTransactionsAdapter(this,myReturnsList);
        PendingTransactionsAdapter borrowListFromAdapter = new PendingTransactionsAdapter(this,borrowsListFromMe);
        PendingTransactionsAdapter returnsListFromAdapter = new PendingTransactionsAdapter(this,returnsListFromMe);

        myBorrows.setAdapter(myBorrowsAdapter);
        myReturns.setAdapter(myReturnsAdapter);
        borrowsFrom.setAdapter(borrowListFromAdapter);
        returnsFrom.setAdapter(returnsListFromAdapter);
    }
}
