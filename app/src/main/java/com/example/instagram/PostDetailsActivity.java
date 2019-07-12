package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

public class PostDetailsActivity extends AppCompatActivity {
    public TextView tvUserName;
    public TextView tvDescription;
    //        public TextView timestamp;
    public ImageView postImage;
    public TextView timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_item);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        postImage = (ImageView) findViewById(R.id.ivPost);
        tvUserName = (TextView) findViewById(R.id.tvUsername);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        timestamp = (TextView) findViewById(R.id.timeStamp);

        Bundle extras = getIntent().getExtras();

        tvUserName.setText(extras.getString("username"));
        tvDescription.setText(extras.getString("description"));
        timestamp.setText(extras.getString("timestamp"));
        Glide.with(this).load(extras.getString("imageURL")).into(postImage);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_back,
                R.anim.slide_out_back);
    }
}
