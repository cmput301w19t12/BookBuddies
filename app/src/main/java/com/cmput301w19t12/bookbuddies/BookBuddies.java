package com.cmput301w19t12.bookbuddies;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class BookBuddies extends Application {
    private static User user;
    private static FirebaseUser fUser;
    private static DatabaseReference ref;
    private static ArrayList<String> userInfo;
    private static String username;

    @Override
    public void onCreate() {
        super.onCreate();
        userInfo = new ArrayList<>();
        FirebaseApp.initializeApp(getApplicationContext());
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("STUFF",fUser.getEmail());
        //user = new User("234","bgrenier","234234","EMAIL");
        setUser();
    }



    public void setUser(){
        if(fUser != null){
            ref = FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot thing : dataSnapshot.getChildren()) {
                        if(thing.getKey() == "username"){
                            username = thing.getValue(String.class);
                        }
                        userInfo.add(thing.getKey());
                        Log.i("STUFFY", thing.getValue(String.class));
                    }
                    //user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                  Log.e("DATBASERROR","GOT INSIDE BUT THEN FAILED");

                }
            });
        }
        else{

            Log.e("STUFF","GETTING USER AT APP START FAILED");
        }

    }

    public static String getUsername(){
        //return user.getUsername();
       //return userInfo.get(2);
        return username;
    }

    public String getEmail(){
        return userInfo.get(0);
    }

}
