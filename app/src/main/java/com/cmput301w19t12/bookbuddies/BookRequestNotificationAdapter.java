package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cmput301w19t12.bookbuddies.Notification.BookRequestNotification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;


/**BookRequestNotificationAdapter creates views for entries in an array of BookRequestNotifications
 *
 * @author bgrenier
 * @version 1.0
 *
 * @see BookRequestNotification
 * @see com.cmput301w19t12.bookbuddies.Notification.Notification
 * @see com.cmput301w19t12.bookbuddies.Notification.MyNotificationsActivity*/

public class BookRequestNotificationAdapter extends ArrayAdapter<BookRequestNotification> {
    private ArrayList<BookRequestNotification> entries;
    private BookRequestNotificationAdapter adapter;
    private TextView contents;
    private ImageButton checkButton;
    private Context context;

    public BookRequestNotificationAdapter(Context context, ArrayList<BookRequestNotification> entries){
        super(context,0,entries);
        this.entries = entries;
        this.adapter = this;
        this.context = context;
    }

    /**generates a view for a single entry in the array, the result will be different based on the type
     * of notification*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BookRequestNotification notification = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_book_request_list_item,
                    parent,false);
        }

        contents = convertView.findViewById(R.id.notificationTextView);
        checkButton = convertView.findViewById(R.id.checkButton);

        contents.setText(notification.toString());

        if(notification.getType().equals("request")) {
            /*If the notification is for an initial request, set the text accordingly and make the
            * button link to the book*/
            contents.setText(notification.toStringRequest());
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("Notifications").child("BookRequestNotifications").child(notification.getNotifiedUsername()).child(notification.getID()).removeValue();
                    Intent intent = new Intent(context, BookDetailsActivity.class);
                    intent.putExtra("book", new Gson().toJson(notification.getBook()));
                    intent.putExtra("isOwner", true);
                    entries.remove(notification);
                    adapter.notifyDataSetChanged();
                    context.startActivity(intent);
                }
            });
        }
        else{
            /*If the notification is for an accepted request, set the text accordingly and make the
            * button simply dismiss the notification*/
            contents.setText(notification.toStringAccept());
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("Notifications").child("BookRequestNotifications").child(notification.getNotifiedByUsername()).child(notification.getID()).removeValue();
                    entries.remove(notification);
                    adapter.notifyDataSetChanged();
                }
            });

        }


        return convertView;
    }
}
