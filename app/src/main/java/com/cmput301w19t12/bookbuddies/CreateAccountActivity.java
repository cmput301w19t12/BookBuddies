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

public class CreateAccountActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        registerButton = findViewById(R.id.createButton);
        usernameField = findViewById(R.id.usernameEdit);
        emailField = findViewById(R.id.emailEdit);
        passwordField = findViewById(R.id.passwordEdit);
        passwordConfirmField = findViewById(R.id.passwordConfirmEdit);
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
        try {
            // Attempt to authorize a new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),"Account creation successful",Toast.LENGTH_LONG).show();
                                // if the authorization is valid, create a new user in the database
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = user.getUid();
                                User newUser = new User(userId, username, password, email);
                                userRef.child(userId).setValue(newUser);
                                Log.i("STUFF", "ACCOUNT CREATION SUCCESSFUL");
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),"Account creation failed",Toast.LENGTH_LONG).show();
                                // if an error occurs, print it to the log
                                Log.w("STUFF", "USER ACCOUNT CREATION FAILURE");
                                Log.w("STUFF", task.getException().toString());
                            }
                        }
                    });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Account creation failed",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.e("STUFF",e.getMessage());
            // notify the user that an error has occurred
            Toast.makeText(getApplicationContext(),"Error occurred during registration",Toast.LENGTH_LONG).show();
        }
    }

}
