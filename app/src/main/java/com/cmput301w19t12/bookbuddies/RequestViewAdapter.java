package com.cmput301w19t12.bookbuddies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
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
    RequestViewAdapter adapter;
    ArrayList<BookRequest> entries;

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
                request.Accept();
                acceptButton.setEnabled(false);
                declineButton.setEnabled(false);
                removeAllRequestsForBook(request.getRequestedBook().getBookDetails().getUniqueID());
                adapter.notifyDataSetChanged();
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

}
