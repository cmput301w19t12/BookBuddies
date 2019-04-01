/**
 * ClubDetailsActivity
 *
 * @Author team12
 *
 * March 31, 2019
 */
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301w19t12.bookbuddies.FirebaseMessaging.Token;
import com.cmput301w19t12.bookbuddies.Notification.ClubRequestNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import android.os.Message;

import java.util.ArrayList;

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
    Button clubChatButton;
    ArrayList<String> membersList;
    ListView clubMembersListView;

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
        clubChatButton = findViewById(R.id.clubChatButton);
        clubMembersListView = findViewById(R.id.clubMembersListView);
        getClubInfo();

    }

    /**Retrieves club info from database*/
    public void getClubInfo(){
        myClub = null;
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
                if (myClub == null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                    actionButton.setText("Delete Club");
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDeleteConfirmation().show();
                        }
                    });
                    setEditListeners();
                } else if(membersList.contains(currentUser.getUsername())){
                    actionButton.setText("Leave Club");
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Clubs");
                            ArrayList<User> allMembers = myClub.getMembersList();
                            allMembers.remove(currentUser);
                            myClub.setMembersList(allMembers);
                            ref.child(myClubKey).removeValue();
                            myClubKey = ref.push().getKey();
                            ref.child(myClubKey).setValue(myClub);
                            populateClubInfo();
                        }
                    });
                } else {
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

    private android.app.AlertDialog getDeleteConfirmation() {
        return new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Club")
                .setMessage("Are you sure you want to delete this club?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Clubs");
                        ref.child(myClubKey).removeValue();
                        populateClubInfo();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }


    /**Populates UI fields*/
    public void populateClubInfo(){
        membersList = new ArrayList<>();
        for(User member: myClub.getMembersList()){
            membersList.add(member.getUsername());
        }

        clubChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClubDetailsActivity.this,ClubChatActivity.class);
                intent.putExtra("club",new Gson().toJson(myClub));
                startActivity(intent);
            }
        });
        //Do not have events functionality added yet
        //clubEventTV.setText(myClub.getEvents().get(0).getEventId());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, membersList);
        clubMembersListView.setAdapter(adapter);
        clubMembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ClubDetailsActivity.this,MyProfileActivity.class);
                i.putExtra("username", clubMembersListView.getItemAtPosition(position).toString());
                startActivity(i);
            }
        });

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
