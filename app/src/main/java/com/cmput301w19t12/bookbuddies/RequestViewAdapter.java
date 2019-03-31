package com.cmput301w19t12.bookbuddies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cmput301w19t12.bookbuddies.Notification.BookRequestNotification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;


/**RequestViewAdapter functions to create a list of requests on the users books that they can either
 * accept of decline
 *
 * @author bgrenier
 * @version 1.0
 * @see ArrayAdapter
 * @see RequestViewActivity
 * @see BookRequest*/


public class RequestViewAdapter extends ArrayAdapter<BookRequest> {
    private TextView usernameField;
    private Button acceptButton;
    private Button declineButton;
    private Context context;
    private RequestViewAdapter adapter;
    private ArrayList<BookRequest> entries;
    private static final int MAPS_INTENT = 4;

    public RequestViewAdapter(Context context, ArrayList<BookRequest> entries){
        super(context,0,entries);
        this.context = context;
        this.adapter = this;
        this.entries = entries;
    }


    /**getView generates the individual view created for each entry in the given Array
     * @param position int
     * @param convertView View
     * @param parent ViewGroup
     * @return convertView View*/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BookRequest request = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_view_list_item,
                    parent, false);
        }

        usernameField = convertView.findViewById(R.id.usernameField);
        acceptButton = convertView.findViewById(R.id.acceptButton);
        declineButton = convertView.findViewById(R.id.declineButton);

        // accepts the clicked request and updates the activity
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("request",new Gson().toJson(request));
                makeRequestNotification(request.getRequesterUsername(),request.getRequestedBook());
                removeAllRequestsForBook(request.getRequestedBook().getBookDetails().getUniqueID());
                acceptButton.setEnabled(false);
                declineButton.setEnabled(false);
                adapter.notifyDataSetChanged();
                ((Activity) context).startActivityForResult(intent,MAPS_INTENT);
            }
        });

        // declines the request and updates the activity
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.deleteThisRequest();
                declineButton.setEnabled(false);
                acceptButton.setEnabled(false);
                entries.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        usernameField.setText(request.getRequesterUsername());

        return convertView;
    }

    private void removeAllRequestsForBook(String thisID){
        for (BookRequest r : entries){
            String rID = r.getRequestedBook().getBookDetails().getUniqueID();
            if(rID.equals(thisID)){
                entries.remove(r);
            }
        }
    }

    private void makeRequestNotification(final String requesterUsername,final Book book) {
        // create a notification in firebase for this request

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(book.getOwner());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                BookRequestNotification notification = new BookRequestNotification(temp.getUsername(), requesterUsername, book, ref.push().getKey(), "request");
                FirebaseDatabase.getInstance().getReference("Notifications").child("BookRequestNotifications").child(notification.getNotifiedByUsername()).child(notification.getID()).setValue(notification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
