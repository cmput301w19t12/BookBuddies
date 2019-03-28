package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MyBorrowingBooksAdapter extends ArrayAdapter<Book> {
    private Context context;
    private TextView title;
    private TextView author;
    private Button button;

    public MyBorrowingBooksAdapter(Context context, ArrayList<Book> entries) {
        super(context, 0, entries);
        this.context = context;
    }


    /**
     * getView generates the individual view created for each entry in the given Array
     *
     * @param position    int
     * @param convertView View
     * @param parent      ViewGroup
     * @return convertView View
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Book book = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_borrowing_books_item,
                    parent, false);
        }

        title = convertView.findViewById(R.id.bookTitle);
        author = convertView.findViewById(R.id.bookAuthor);
         button = convertView.findViewById(R.id.seeBookButton);

        title.setText(book.getBookDetails().getTitle());
        author.setText(book.getBookDetails().getAuthor());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,BookDetailsActivity.class);
                intent.putExtra("book",new Gson().toJson(book));
                intent.putExtra("isBorrowing",true);
                context.startActivity(intent);
            }
        });


        return convertView;
    }
}