package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

/**
 * Allows the user to view the details of a given book
 * details are presented in a series of EditText views
 *
 * @version 1.0*/



public class book_details extends AppCompatActivity {
    private EditText titleField;
    private EditText authorField;
    private EditText ISBNField;
    private EditText ownerField;
    private EditText statusField;
    private EditText descriptionField;
    private String username;
    private DatabaseReference userRef;
    private DatabaseReference userLibRef;
    private Book book;


    /**onCreate method inits all variables and sets click listeners*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Bundle b = getIntent().getExtras();
        final String ID = b.getString("ID");
        book = new Gson().fromJson(b.getString("book"),Book.class);
        titleField = findViewById(R.id.titleLayout);
        authorField = findViewById(R.id.authorLayout);
        ISBNField = findViewById(R.id.isbnLayout);
        ownerField = findViewById(R.id.ownerLayout);
        statusField = findViewById(R.id.statusLayout);
        descriptionField = findViewById(R.id.descriptionLayout);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(book.getOwner()).child("username");
        userLibRef = FirebaseDatabase.getInstance().getReference("Books");


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue(String.class);
                ownerField.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BookDetails d = book.getBookDetails();
        statusField.setText(book.getStatus());
        titleField.setText(d.getTitle());
        authorField.setText(d.getAuthor());
        ISBNField.setText(d.getISBN());
        descriptionField.setText(d.getDescription());


        //getImage(ID);

    }

    private void getImage(String ID){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(ID);
        Glide.with(this).load(ref)
                .into((ImageView) findViewById(R.id.bookImage));
    }

}
