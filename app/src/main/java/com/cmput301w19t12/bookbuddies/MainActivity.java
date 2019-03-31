//how to make a tabbed activity:
//https://www.youtube.com/watch?v=zcnT-3F-9JA
package com.cmput301w19t12.bookbuddies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.cmput301w19t12.bookbuddies.FirebaseMessaging.Token;
import com.cmput301w19t12.bookbuddies.Notification.MyNotificationsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity implements ClubFragment.OnFragmentInteractionListener,
        BrowseFragment.OnFragmentInteractionListener,
        MyLibraryFragment.OnFragmentInteractionListener {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private User user;
    private String token;

    @Override
    public void onFragmentInteraction(Uri uri) {
        //https://stackoverflow.com/questions/24777985/how-to-implement-onfragmentinteractionlistener
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Toast.makeText(this, String.format("Barcode: %s",
                        result), Toast.LENGTH_LONG).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPageAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLoggedIn();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setUpViewPager(mViewPager);

        //add tabs to the view
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FirebaseApp.initializeApp(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        tokenHandler();
    }

    private void tokenHandler() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        token = task.getResult().getToken();
                        storeToken();
                    }
                });

    }

    private void storeToken() {
       final String userID = FirebaseAuth.getInstance().getUid();
       userRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   if (snapshot.getKey().equals(userID)) {
                       String username = snapshot.getValue(User.class).getUsername();
                       addTokenToDatabase(username);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void addTokenToDatabase(final String username) {
        final DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
        final String userID = FirebaseAuth.getInstance().getUid();
        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Token newToken = new Token(username, token);
                tokenRef.child(userID).setValue(newToken);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkLoggedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainActivity.this,LogInActivity.class));
        }

    }

    private void setUpViewPager(ViewPager v) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new BrowseFragment(), "Browse");
        adapter.addFragment(new MyLibraryFragment(), "My Library");
        adapter.addFragment(new ClubFragment(), "Clubs");

        v.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_view_detailed:
                Intent i = new Intent(this, DetailedBookList.class);
                i.putExtra("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(i);
                break;
            case R.id.action_signOut:
                FirebaseAuth.getInstance().signOut();
                checkLoggedIn();
                break;
            case R.id.action_myProfile:
                getCurrentUser();
                return true;
            case R.id.action_Notifications:
                Intent notificationIntent = new Intent(this, MyNotificationsActivity.class);
                startActivity(notificationIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public void getCurrentUser(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser userDB = mAuth.getCurrentUser();
                String userId = userDB.getUid();
                user = dataSnapshot.child(userId).getValue(User.class);
                Intent profileIntent = new Intent(MainActivity.this,MyProfileActivity.class);
                profileIntent.putExtra("username", user.getUsername());
                startActivity(profileIntent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               //nothing
            }
        });
    }

}