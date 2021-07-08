package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    private TextView tvHandle;
    private TextView tvLikeCount;
    private ImageView ivUserProfile;
    private ImageView btnLike;
    private ImageView btnComment;
    private ImageView btnShare;
    private ImageView btnSave;

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
        this.tvHandle = findViewById(R.id.tvHandle);
        this.tvLikeCount = findViewById(R.id.tvLikeCount);
        this.ivUserProfile = findViewById(R.id.ivUserProfile);
        this.btnLike = findViewById(R.id.btnLike);
        this.btnComment = findViewById(R.id.btnComment);
        this.btnShare = findViewById(R.id.btnShare);
        this.btnSave = findViewById(R.id.btnSave);


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
                        bindInformation();
                    }
                } else {
                    Toast.makeText(PostDetail.this, "Error retrieving data", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error retrievinf post from Parse", e);
                    finish();
                }
            }
        });
    }

    private void bindInformation() {

        Drawable liked = ContextCompat.getDrawable(PostDetail.this, R.drawable.ufi_heart_active);
        Drawable unliked = ContextCompat.getDrawable(PostDetail.this, R.drawable.ufi_heart);

        // Query into database if the post has been liked by the user
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Likes");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("post", post);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if( e != null ) {
                    Log.e("PostAdapter","Error while getting likes", e);
                } else {
                    if( objects.size() == 1 ) {
                        post.liked = true;
                        btnLike.setImageDrawable(liked);
                        btnLike.setColorFilter(Color.argb(255, 255, 0, 0));
                    } else {
                        btnLike.setImageDrawable(unliked);
                        btnLike.setColorFilter(Color.argb(255, 0, 0, 0));
                        Log.d("XXX", "NOT LIKED");
                    }
                }
            }
        });

        // Set buttons color and image
        this.btnComment.setColorFilter(Color.argb(255, 0, 0, 0));
        this.btnShare.setColorFilter(Color.argb(255, 0, 0, 0));
        this.btnSave.setColorFilter(Color.argb(255, 0, 0, 0));

        // Add like clickListener
        this.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.liked) {
                    // Delete current likes
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Likes");
                    query.whereEqualTo("user", ParseUser.getCurrentUser());
                    query.whereEqualTo("post", post);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if( e != null ) {
                                Log.e("PostAdapter","Error while unliking", e);
                            } else {
                                for(ParseObject object: objects) {
                                    object.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if( e != null ) {
                                                Log.e("PostAdapter", "Problem unliking", e);
                                            } else {
                                                btnLike.setImageDrawable(unliked);
                                                btnLike.setColorFilter(Color.argb(255, 0, 0, 0));
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                    // Update likecount
                    post.setLikeCount(post.getLikeCount() - 1);

                } else {
                    post.setLikeCount(post.getLikeCount() + 1);
                    // Add like object
                    ParseObject like = new ParseObject("Likes");
                    like.put("user", ParseUser.getCurrentUser());
                    like.put("post", post);
                    like.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if( e != null ) {
                                Log.e("PostsAdapter", "Problem liking", e);
                            } else {
                                btnLike.setImageDrawable(liked);
                                btnLike.setColorFilter(Color.argb(255, 255, 0, 0));
                            }
                        }
                    });
                }
                post.liked = !post.liked;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
                query.getInBackground(post.getObjectId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        object.put(Post.KEY_LIKE_COUNT, post.getLikeCount());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null) {
                                    tvLikeCount.setText(String.format("%d likes", post.getLikeCount()));
                                } else {
                                    Log.e("PostAdapter", "Could not save likeCount", e);
                                }
                            }
                        });
                    }
                });
            }
        });

        this.tvUserName.setText(this.post.getUser().getUsername());
        this.tvDescription.setText(this.post.getDescription());
        this.tvTimestamp.setText(getTimeAgo(this.post.getCreatedAt()));
        this.tvHandle.setText(String.format("@%s", post.getUser().get("handle").toString()));
        this.tvLikeCount.setText(String.format("%d likes", post.getLikeCount()));
        Glide.with(PostDetail.this)
                .load(this.post.getImage().getUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(ivImage);
        ParseFile profile = (ParseFile) post.getUser().get("picture");
        if (profile != null) {
            Glide.with(PostDetail.this)
                    .load(profile.getUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .circleCrop()
                    .into(ivUserProfile);
        } else {
            Glide.with(PostDetail.this)
                    .load(R.drawable.avatar)
                    .circleCrop()
                    .into(ivUserProfile);
        }
    }

    private void onStartLoading() {
        this.tvUserName.setVisibility(View.GONE);
        this.tvDescription.setVisibility(View.GONE);
        this.tvTimestamp.setVisibility(View.GONE);
        this.ivImage.setVisibility(View.GONE);
        this.loading.setVisibility(View.VISIBLE);
        this.tvHandle.setVisibility(View.GONE);
        this.tvLikeCount.setVisibility(View.GONE);
        this.ivUserProfile.setVisibility(View.GONE);
        this.btnLike.setVisibility(View.GONE);
        this.btnComment.setVisibility(View.GONE);
        this.btnShare.setVisibility(View.GONE);
        this.btnSave.setVisibility(View.GONE);
    }

    private void onEndLoading() {
        this.tvUserName.setVisibility(View.VISIBLE);
        this.tvDescription.setVisibility(View.VISIBLE);
        this.tvTimestamp.setVisibility(View.VISIBLE);
        this.ivImage.setVisibility(View.VISIBLE);
        this.loading.setVisibility(View.GONE);
        this.tvHandle.setVisibility(View.VISIBLE);
        this.tvLikeCount.setVisibility(View.VISIBLE);
        this.ivUserProfile.setVisibility(View.VISIBLE);
        this.btnLike.setVisibility(View.VISIBLE);
        this.btnComment.setVisibility(View.VISIBLE);
        this.btnShare.setVisibility(View.VISIBLE);
        this.btnSave.setVisibility(View.VISIBLE);
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