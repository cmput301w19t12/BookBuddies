package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class book_details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        /*Intent i = getIntent();
        String ttl = i.getStringExtra(MyLibraryFragment.class.EXTRA_ttl);
        String author = i.getStringExtra(MyLibraryFragment.class.EXTRA_auth);
        String isbn = i.getStringExtra(MyLibraryFragment.class.EXTRA_isbn);
        String owner = i.getStringExtra(MyLibraryFragment.class.EXTRA_owner);
        String status = i.getStringExtra(MyLibraryFragment.class.EXTRA_status);
        String desc = i.getStringExtra(MyLibraryFragment.class.EXTRA_desc);*/
    }
    public void setting(View v){
        EditText textView = (EditText) findViewById(R.id.titleLayout);
        EditText textView2 = (EditText) findViewById(R.id.authorLayout);
        EditText textView3 = (EditText) findViewById(R.id.isbnLayout);
        EditText textView4 = (EditText) findViewById(R.id.ownerLayout);
        EditText textView5 = (EditText) findViewById(R.id.statusLayout);
        EditText textView6 = (EditText) findViewById(R.id.descriptionLayout);
        /*textView.setText("hint");
        textView2.setText("hint");
        textView3.setText("hint");
        textView4.setText("hint");
        textView5.setText("hint");
        textView6.setText("hint");*/
    }
}
