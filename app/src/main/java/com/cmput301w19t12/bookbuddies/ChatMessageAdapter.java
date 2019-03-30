package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatMessageAdapter extends ArrayAdapter<Message> {
    private TextView senderUsername;
    private TextView messageField;
    private TextView timeField;

    ChatMessageAdapter(Context context, ArrayList<Message> entries){
        super(context,0,entries);
    }


    /**getView generates the individual view created for each entry in the given Array
     * @param position int
     * @param convertView View
     * @param parent ViewGroup
     * @return convertView View*/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Message message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_item,
                    parent, false);
        }


        senderUsername = convertView.findViewById(R.id.senderUsername);
        messageField = convertView.findViewById(R.id.messageField);
        timeField = convertView.findViewById(R.id.timeField);

        senderUsername.setText(message.getSender().getUsername());
        messageField.setText(message.getMessageText());
        timeField.setText(message.getMessageTime().toString());


        return convertView;
    }

}
