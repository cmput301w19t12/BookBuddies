package com.cmput301w19t12.bookbuddies;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**Shows the user a list of the books they currently have request on
 *
 * @author bgrenier
 * @verison 1.0
 *
 * @see BookRequest*/

public class MyRequestsActivity extends AppCompatActivity {
    private ArrayList<BookRequest> requestList;
    private ArrayList<String> entries;
    private String myID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);
        requestList = new ArrayList<>();
        entries = new ArrayList<>();
        myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getRequests();
    }

    private void getRequests(){
        // get requests made by this user

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child("BookRequests");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot r : dataSnapshot.getChildren()){
                    BookRequest temp = r.getValue(BookRequest.class);
                    if(temp.getRequesterID().equals(myID)){
                        requestList.add(temp);
                    }
                }
                makeEntries();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void makeEntries(){
        // make formatted strings to show information of each request

        final ListView listView = findViewById(R.id.myRequestsList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,entries);
        listView.setAdapter(adapter);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        for(BookRequest request : requestList){
            // get the username of each book owner to use in the info string
            Book book = request.getRequestedBook();
            final BookDetails details = book.getBookDetails();
            DatabaseReference tempRef = ref.child(book.getOwner());
            tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String entry = String.format("%s\n%s\n%s",details.getTitle(),details.getAuthor(),user.getUsername());
                    entries.add(entry);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
