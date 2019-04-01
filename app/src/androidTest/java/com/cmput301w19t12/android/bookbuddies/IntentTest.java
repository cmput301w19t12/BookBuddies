/**
 * IntentTest.java
 *
 * Ensure you are logged out of application before running
 */

package com.cmput301w19t12.android.bookbuddies;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.cmput301w19t12.bookbuddies.Book;
import com.cmput301w19t12.bookbuddies.Club;
import com.cmput301w19t12.bookbuddies.ClubDetailsActivity;
import com.cmput301w19t12.bookbuddies.DetailedBookList;
import com.cmput301w19t12.bookbuddies.ExpandingMenuListAdapter;
import com.cmput301w19t12.bookbuddies.MainActivity;
import com.cmput301w19t12.bookbuddies.MyProfileActivity;
import com.cmput301w19t12.bookbuddies.Notification.CustomNotificationArrayAdapter;
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
    private User currentTestUser;
    private Club testClub;
    private User secondTestUser;

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
     * Needs more work.
     */
    @Test
    public void addDeleteClubTest() {
        String clubName = "Great club (TEST)";
        validLogIn();
        addClub();
        assertEquals(true, ensureClubInList(clubName));
        deleteClub();
        assertNotEquals(true, ensureClubInList(clubName));
        validLogOut();
    }

    /**
     * Iterates through the listview checking returns true if the test club is present in the listview,
     * otherwise it returns false.
     * @return boolean
     */
    public boolean ensureClubInList(final String clubName) {
        ListView clubList =  (ListView) solo.getView(R.id.clubsListView);
        Log.i("Count", Integer.toString(clubList.getAdapter().getCount()));
        for (int i = 0; i < clubList.getAdapter().getCount(); i++) {
            Log.d("Club name testing", clubList.getItemAtPosition(i).toString());
            if (clubList.getItemAtPosition(i).toString().equals(clubName)) {
                position = i;
                Log.i("Position", Integer.toString(i));
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes the club from the application.
     */
    public void deleteClub() {
        solo.clickInList(position+1, 1);
        solo.clickOnButton("Delete Club");
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
        solo.waitForText("grenierb96@gmail.com is signed in");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Signs out of the application. Checks for the successful logout message ensuring that the test
     * succeeded.
     */
    public void validLogOut() {
        solo.clickOnMenuItem("SignOut");
        solo.searchText("Successfully signed out");
    }


    /**
     * Adds a new test book and uses findTitleInList to ensure that the book was added and displayed
     * successfully.
     */
    @Test
    public void addBookTest() {
        validLogIn();
        addBook();
        solo.waitForText("My Library");
        solo.clickOnText("My Library");
        solo.clickOnText("Available");
        assertEquals(true, findTitleInList());
        removeTestTitle("Hunger Games (TEST)");
        validLogOut();
    }

    private void addBook() {
        solo.clickOnText("My Library");
        FloatingActionButton button = (FloatingActionButton) solo.getView(R.id.addNewBook);
        solo.clickOnView(button);
        solo.enterText((EditText) solo.getView(R.id.titleEdit), "Hunger Games (TEST)");
        solo.enterText((EditText) solo.getView(R.id.authorEdit), "Suzanne Collins");
        solo.enterText((EditText) solo.getView(R.id.ISBNEdit), "122255566");
        solo.enterText((EditText) solo.getView(R.id.DesEdit), "This is a book for Intent Testing");
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.addButton));
    }

    /**
     * Removes the Book that was added during the intent test from the database.
     */
    public void removeTestTitle(final String keyWord) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Books").child("Available");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book.getBookDetails().getTitle().contains(keyWord)) {
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

//    /**
//     * Scroll down the current screen
//     */
//    //https://stackoverflow.com/questions/11682196/fast-scroll-in-robotium
//    public void scrollDown() {
//        int screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
//        int screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
//
//        int fromX, toX, fromY, toY = 0,stepCount=1;
//
//        // Scroll Down // Drag Up
//        fromX = screenWidth/2;
//        toX = screenWidth/2;
//        fromY = (screenHeight/2) + (screenHeight/3);
//        toY = (screenHeight/2) - (screenHeight/3);
//
//        solo.drag(fromX, toX, fromY, toY, stepCount);
//    }

    /**
     * Returns true if the book title is added to the list of Available books, else returns false.
     * @return boolean
     */
    public boolean findTitleInList() {
        ExpandableListView list = (ExpandableListView) solo.getView(R.id.ExpandingMenu);
        ExpandingMenuListAdapter adapter = (ExpandingMenuListAdapter) list.getExpandableListAdapter();
        for (int i = 0; i < adapter.getChildrenCount(0); i++) {
            if (adapter.getChild(0, i).toString().contains("(TEST)")) {
                position = i;
                return true;
            }
        }
        return false;
    }

    /**
     * Test for when the user enters a valid email but an invalid password.
     */
    @Test
    public void validEmailInvalidPassword() {
        solo.enterText((EditText) solo.getView(R.id.emailEdit), "grenierb96@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEdit), "Incorrect Password");
        solo.clickOnButton("Login");
        solo.searchText("USER DOES NOT EXIST");
    }

    @Test
    public void userProfileEditTest() {
        testLogin();
        solo.clickOnMenuItem("My Profile");
        solo.assertCurrentActivity("Wrong activity", MyProfileActivity.class);
        solo.clickLongOnView((TextView) solo.getView(R.id.profileFullName));
        solo.enterText((EditText) solo.getView(R.id.promptUserInput), "New Name");
        solo.clickOnButton("OK");
        solo.clickLongOnView((TextView) solo.getView(R.id.profileEmailAddr));
        solo.enterText((EditText) solo.getView(R.id.promptUserInput), "newemail@gmail.com");
        solo.clickOnButton("OK");
        solo.clickLongOnView((TextView) solo.getView(R.id.profilePhoneNum));
        solo.enterText((EditText) solo.getView(R.id.promptUserInput), "000-000-0000");
        solo.clickOnButton("OK");
        solo.searchText("New Name");
        solo.searchText("newemail@gmail.com");
        solo.searchText("000-000-0000");
        revertTestInfo();
        solo.goBack();
        solo.goBack();
        solo.goBack();
        solo.goBack();
        solo.goBack();
        solo.goBack();
        solo.goBack();
        validLogOut();
    }

    private void revertTestInfo() {
        solo.clickLongOnView((TextView) solo.getView(R.id.profileFullName));
        solo.enterText((EditText) solo.getView(R.id.promptUserInput), "Test");
        solo.clickOnButton("OK");
        solo.clickLongOnView((TextView) solo.getView(R.id.profileEmailAddr));
        solo.enterText((EditText) solo.getView(R.id.promptUserInput), "testaccount@gmail.com");
        solo.clickOnButton("OK");
        solo.clickLongOnView((TextView) solo.getView(R.id.profilePhoneNum));
        solo.enterText((EditText) solo.getView(R.id.promptUserInput), "111-111-1111");
        solo.clickOnButton("OK");

    }

    private void testLogin() {
        solo.clearEditText((EditText) solo.getView(R.id.passwordEdit));
        solo.clearEditText((EditText) solo.getView(R.id.emailEdit));
        solo.enterText((EditText) solo.getView(R.id.emailEdit), "testaccount@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEdit), "123456");
        solo.clickOnButton("Login");
        solo.waitForText("testaccount@gmail.com is signed in");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void detailedBookListTest() {
        solo.clickOnMenuItem("Detailed Book List");
        solo.assertCurrentActivity("Wrong Activity", DetailedBookList.class);
    }

    @Test
    public void searchBookTest() {
        testLogin();
        addBook();
        validLogOut();

        validLogIn();
        final View search = (SearchView) solo.getView(R.id.bookSearch);
        ((SearchView) search).setQuery("Hunger Games (TEST)", true);
        ListView list = (ListView) solo.getView(R.id.listView);
        assertEquals(1, list.getAdapter().getCount());
        removeTestTitle("(TEST)");
        validLogOut();
    }

    @Test
    public void searchAndClubDetails() {
        testLogin();
        solo.clickOnText("Clubs");
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.addClubButton));
        solo.enterText((EditText) solo.getView(R.id.clubName), "TEST CLUB");
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.createClub));
        solo.waitForText("Clubs");
        validLogOut();

        validLogIn();
        solo.clickOnText("Clubs");
        solo.clickOnView((SearchView) solo.getView(R.id.clubSearch));
        ((SearchView) solo.getView(R.id.clubSearch)).setQuery("TEST CLUB", true);
        solo.assertCurrentActivity("Wrong activity", ClubDetailsActivity.class);
        solo.searchText("TEST CLUB");
        solo.goBack();
        validLogOut();

        removeClubFromDatabase();
    }

    private void removeClubFromDatabase() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Clubs");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.getValue(Club.class).getOwner().getUsername().equals("Test Account")) {
                        ref.child(snap.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Test
    public void joinClubTest() {
        testLogin();
        solo.clickOnText("Clubs");
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.addClubButton));
        solo.enterText((EditText) solo.getView(R.id.clubName), "TEST CLUB");
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.createClub));
        solo.waitForText("Clubs");
        validLogOut();

        validLogIn();
        solo.clickOnText("Clubs");
        solo.clickOnView((SearchView) solo.getView(R.id.clubSearch));
        ((SearchView) solo.getView(R.id.clubSearch)).setQuery("TEST CLUB", true);
        solo.assertCurrentActivity("Wrong activity", ClubDetailsActivity.class);
        solo.searchText("TEST CLUB");
        solo.clickOnButton("Join Club");
        solo.waitForText("REQUEST SENT");
        solo.goBack();
        validLogOut();

        testLogin();
        solo.clickOnText("Clubs");
        solo.clickOnButton("Club Requests");
        ListView list = (ListView) solo.getView(R.id.clubRequestListView);
        assertNotNull(list);
        removeClubFromDatabase();
        removeNotification();
        validLogOut();
    }

    private void removeNotification() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications")
                .child("Club Requests")
                .child("Test Account");
        ref.removeValue();
    }

    @Test
    public void clubChatTest() {
        testLogin();
        addClub();
        validLogOut();
        validLogIn();
        solo.clickOnText("Clubs");
        solo.clickOnView((SearchView) solo.getView(R.id.clubSearch));
        ((SearchView) solo.getView(R.id.clubSearch)).setQuery("Great club (TEST)", true);
        solo.assertCurrentActivity("Wrong activity", ClubDetailsActivity.class);
        solo.searchText("Great club (TEST)");
        solo.clickOnButton("Club Chat");
        solo.enterText((EditText) solo.getView(R.id.messageContents), "TEST MESSAGE");
        solo.clickOnView(solo.getView(R.id.sendButton));
        solo.goBack();
        solo.goBack();
        validLogOut();

        testLogin();
        solo.clickOnText("Clubs");
        solo.clickOnView((SearchView) solo.getView(R.id.clubSearch));
        ((SearchView) solo.getView(R.id.clubSearch)).setQuery("Great club (TEST)", true);
        solo.assertCurrentActivity("Wrong activity", ClubDetailsActivity.class);
        solo.searchText("Great club (TEST)");
        solo.clickOnButton("Club Chat");
        ListView messages = (ListView) solo.getView(R.id.messageList);
        assertEquals(messages.getAdapter().getCount(), 2);
        solo.goBack();
        solo.goBack();
        validLogOut();
        removeClubFromDatabase();
    }
}

