package com.cmput301w19t12.bookbuddies;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301w19t12.bookbuddies.Notification.ClubRequestNotification;
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
    String myClubKey;
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
                    String clubKey = snapshot.getKey();
                    if (club.getName().equals(clubName)) {
                        myClub = club;
                        myClubKey = clubKey;
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
                final User currentUser = dataSnapshot.child(userID).getValue(User.class);
                if(myClub.getOwner().getUsername().equals(currentUser.getUsername())){
                    Log.i("Club owner myclub", myClub.getOwner().getUsername());
                    actionButton.setVisibility(View.INVISIBLE);
                    setEditListeners();
                }

                else {
                    actionButton.setText("Join Club");
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            String clubOwnerUsername = myClub.getOwner().getUsername();
                            String key = ref.push().getKey();
                            ClubRequestNotification notification = new ClubRequestNotification(clubOwnerUsername, currentUser.getUsername(), clubName, "PENDING");
                            ref.child("Notifications").child("Club Requests").child(clubOwnerUsername).child(key).setValue(notification);
                            Toast.makeText(ClubDetailsActivity.this, "REQUEST SENT", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setEditListeners(){
        clubNameTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openEditMenu(0);
                return true;
            }
        });
        clubBookTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openEditMenu(1);
                return true;
            }
        });
    }

    public void openEditMenu(final int field){

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.promptUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                switch(field){
                                    case 0:
                                        myClub.setName(userInput.getText().toString());
                                        clubsRef.child(myClubKey).setValue(myClub);
                                        clubName = myClub.getName();
                                        break;
                                    case 1:
                                        myClub.setCurrentBook(userInput.getText().toString());
                                        clubsRef.child(myClubKey).setValue(myClub);
                                        break;
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    /**Populates UI fields*/
    public void populateClubInfo(){
        if(myClub.getName().equals("")) {
            clubNameTV.setText("This Club has no Name.");
        }else{
            clubNameTV.setText(myClub.getName());
        }

        if((myClub.getCurrentBook() == null) || myClub.getCurrentBook().equals("")){
            clubBookTV.setText("No Book Selected.");
        } else{
            clubBookTV.setText(myClub.getCurrentBook());
        }

    }

}
