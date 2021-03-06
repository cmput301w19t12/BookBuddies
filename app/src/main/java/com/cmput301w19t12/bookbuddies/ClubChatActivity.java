package com.cmput301w19t12.bookbuddies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;



/**ClubChatActivity allows the user to chat with other users in real time in a forum style format
 * where the newest posts are placed at the top of the window
 *
 * @author bgrenier, dfournier
 * @version 1.0
 *
 * @see Chat
 * @see Message
 * @see Club*/

public class ClubChatActivity extends AppCompatActivity {

    private Club club;
    private ChatMessageAdapter adapter;
    private EditText messageContents;
    private Button sendMessageButton;
    private ListView messageList;
    private Chat chat;
    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_chat);
        Bundle b = getIntent().getExtras();

        sendMessageButton = findViewById(R.id.sendButton);
        messageContents = findViewById(R.id.messageContents);
        // the club that this chat is meant for
        club = new Gson().fromJson(b.getString("club"),Club.class);

        messages = club.getGroupChat().getMessageList();
        chat = club.getGroupChat();
        messageList = findViewById(R.id.messageList);
        adapter = new ChatMessageAdapter(this,messages);
        messageList.setAdapter(adapter);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide the keyboard as the message has already been sent
                hideKeyboard(ClubChatActivity.this);
                final String messageText = messageContents.getText().toString();
                messageContents.getText().clear();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // get the username of the user sending this message
                        User user = dataSnapshot.getValue(User.class);
                        // create a new message and add to firebase
                        Message newMessage = new Message(messageText, Calendar.getInstance().getTime(),user);
                        messages = chat.getMessageList();
                        messages.add(0,newMessage);
                        chat.setMessageList(messages);
                        adapter.notifyDataSetChanged();
                        FirebaseDatabase.getInstance().getReference("Clubs").child(club.getClubID()).child("groupChat").setValue(chat);
                        onStart();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Clubs").child(club.getClubID()).child("groupChat");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // whenever a new message is added, get it from firebase and update the adapter
                chat = dataSnapshot.getValue(Chat.class);
                if(chat != null) {
                    messages = chat.getMessageList();
                    adapter = new ChatMessageAdapter(ClubChatActivity.this,messages);
                    messageList.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**Hides the sorft keyboard
     * @param activity Activity*/
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
