package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
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
    List<Comment> comments;
    CommentAdapter adapter;
    private TextView tvHandle;
    private TextView tvLikeCount;
    private ImageView ivUserProfile;
    private ImageView btnLike;
    private ImageView btnComment;
    private ImageView btnShare;
    private ImageView btnSave;
    private RecyclerView rvComments;
    private RelativeLayout rlComment;
    private EditText etComment;
    private Button btnPostComment;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Swipe to load more comments
        // Set up swipeContainer
        this.swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        this.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                comments.clear();
                queryComments();
                swipeContainer.setRefreshing(false);
            }
        });

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
        this.rvComments = findViewById(R.id.rvComments);
        this.rlComment = findViewById(R.id.rlComment);
        this.etComment = findViewById(R.id.etComment);
        this.btnPostComment = findViewById(R.id.btnPostComment);


        // Set comments and adapter
        this.comments = new ArrayList<>();
        this.adapter = new CommentAdapter(this, this.comments);
        this.rvComments.setAdapter(this.adapter);
        this.rvComments.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String objectId = intent.getStringExtra("postId");
        if(objectId == null) {
            Toast.makeText(this, "No post received", Toast.LENGTH_SHORT).show();
            finish();
        }

        this.loadInformation(objectId);
    }

    private void queryComments() {
        ParseQuery<Comment> q = ParseQuery.getQuery(Comment.class);
        q.include(Comment.KEY_USER);
        q.orderByDescending("createdAt");
        q.whereEqualTo("post", post);
        q.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> _comments, ParseException e) {
                if( e == null ) {
                    comments.addAll(_comments);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PostDetail.this, "Could not retrieve comments", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error getting comments", e);
                }
            }
        });
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

        // Click on comment
        this.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlComment.setVisibility(View.VISIBLE);
            }
        });

        // Click on post comment
        this.btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etComment.getText().toString();
                if( text.isEmpty() ) {
                    Toast.makeText(PostDetail.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseObject comment = new ParseObject("Comment");
                comment.put("user", ParseUser.getCurrentUser());
                comment.put("post", post);
                comment.put("text", text);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if( e == null ) {
                            rlComment.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(PostDetail.this, "Error while posting comment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Query and load all comments
        queryComments();
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