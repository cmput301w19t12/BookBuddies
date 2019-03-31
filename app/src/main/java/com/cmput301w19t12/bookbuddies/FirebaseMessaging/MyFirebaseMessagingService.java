package com.cmput301w19t12.bookbuddies.FirebaseMessaging;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.cmput301w19t12.bookbuddies.R;
import com.cmput301w19t12.bookbuddies.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String username;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        int notifID = new Random().nextInt(50000);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notifID, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        storeInServer(token);
    }

    private void storeInServer(final String token) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        final String userID = FirebaseAuth.getInstance().getUid();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(userID)) {
                        username = snapshot.getValue(User.class).getUsername();
                        addToken(token);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToken(final String token) {
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
}