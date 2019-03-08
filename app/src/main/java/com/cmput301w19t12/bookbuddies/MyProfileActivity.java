package com.cmput301w19t12.bookbuddies;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MyProfileActivity extends AppCompatActivity {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private TextView fullName;
    private TextView username;
    private TextView phoneNum;
    private TextView email;
    private User user;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        fullName = findViewById(R.id.profileFullName);
        username = findViewById(R.id.profileUsername);
        phoneNum = findViewById(R.id.profilePhoneNum);
        email = findViewById(R.id.profileEmailAddr);
        FirebaseUser userDB = mAuth.getCurrentUser();
        userId = userDB.getUid();
        user = new User();


        setTextViews();

        fullName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openEditMenu(0);
                return true;
            }
        });
        username.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openEditMenu(1);
                return true;
            }
        });
        phoneNum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openEditMenu(2);
                return true;
            }
        });
        email.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openEditMenu(3);
                return true;
            }
        });


    }

    public void setTextViews(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser userDB = mAuth.getCurrentUser();
                String userId = userDB.getUid();
                user = dataSnapshot.child(userId).getValue(User.class);

                if(user.getFullName().equals("")) {
                    if(user.getUsername().equals("")) {
                        fullName.setText("John Doe");
                    } else {
                        fullName.setText(user.getUsername());
                    }
                } else {
                    fullName.setText(user.getFullName());
                }

                if(user.getUsername().equals("")) {
                    username.setText("no_username");
                }else {
                    username.setText(user.getUsername());
                }
                phoneNum.setText("Phone: " + user.getPhoneNumber());
                email.setText("Email: " +user.getEmailAddress());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                                        user.setFullName(userInput.getText().toString());
                                        userRef.child(userId).setValue(user);
                                        break;
                                    case 1:
                                        user.setUsername(userInput.getText().toString());
                                        userRef.child(userId).setValue(user);
                                        break;
                                    case 2:
                                        user.setPhoneNumber(userInput.getText().toString());
                                        userRef.child(userId).setValue(user);
                                        break;
                                    case 3:
                                        user.setEmailAddress(userInput.getText().toString());
                                        userRef.child(userId).setValue(user);
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
}
