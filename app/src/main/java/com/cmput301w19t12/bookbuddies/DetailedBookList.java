package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailedBookList extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userID;
    private DatabaseReference userLibRef;
    private ArrayList<String> bookList;
    private ArrayAdapter adapter;
    private ListView detailedBookListView;
    private ArrayList<String> statusList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_book_list);
        detailedBookListView = findViewById(R.id.detailedBookList);
        userID = getIntent().getStringExtra("UserID");
        Log.i("UserID: ", ""+userID);
        bookList = new ArrayList<String>();
        getIntent().removeExtra("UserID");

        initializeStatusList();
        context = this;
        addAllMyTitles(0);

    }

    private void initializeStatusList() {
        statusList = new ArrayList<String>();
        statusList.add("Available");
        statusList.add("Accepted");
        statusList.add("Requested");
        statusList.add("Borrowed");
    }

    public void addAllMyTitles(final int index) {
        userLibRef = FirebaseDatabase.getInstance().getReference("Books").child(statusList.get(index));
        userLibRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (userID.equals(book.getOwner())) {
                        bookList.add(addBookToList(book));
                        Log.i("My Book", book.getOwner()+" | "+book.getBookDetails().getTitle());
                    }
                }
                if (index < statusList.size()-1) {
                    int newIndex = index + 1;
                    addAllMyTitles(newIndex);
                }
                else {
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, bookList);
                    detailedBookListView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String addBookToList(Book aBook) {
        String titleText = "Book Title: " + aBook.getBookDetails().getTitle();
        String statusText = "Book Status: " + aBook.getBookDetails().getTitle();
        String descriptionText = "Book Description: " + aBook.getBookDetails().getTitle();
        String concatText = "\n" + titleText + "\n" + statusText + "\n" + descriptionText + "\n";
        return concatText;
    }
}