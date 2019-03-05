package com.cmput301w19t12.bookbuddies;

import android.widget.AutoCompleteTextView;
import android.widget.EditText;

/**LoginValidator object checks and tracks the validity of login info
 * LoginValidator has a bool validLogin, usernameField, emailField and passwordField
 *
 * @author bgrenier
 * @version 1.0*/

public class LoginValidator {
    private boolean validLogin;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText passwordConfirmField;

    /**Registration login constructor
     * @param username AutoCompleteTextView
     * @param email AutoCompleteTextView
     * @param password EditText*/
    LoginValidator(EditText username, EditText email, EditText password, EditText passwordConfirm){
        this.validLogin = true;
        this.usernameField = username;
        this.emailField = email;
        this.passwordField = password;
        this.passwordConfirmField = passwordConfirm;
    }

    /**Regular login constructor
     * @param email AutoCompleteTextView
     * @param password EditText*/
    LoginValidator(EditText email, EditText password){
        this.validLogin = true;
        this.emailField = email;
        this.passwordField = password;
        this.usernameField = null;
        this.passwordField = null;
    }

    /**Run all tests associated with account registration*/
    public void runRegistrationTests(){
        checkRegistrationEmail();
        checkRegistrationPassword();
        checkRegistrationUsername();

    }

    /**returns if all inputs are valid
     * @return validLogin boolean*/
    public boolean isValid(){
        return validLogin;
    }

    /**Checks the validity of the email address given by the user during registration*/
    public void checkRegistrationEmail(){
        //check that this is a real email
    }

    /**checks the validity of the username given by the user during registration*/
    public void checkRegistrationUsername(){
        String username = usernameField.getText().toString();
        // set error to the text field if the username is too short
        if(username.length() < 5){
            usernameField.setError("Username is too short");
            validLogin = false;
            usernameField.getText().clear();
        }
    }

    /**checks the validity of the password given by the user during registration*/
    public void checkRegistrationPassword() {
        String password = passwordField.getText().toString();
        // set error to the text field if the password is too short
        if (password.length() < 5) {
            passwordField.setError("Password is too short");
            validLogin = false;
            passwordField.getText().clear();
            passwordConfirmField.getText().clear();
        }
        // check that both passwords match
        String passwordConfirm = passwordConfirmField.getText().toString();
        if (!(password.equals(passwordConfirm))){
            passwordField.setError("Passwords do not match");
            passwordConfirmField.setError("Passwords do not match");
            validLogin = false;
            passwordField.getText().clear();
            passwordConfirmField.getText().clear();
        }
    }
}
