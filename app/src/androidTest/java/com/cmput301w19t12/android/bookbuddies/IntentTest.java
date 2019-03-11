/**
 * IntentTest.java
 *
 */

package com.cmput301w19t12.android.bookbuddies;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cmput301w19t12.bookbuddies.Book;
import com.cmput301w19t12.bookbuddies.ExpandingMenuListAdapter;
import com.cmput301w19t12.bookbuddies.LogInActivity;
import com.cmput301w19t12.bookbuddies.MainActivity;
import com.cmput301w19t12.bookbuddies.MainActivity;
import com.cmput301w19t12.bookbuddies.R;
import com.cmput301w19t12.bookbuddies.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robotium.solo.Solo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

/**
 * Some intent tests for the functionalities of the BookBuddies application.
 */
@RunWith(AndroidJUnit4.class)
public class IntentTest extends ActivityTestRule<MainActivity> {

    private Solo solo;
    private int position;
    private View view;

    public IntentTest() {
        super(MainActivity.class, true, true);
    }

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), rule.getActivity());
    }

    /**
     * Test for the user log in and log out functionalities.
     */
    @Test
    public void validLogInLogout() {
        validLogIn();
        validLogOut();
    }

    /**
     * Tests for the add and delete club functionalities.
     */
    @Test
    public void addDeleteClubTest() {
        validLogIn();
        addClub();
        assertEquals(true, ensureClubInList());
        deleteClub();
        assertNotEquals(true, ensureClubInList());
        validLogOut();
    }

    /**
     * Iterates through the listview checking returns true if the test club is present in the listview,
     * otherwise it returns false.
     * @return boolean
     */
    public boolean ensureClubInList() {
        ListView clubList =  (ListView) solo.getView(R.id.clubsListView);
        ListAdapter adapter = clubList.getAdapter();

        for (int i = 0; i < adapter.getCount(); i++) {
            String text = (String) adapter.getItem(i);
            if (text.equals("Great club (TEST)")) {
                position = i;
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes the club from the application.
     */
    public void deleteClub() {
        Log.i("Club position", ""+position);
        solo.clickLongInList(2, position);
        solo.clickOnButton("Delete");
    }

    /**
     * Adds a club to the application.
     */
    public void addClub() {
        solo.clickOnText("Clubs");
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.addClubButton));
        solo.enterText((EditText) solo.getView(R.id.clubName), "Great club (TEST)");
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.createClub));
        solo.clickOnText("Clubs");
    }

    /**
     * Uses invalid information to log into the database, ensuring the unsuccessful log in message
     * is displayed and that the main activity is not displayed.
     */
    @Test
    public void invalidInfoLogIn() {
        solo.clearEditText((EditText) solo.getView(R.id.passwordEdit));
        solo.clearEditText((EditText) solo.getView(R.id.emailEdit));
        solo.enterText((EditText) solo.getView(R.id.emailEdit), "FakeInfo@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEdit), "222222");
        solo.clickOnButton("Login");
        solo.searchText("USER DOES NOT EXIST");
    }


    /**
     * Uses valid user information to log in to the application and ensures that the successful log in
     * message is displayed and that the user is able to view the main activity of the application.
     */
    public void validLogIn() {
        solo.clearEditText((EditText) solo.getView(R.id.passwordEdit));
        solo.clearEditText((EditText) solo.getView(R.id.emailEdit));
        solo.enterText((EditText) solo.getView(R.id.emailEdit), "grenierb96@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEdit), "123456");
        solo.clickOnButton("Login");
        assertEquals(true, solo.searchText("grenierb96@gmail.com is signed in"));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Signs out of the application. Checks for the successful logout message ensuring that the test
     * succeeded.
     */
    public void validLogOut() {
        solo.clickOnMenuItem("SignOut");
        assertEquals("Successfully signed out", true, solo.searchText("Sign in to your account"));
    }


    /**
     * Adds a new test book and uses findTitleInList to ensure that the book was added and displayed
     * successfully.
     */
    @Test
    public void addBookTest() {
        validLogIn();
        solo.clickOnMenuItem("My Profile");
        solo.clickOnButton("Add New Book");
        solo.enterText((EditText) solo.getView(R.id.titleEdit), "Hunger Games (TEST)");
        solo.enterText((EditText) solo.getView(R.id.authorEdit), "Suzanne Collins");
        solo.enterText((EditText) solo.getView(R.id.ISBNEdit), "122255566");
        solo.enterText((EditText) solo.getView(R.id.DesEdit), "This is a book for Intent Testing");
        scrollDown();
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.addButton));
        solo.waitForText("My Library");
        solo.clickOnText("My Library");
        solo.clickOnText("Available");
        assertEquals(true, findTitleInList());
        removeTestTitle();
        validLogOut();
    }

    /**
     * Removes the Book that was added during the intent test from the database.
     */
    public void removeTestTitle() {
        solo.sleep(500);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Books").child("Available");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book.getBookDetails().getTitle().equals("Hunger Games (TEST)")) {
                        ref.child(snapshot.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser databaseUser = mAuth.getCurrentUser();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(databaseUser.getUid()).child("Books").child("Owned").child("Available");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book.getBookDetails().getTitle().equals("Hunger Games (TEST)")) {
                        userRef.child(snapshot.getKey()).removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Scroll down the current screen
     */
    //https://stackoverflow.com/questions/11682196/fast-scroll-in-robotium
    public void scrollDown() {
        int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();

        int fromX, toX, fromY, toY = 0,stepCount=1;

        // Scroll Down // Drag Up
        fromX = screenWidth/2;
        toX = screenWidth/2;
        fromY = (screenHeight/2) + (screenHeight/3);
        toY = (screenHeight/2) - (screenHeight/3);

        solo.drag(fromX, toX, fromY, toY, stepCount);
    }

    /**
     * Returns true if the book title is added to the list of Available books, else returns false.
     * @return boolean
     */
    public boolean findTitleInList() {
        ExpandableListView list = (ExpandableListView) solo.getCurrentActivity().findViewById(R.id.ExpandingMenu);
        ExpandingMenuListAdapter adapter = (ExpandingMenuListAdapter) list.getExpandableListAdapter();
        return adapter.searchForTitle(0, "Hunger Games (TEST)");
    }

}
