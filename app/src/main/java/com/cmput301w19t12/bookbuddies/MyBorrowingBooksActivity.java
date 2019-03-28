package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyBorrowingBooksActivity extends AppCompatActivity {
    private ArrayList<Book> myBorrows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_borrowing_books);
        myBorrows = new ArrayList<>();
        getMyBorrowingBooks();
    }


    private void getMyBorrowingBooks(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books").child("Borrowed");
        final String myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot book : dataSnapshot.getChildren()){
                    Book temp = book.getValue(Book.class);
                    if(temp.getCurrentBorrower().equals(myID)){
                        myBorrows.add(temp);
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
        ListView listView = findViewById(R.id.borrowingListView);
        final MyBorrowingBooksAdapter adapter = new MyBorrowingBooksAdapter(this,myBorrows);
        listView.setAdapter(adapter);

    }
}
