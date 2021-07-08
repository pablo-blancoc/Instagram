package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

public class PostDetail extends AppCompatActivity {

    // Constants
    private final static String TAG = "PostDetail";

    // Attributes
    TextView tvUserName;
    TextView tvDescription;
    ImageView ivImage;
    TextView tvTimestamp;
    ProgressBar loading;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Find items on View
        this.tvUserName = findViewById(R.id.tvUserName);
        this.tvDescription = findViewById(R.id.tvDescription);
        this.tvTimestamp = findViewById(R.id.tvTimestamp);
        this.ivImage = findViewById(R.id.ivImage);
        this.loading = findViewById(R.id.loading);


        Intent intent = getIntent();
        String objectId = intent.getStringExtra("postId");
        if(objectId == null) {
            Toast.makeText(this, "No post received", Toast.LENGTH_SHORT).show();
            finish();
        }

        this.loadInformation(objectId);
    }

    private void loadInformation(String objectId) {
        this.onStartLoading();

        // Create a query of a post
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // Include the user information
        query.include(Post.KEY_USER);

        // Select only the one with the same objectId
        query.whereEqualTo("objectId",objectId);

        // Do query
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                onEndLoading();

                if(e == null) {
                    if(posts.size() < 1) {
                        Toast.makeText(PostDetail.this, "No post found", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        post = posts.get(0);
                        tvUserName.setText(post.getUser().getUsername());
                        tvDescription.setText(post.getDescription());
                        tvTimestamp.setText(getTimeAgo(post.getCreatedAt()));
                        Glide.with(PostDetail.this)
                                .load(post.getImage().getUrl())
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error)
                                .into(ivImage);
                    }
                } else {
                    Toast.makeText(PostDetail.this, "Error retrieving data", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error retrievinf post from Parse", e);
                    finish();
                }
            }
        });


    }

    private void onStartLoading() {
        this.loading.setVisibility(View.VISIBLE);
        this.tvUserName.setVisibility(View.GONE);
        this.tvDescription.setVisibility(View.GONE);
        this.tvTimestamp.setVisibility(View.GONE);
        this.ivImage.setVisibility(View.GONE);
    }

    private void onEndLoading() {
        this.loading.setVisibility(View.GONE);
        this.tvUserName.setVisibility(View.VISIBLE);
        this.tvDescription.setVisibility(View.VISIBLE);
        this.tvTimestamp.setVisibility(View.VISIBLE);
        this.ivImage.setVisibility(View.VISIBLE);
    }

    private String getTimeAgo(Date createdAt) {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "1 min ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " min ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "1 hr ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hr ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }


}