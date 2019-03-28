package com.cmput301w19t12.bookbuddies;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**Allows the user to see all the pending requests on a particular book
 *
 * @author bgrenier
 * @version 1.0
 *
 * @see BookRequest
 * @see RequestViewAdapter*/

public class RequestViewActivity extends AppCompatActivity {

    private ArrayList<BookRequest> entries;
    private String bookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_view);

        // get the id of the book of concern passed in from previous activity
        Bundle b = getIntent().getExtras();
        bookID = b.getString("bookID");
        entries = new ArrayList<>();
        getMatches();
    }


    private void getMatches(){
        // get all the request for this book from firebase

        entries.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child("BookRequests");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    BookRequest temp = snap.getValue(BookRequest.class);
                    if (temp.getRequestedBook().getBookDetails().getUniqueID().equals(bookID)){
                        entries.add(temp);
                    }
                }
                makeList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void makeList(){
        // set the array adapter to the layout
        RequestViewAdapter requestViewAdapter = new RequestViewAdapter(getApplicationContext(),entries);
        ListView listView = findViewById(R.id.requestListView);
        listView.setAdapter(requestViewAdapter);
    }
}
