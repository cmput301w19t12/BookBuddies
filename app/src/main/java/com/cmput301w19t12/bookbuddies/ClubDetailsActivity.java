package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**Presents the user with all the details of a given club
 *
 * @version 1.0*/

public class ClubDetailsActivity extends AppCompatActivity {

    private DatabaseReference clubsRef;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    String clubName;
    Club myClub;
    TextView clubNameTV;
    TextView clubBookTV;
    TextView clubEventTV;
    Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_details);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        clubName = intent.getStringExtra("CLUB DETAILS NAME");
        intent.removeExtra("CLUB DETAILS NAME");
        clubNameTV = findViewById(R.id.clubDetailsName);
        clubEventTV = findViewById(R.id.clubDetailsClubEvents);
        clubBookTV = findViewById(R.id.clubDetailsBookName);
        actionButton = findViewById(R.id.clubDetailsEditButton);
        getClubInfo();
    }

    /**Retrieves club info from database*/
    public void getClubInfo(){
        clubsRef = FirebaseDatabase.getInstance().getReference("Clubs");
        clubsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Club club;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    club = snapshot.getValue(Club.class);
                    if (club.getName().equals(clubName)) {
                        myClub = club;
                        populateClubInfo();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = mAuth.getCurrentUser();
                String userID = user.getUid();
                User currentUser = dataSnapshot.child(userID).getValue(User.class);
                if(myClub.getOwner().getUsername().equals(currentUser.getUsername())){
                    actionButton.setVisibility(View.VISIBLE);
                    Log.i("Club owner myclub", myClub.getOwner().getUsername());
                } else {
                    //do not have adding members functionality yet, so no need to implement yet
                    actionButton.setText("Join Club");
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**Populates UI fields*/
    public void populateClubInfo(){
        clubNameTV.setText(myClub.getName());
        if(myClub.getCurrentBook()!= null){
            clubBookTV.setText(myClub.getCurrentBook().getBookDetails().getTitle());
        }
        //Do not have events functionality added yet
        //clubEventTV.setText(myClub.getEvents().get(0).getEventId());



    }

}
