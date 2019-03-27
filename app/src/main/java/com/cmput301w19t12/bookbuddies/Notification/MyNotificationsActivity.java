package com.cmput301w19t12.bookbuddies.Notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cmput301w19t12.bookbuddies.R;
import com.cmput301w19t12.bookbuddies.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyNotificationsActivity extends AppCompatActivity {

    private ArrayList<ClubRequestNotification> clubNotifications;
    private Context context;
    private ListView clubNotifList;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notifications);
        clubNotifications = new ArrayList<ClubRequestNotification>();
        context = this;
        clubNotifList = (ListView) findViewById(R.id.clubRequestListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentUser();
    }

    private void getCurrentUser() {
        final String UserID = FirebaseAuth.getInstance().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(UserID)) {
                        currentUser = snapshot.getValue(User.class);
                    }
                }
                addClubNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addClubNotifications() {
        clubNotifications.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child("Club Requests").child(currentUser.getUsername());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClubRequestNotification notification = snapshot.getValue(ClubRequestNotification.class);
                    clubNotifications.add(notification);
                }
                final CustomNotificationArrayAdapter adapter = new CustomNotificationArrayAdapter(context, clubNotifications);
                clubNotifList.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
