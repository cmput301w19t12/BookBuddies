package com.cmput301w19t12.bookbuddies.notifications;

//https://www.youtube.com/watch?v=QXPgMUSfYFI

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.cmput301w19t12.bookbuddies.Notification.Notification;
import com.cmput301w19t12.bookbuddies.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseIdService extends FirebaseMessagingService {
    final String NOTIFICATION_CHANNEL_ID = "com.cmput301w19t12.bookbuddies";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            // post notification if needed
            Log.d("Got notif", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
}

    private void updateContent(RemoteMessage.Notification notification) {

    }

    private void showNotification(String title, String body) {

        Log.i("Titleremote", title);
        Log.i("Titlebody", body);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentInfo("Information")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light_normal);

        manager.notify(new Random().nextInt(), notificationBuilder.build());
    }
}
