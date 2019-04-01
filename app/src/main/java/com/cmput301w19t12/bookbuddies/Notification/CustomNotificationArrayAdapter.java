/**
 * CustomNotificationArrayAdapter
 *
 * @Author team12
 *
 * March 31, 2019
 */

package com.cmput301w19t12.bookbuddies.Notification;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cmput301w19t12.bookbuddies.Club;
import com.cmput301w19t12.bookbuddies.R;
import com.cmput301w19t12.bookbuddies.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * An array adapter for club request notifications. It displays the string format of the notification
 * as well as two buttons to either accept the club join request or to deny it.
 */
public class CustomNotificationArrayAdapter extends ArrayAdapter<ClubRequestNotification> {
    private final Context context;
    private final ArrayList<ClubRequestNotification> Requests;
    private String username;
    private User userToAdd;
    private CustomNotificationArrayAdapter adapter;

    public CustomNotificationArrayAdapter(@NonNull Context context, @NonNull ArrayList<ClubRequestNotification> Requests) {
        super(context, 0, Requests);
        this.context = context;
        this.Requests = Requests;
        this.adapter = this;
    }

    /**
     * Sets the text and the functionality of the accept and deny button and returns the configured
     * view.
     * @param position:int
     * @param convertView:View
     * @param parent:ViewGroup
     * @return convertView:View
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View notifItem = convertView;

        if (notifItem == null) {
            notifItem = LayoutInflater.from(context).inflate(R.layout.notification_list_item, parent, false);
        }

        final ClubRequestNotification mRequest = Requests.get(position);
        username = mRequest.getNotifiedUsername();

        TextView textView = (TextView) notifItem.findViewById(R.id.notificationTextView);
        ImageButton acceptButton = (ImageButton) notifItem.findViewById(R.id.acceptButton);
        ImageButton denyButton = (ImageButton) notifItem.findViewById(R.id.denyButton);

        String text;
        if (mRequest.getStatus().equals("ACCEPTED")) {
             text = mRequest.acceptedString();
             textView.setText(text);
             denyButton.setVisibility(View.INVISIBLE);
             acceptButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     removeNotification(mRequest);
                     Log.i("Notification", "Was removed");
                 }
             });
        }
        else {
            text = mRequest.getClubName() + "\n" + mRequest.toString();
            textView.setText(text);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClubRequestNotification notification = new ClubRequestNotification(mRequest.getNotifiedByUsername(), mRequest.getNotifiedUsername(), mRequest.getClubName(), "ACCEPTED");
                    notifyRequester(notification);
                    Log.i("DOne notifying", "");
                    getUserAndAdd(mRequest);
                    Log.i("DOne adding member", "");
                    removeNotification(mRequest);
                    Log.i("DOne removing notif", "");
                    adapter.notifyDataSetChanged();
                }
            });

            denyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClubRequestNotification notification = new ClubRequestNotification(mRequest.getNotifiedByUsername(), mRequest.getNotifiedUsername(), mRequest.getClubName(), "DENIED");
                    removeNotification(mRequest);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        return notifItem;
    }

    /**
     * Obtains the information of the user who has attempted to join the club and calls addUser once it
     * is found.
     * @param mRequest:ClubRequestNotification
     */
    private void getUserAndAdd(final ClubRequestNotification mRequest) {
        final String UserID = FirebaseAuth.getInstance().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue(User.class).getUsername().equals(mRequest.getNotifiedByUsername())) {
                        userToAdd = snapshot.getValue(User.class);
                        Log.i("User to add", userToAdd.getUsername());
                    }
                }
                addUser(mRequest,userToAdd);
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Adds the user to the club and updates the club information in the database to reflect
     * the addition of the new member.
     * @param notification:ClubRequestNotification
     * @param user:User
     */
    private void addUser(final ClubRequestNotification notification, final User user) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Clubs");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Club club = snapshot.getValue(Club.class);
                    if (club.getName().equals(notification.getClubName())) {
                        ArrayList<User> membersList = club.getMembersList();
                        membersList.add(user);
                        club.setMembersList(membersList);
                        ref.child(snapshot.getKey()).removeValue();
                        String key = club.getClubID();
                        ref.child(key).setValue(club);
                        Log.i("Deleting club and added new club", "");
                    }
                }
            }
            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Once the user has either been added or the request to join the club has been denied then the
     * notification is removed from the database.
     * @param request:ClubRequestNotification
     */
    private void removeNotification(final ClubRequestNotification request) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child("Club Requests").child(username);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        if (snapshot.getValue(ClubRequestNotification.class).getClubName().equals(request.getClubName())) {
                            ref.child(snapshot.getKey()).removeValue();
                            Log.i("Requested club", request.getClubName());
                            Requests.remove(request);
                        }
                    }
                    catch (NullPointerException e) {
                        //there are no requests
                    }
                }
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * If the request has been accepted by the owner of the club then the requester is notified
     * that they have been added to the club. This is done by adding a new notification to the
     * database so that the requester can see it the next time they are using the application.
     * @param notification:ClubRequestNotification
     */
    private void notifyRequester(ClubRequestNotification notification) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child("Club Requests").child(notification.getNotifiedUsername());
        String key = ref.push().getKey();
        ref.child(key).setValue(notification);
    }
}
