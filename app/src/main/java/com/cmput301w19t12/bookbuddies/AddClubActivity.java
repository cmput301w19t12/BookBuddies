/**
 * AddClubActivity
 *
 * March 9/2019
 */
package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Activity which allows the user to create a new book reading club.
 */
public class AddClubActivity extends AppCompatActivity {
    private FirebaseUser user;
    private User owner;
    private FirebaseAuth mAuth;
    private DatabaseReference clubsRef;
    private DatabaseReference userRef;
    private FloatingActionButton saveClubButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);

        userAuthorization();
        getClubOwner();
        configureSaveButton();

    }

    /**
     * Sets the on click listener for the floating action button. If the button is pressed, createClub
     * is called to create the club and save it in the database.
     */
    public void configureSaveButton() {
        saveClubButton = findViewById(R.id.createClub);
        saveClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClub();
            }
        });
    }

    /**
     * Creates the club with the name specified by the user and adds the club to the database before
     * returning to the main activity.
     */
    public void createClub() {
        clubsRef = FirebaseDatabase.getInstance().getReference().child("Clubs");
        String key = clubsRef.push().getKey();
        EditText clubNameField = findViewById(R.id.clubName);
        String name = clubNameField.getText().toString();
        ArrayList<User> membersList = new ArrayList<>();
        membersList.add(owner);
        Club newClub = new Club(owner, name, membersList,key);
        newClub.getGroupChat().getMessageList().add(new Message("Welcome to BookBuddies Chat!", Calendar.getInstance().getTime(),new User("BookBuddies","null")));
        newClub.getEvents().add(new Event());
        newClub.setCurrentBook("");
        if(key != null) {
            clubsRef.child(key).setValue(newClub);
        }
        returnToMainActivity();
    }

    /**
     * Starts the main activity
     */
    public void returnToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Gets the user's information from the database in the form of a user object.
     */
    public void getClubOwner() {
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                owner = dataSnapshot.getValue(User.class);
                configureSaveButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sets the user field to the currently logged in user.
     */
    public void userAuthorization() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }
}
