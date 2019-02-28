package com.cmput301w19t12.bookbuddies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**Registration activity
 * Allows the user to create a new account, and stores information in the database
 *
 * @author bgreni
 * @version 1.0*/


public class RegisterActivity extends AppCompatActivity {
    private Button registerButton;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText passwordConfirmField;
    private LoginValidator loginValidator;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;


    /**onCreate method initializes all instance attributes, and sets a click listener
     * for the registration confirmation button
     * @param savedInstanceState Bundle*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        registerButton = findViewById(R.id.register_Button);
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        passwordConfirmField = findViewById(R.id.confirmPassword);
        loginValidator =  new LoginValidator(usernameField,emailField,passwordField,passwordConfirmField);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ensures all info fields follow the required regulations
                loginValidator.runRegistrationTests();
                // if the account credentials are valid, create a new account
                if(loginValidator.isValid()) {
                    createAccount();
                }

            }
        });
    }


    /**Creates a new account in the database*/
    public void createAccount(){
        // get all credentials
        final String username = usernameField.getText().toString();
        final String email = emailField.getText().toString();
        final String password = passwordField.getText().toString();
        // Attempt to authorize a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        // if the authorization is valid, create a new user in the database
                        FirebaseUser user = mAuth.getCurrentUser();
                        // UPDATE UI STUFF HERE
                        String userId = user.getUid();
                        User newUser = new User(userId,username,password,email);
                        userRef.child(userId).setValue(newUser);
                        Log.i("STUFF","ACCOUNT CREATION SUCCESSFUL");
                        finish();
                    }
                    else{
                        // if an error occurs, print it to the log
                        Log.w("STUFF","USER ACCOUNT CREATION FAILURE");
                        Log.w("STUFF",task.getException().toString());
                    }
                }
            });
    }

}

