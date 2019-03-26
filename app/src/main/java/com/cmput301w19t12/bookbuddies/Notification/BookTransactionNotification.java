package com.cmput301w19t12.bookbuddies.Notification;

import android.support.annotation.NonNull;

public class BookTransactionNotification extends Notification {

    public BookTransactionNotification(String notifiedUsername, String notifiedByUsername) {
        super(notifiedUsername, notifiedByUsername);
    }

    @NonNull
    @Override
    public String toString() {
        return null;
    }
}
