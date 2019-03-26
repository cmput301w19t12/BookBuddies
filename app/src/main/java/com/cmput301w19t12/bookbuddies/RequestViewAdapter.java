package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestViewAdapter extends ArrayAdapter<BookRequest> {
    private TextView usernameField;
    private Button acceptButton;

    public RequestViewAdapter(Context context, ArrayList<BookRequest> entries){
        super(context,0,entries);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BookRequest request = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_view_list_item,
                    parent, false);
        }

        usernameField = convertView.findViewById(R.id.usernameField);
        acceptButton = convertView.findViewById(R.id.acceptButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.Accept();
            }
        });

        usernameField.setText(request.getRequesterUsername());

        return convertView;
    }

}
