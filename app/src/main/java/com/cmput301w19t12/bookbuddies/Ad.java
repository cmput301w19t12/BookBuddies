package com.cmput301w19t12.bookbuddies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Ad extends AppCompatActivity {
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        Bundle bundle=this.getIntent().getExtras();
        int pic=bundle.getInt("image");
        img = (ImageView) findViewById(R.id.imageView2);
        img.setImageResource(pic);
    }
}
