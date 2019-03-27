package com.cmput301w19t12.bookbuddies;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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



public class BookDetailsActivity extends AppCompatActivity {
    private TextView titleField;
    private TextView authorField;
    private TextView ISBNField;
    private TextView ownerField;
    private TextView statusField;
    private TextView descriptionField;
    private String username;
    private DatabaseReference userRef;
    private DatabaseReference userLibRef;
    private Book book;
    private FloatingActionButton editButton;
    private Button requestBookButton;
    private Button seeRequestsButton;


    /**onCreate method inits all variables and sets click listeners*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Bundle b = getIntent().getExtras();
        book = new Gson().fromJson(b.getString("book"),Book.class);
        BookDetails d = book.getBookDetails();



        titleField = findViewById(R.id.titleEdit);
        authorField = findViewById(R.id.authorEdit);
        ISBNField = findViewById(R.id.ISBNEdit);
        ownerField = findViewById(R.id.ownerEdit);
        statusField = findViewById(R.id.statusEdit);
        descriptionField = findViewById(R.id.DesEdit);
        editButton = findViewById(R.id.editButton);


        requestBookButton = findViewById(R.id.requestButton);
        seeRequestsButton = findViewById(R.id.seeRequestsButton);
        boolean isOwner = b.getBoolean("isOwner");
        if (isOwner){
            requestBookButton.setVisibility(View.INVISIBLE);
            requestBookButton.setClickable(false);
        }
        else{
            checkPriorRequests();
            seeRequestsButton.setVisibility(View.INVISIBLE);
            seeRequestsButton.setClickable(false);
            //editButton.setVisibility(View.INVISIBLE);
            editButton.setClickable(false);

        }
        seeRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"See Requests clicked",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BookDetailsActivity.this,RequestViewActivity.class);
                intent.putExtra("bookID",book.getBookDetails().getUniqueID());
                startActivity(intent);
            }
        });

        requestBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ID = FirebaseAuth.getInstance().getUid();
                final String key = FirebaseDatabase.getInstance().getReference().push().getKey();
                Toast.makeText(getApplicationContext(),"Request Sent",Toast.LENGTH_LONG).show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(ID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User temp = dataSnapshot.getValue(User.class);
                        BookRequest request = new BookRequest(ID,book,key,temp.getUsername());
                        request.Send();
                        requestBookButton.setEnabled(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        getImage(d.getUniqueID());


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailsActivity.this,EditBookDetailsActivity.class);
                intent.putExtra("book",new Gson().toJson(book));
                startActivity(intent);
            }
        });

        ownerField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailsActivity.this, MyProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });




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


        statusField.setText(book.getStatus());
        titleField.setText(d.getTitle());
        authorField.setText(d.getAuthor());
        ISBNField.setText(d.getISBN());
        descriptionField.setText(d.getDescription());

        makePopup();
    }

    private void checkPriorRequests(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child("BookRequests");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ID = FirebaseAuth.getInstance().getUid();
                String bookID = book.getBookDetails().getUniqueID();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    BookRequest r = snap.getValue(BookRequest.class);
                    String bID = r.getRequestedBook().getBookDetails().getUniqueID();
                    if(r.getRequesterID().equals(ID) && bID.equals(bookID)){
                        requestBookButton.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getImage(String ID){
        try {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(ID);

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri.toString())
                            .into((ImageView) findViewById(R.id.bookImage));
                }
            });

        }catch (Exception e){
            Log.e("STUFF",e.getMessage());
        }
    }


    // https://github.com/chathuralakmal/AndroidImagePopup
    private void makePopup(){
        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setImageOnClickClose(true);
        imagePopup.setBackgroundColor(Color.BLACK);
        imagePopup.setFullScreen(true);
        final ImageView image = findViewById(R.id.bookImage);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePopup.initiatePopup(image.getDrawable());
                imagePopup.viewPopup();
            }
        });
    }

}