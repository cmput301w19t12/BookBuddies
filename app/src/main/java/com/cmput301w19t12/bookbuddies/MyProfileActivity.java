package com.cmput301w19t12.bookbuddies;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


/**Presents the user with their profile information
 *
 * @verion 1.0
 *
 * @see User*/

public class MyProfileActivity extends AppCompatActivity {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private TextView fullName;
    private TextView username;
    private TextView phoneNum;
    private TextView email;
    private User user;
    private User userViewed;
    private String usernameToShow;
    private String userId;
    private Button viewPendingTransactions;

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
        viewPendingTransactions = findViewById(R.id.viewPendingTransactionsButton);

        Bundle b = getIntent().getExtras();
        usernameToShow = b.getString("username");

        viewPendingTransactions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewTransactionsList();
            }
        });

        getUserData();

    }

    private void viewTransactionsList(){
        startActivity(new Intent(MyProfileActivity.this,PendingTransactionsActivity.class));
    }

    public void setEditListeners(){
        if(userViewed.getUsername().equals(user.getUsername())){
            enableViewTransactions();
            fullName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openEditMenu(0);
                    return true;
                }
            });
            phoneNum.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openEditMenu(1);
                    return true;
                }
            });
            email.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openEditMenu(2);
                    return true;
                }
            });
        }
    }

    private void enableViewTransactions(){
        viewPendingTransactions.setVisibility(View.VISIBLE);
        viewPendingTransactions.setClickable(true);
    }

    public void getUserData(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser userDB = mAuth.getCurrentUser();
                String userId = userDB.getUid();
                user = dataSnapshot.child(userId).getValue(User.class);
                for (DataSnapshot allUsers : dataSnapshot.getChildren()) {
                    User tempUser = allUsers.getValue(User.class);
                    try {
                        String usernameViewed = tempUser.getUsername();
                        if (usernameViewed.equals(usernameToShow)) {
                            userViewed = tempUser;
                            Log.i("USER", userViewed.getUsername());
                            setTextViews();
                            setEditListeners();

                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**Populates text views*/
    public void setTextViews(){
        if(userViewed.getFullName().equals("")) {
            if(userViewed.getUsername().equals("")) {
                fullName.setText("John Doe");
            } else {
                fullName.setText(userViewed.getUsername());
            }
        } else {
            fullName.setText(userViewed.getFullName());
        }
        if(userViewed.getUsername().equals("")) {
            username.setText("no_username");
        }else {
            username.setText(userViewed.getUsername());
        }
        phoneNum.setText("Phone: " + userViewed.getPhoneNumber());
        email.setText("Email: " +userViewed.getEmailAddress());
    }

    /**Opens menu to edit profile information*/
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
                                        user.setPhoneNumber(userInput.getText().toString());
                                        userRef.child(userId).setValue(user);
                                        break;
                                    case 2:
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
