package com.cmput301w19t12.bookbuddies;

import android.app.SearchManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultAdapter extends ArrayAdapter<String[]> {
    public SearchResultAdapter(Context context,ArrayList<String[]> entries){
        super(context,0,entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] entry = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_search_result_item,
                    parent,false);
        }

        TextView title = convertView.findViewById(R.id.bookTitle);
        TextView author = convertView.findViewById(R.id.bookAuthor);
        TextView owner = convertView.findViewById(R.id.bookOwner);
        TextView status = convertView.findViewById(R.id.bookStatus);

        title.setText(entry[0]);
        author.setText(entry[1]);
        owner.setText(entry[2]);
        status.setText(entry[3]);
        convertView.setTag(entry[4]);

        return convertView;
    }
}
