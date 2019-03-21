package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

/**Search Results activity gets the final query string inputted by the user, and presents all
 * matching results in a listView
 *
 * @author bgrenier
 * @version 1.0*/


public class SearchResultsActivity extends AppCompatActivity {
    private String query;
    private ArrayList<Book> books;
    private ArrayList<String[]> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Bundle b = getIntent().getExtras();
        query = b.getString("query");
        books = new ArrayList<>();
        entries = new ArrayList<>();
        getMatches(query);


    }

    // gets all books that are available and match tot he query
    private void getMatches(final String text){
        books.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String UID = FirebaseAuth.getInstance().getUid();
                ArrayList<String> acceptableStatus = new ArrayList<>();
                acceptableStatus.add("Available");
                acceptableStatus.add("Requested");
                // zearch through all books in the database
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    for (DataSnapshot bookData : category.getChildren()) {
                        Book book = bookData.getValue(Book.class);
                        try {
                            BookDetails details = book.getBookDetails();
                            // add book if it conatains the query and has the acceptable status
                            if ((details.getAuthor().contains(text) || details.getTitle().contains(text))
                                    && acceptableStatus.contains(book.getStatus())) {
                                books.add(book);
                                Log.i("STUFF", bookData.getKey());
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
                makeList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // makes an array of String arrays containing the info needed to creake the entries for the results list
    private void makeList(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()){
                    User temp = user.getValue(User.class);
                    for (Book book : books){
                        // match usernames to the owners of the books
                        if(user.getKey().equals(book.getOwner())){
                            BookDetails details = book.getBookDetails();
                            entries.add(new String[]{details.getTitle(),details.getAuthor(),
                                    temp.getUsername(),book.getStatus(),details.getUniqueID()});
                        }
                    }
                }


                // set custom array adapter to the listView
               SearchResultAdapter resultsAdapter = new SearchResultAdapter(getApplicationContext(),entries);
                ListView listView = findViewById(R.id.listView);
                listView.setAdapter(resultsAdapter);

                // handle clicks on items in the listView
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // get the book that contains the matching id and pass it to book details
                        for (Book book : books){
                            if (book.getBookDetails().getUniqueID().equals(view.getTag())){
                                Intent intent = new Intent(SearchResultsActivity.this,BookDetailsActivity.class);
                                intent.putExtra("book",new Gson().toJson(book));
                                startActivity(intent);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
