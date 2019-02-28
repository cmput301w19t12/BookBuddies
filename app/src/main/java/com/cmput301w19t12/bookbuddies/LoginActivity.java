package com.cmput301w19t12.bookbuddies;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**Login activity to allow users to sign into an already created account
 *
 * @author bgrenier
 * @version 1.0*/


public class LoginActivity extends AppCompatActivity {
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private Button signInButton;


    /**onCreate method initializes instance attributes and sets a click listener for the
     * sign in confirmation button*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        emailField = findViewById(R.id.emailLogin);
        passwordField = findViewById(R.id.passwordLogin);
        signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // attempt to sign
                signIn();
            }
        });

    }

    /**attempts to sign the user into a previously created account*/
    public void signIn(){
        // get string contents of the sign in fields
        final String email = emailField.getText().toString();
        final String password = passwordField.getText().toString();

        try {
            // attempt to sign in using the email and password of the user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // if auth is successful, get the user and make a toast
                                FirebaseUser user = mAuth.getCurrentUser();
                                // UPDATE UI STUFF HERE
                                Toast.makeText(LoginActivity.this, String.format("%s is signed in",
                                        email), Toast.LENGTH_LONG).show();
                                Log.i("STUFF", "LOGIN WORKED");
                                finish();
                            } else {
                                // else print error to the log
                                Toast.makeText(LoginActivity.this, "USER DOES NOT EXIST", Toast.LENGTH_LONG).show();
                                Log.i("STUFF", "LOGIN ERROR");
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            Log.e("STUFF",e.getMessage());
            // notify the user that an error has occurred
            Toast.makeText(getApplicationContext(),"Error occurred during sign in",Toast.LENGTH_LONG).show();
        }

    }

}

