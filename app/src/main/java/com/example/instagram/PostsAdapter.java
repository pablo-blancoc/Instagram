package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    // Attributes
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return this.posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // Attributes
        private TextView tvUserName;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvHandle;
        private TextView tvLikeCount;
        private ImageView ivUserProfile;
        private ImageView btnLike;
        private ImageView btnComment;
        private ImageView btnShare;
        private ImageView btnSave;
        private RelativeLayout rlComment;
        private EditText etComment;
        private Button btnPostComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find items on View
            this.ivImage = itemView.findViewById(R.id.ivImage);
            this.tvDescription = itemView.findViewById(R.id.tvDescription);
            this.tvUserName = itemView.findViewById(R.id.tvUserName);
            this.tvHandle = itemView.findViewById(R.id.tvHandle);
            this.tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            this.ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
            this.btnLike = itemView.findViewById(R.id.btnLike);
            this.btnComment = itemView.findViewById(R.id.btnComment);
            this.btnShare = itemView.findViewById(R.id.btnShare);
            this.btnSave = itemView.findViewById(R.id.btnSave);
            this.rlComment = itemView.findViewById(R.id.rlComment);
            this.etComment = itemView.findViewById(R.id.etComment);
            this.btnPostComment = itemView.findViewById(R.id.btnPostComment);
        }

        public void bind(Post post) {

            Drawable liked = ContextCompat.getDrawable(context, R.drawable.ufi_heart_active);
            Drawable unliked = ContextCompat.getDrawable(context, R.drawable.ufi_heart);

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

            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvHandle.setText(String.format("@%s", post.getUser().getUsername()));
            tvUserName.setText(post.getUser().get("handle").toString());
            tvLikeCount.setText(String.format("%d likes", post.getLikeCount()));
            ParseFile profile = (ParseFile) post.getUser().get("picture");
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(ivImage);
            }
            if (profile != null) {
                Glide.with(context)
                        .load(profile.getUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .circleCrop()
                        .into(ivUserProfile);
            } else {
                Glide.with(context)
                        .load(R.drawable.avatar)
                        .circleCrop()
                        .into(ivUserProfile);
            }

            // Listeners to go to PostDetail
            this.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDetail(post.getObjectId());
                }
            });
            this.tvDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDetail(post.getObjectId());
                }
            });

            // Listeners to go to profile activity
            this.tvHandle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(post.getUser().getObjectId());
                }
            });
            this.ivUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(post.getUser().getObjectId());
                }
            });
            this.tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(post.getUser().getObjectId());
                }
            });


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
                        Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(context, "Error while posting comment", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        }

        /**
         * Sends the postId to DetailActivity so that we can show the complete information of a post
         * @param post: The post we want to show on DetailActivity
         */
        private void onDetail(String post) {
            Intent intent = new Intent(context, PostDetail.class);
            intent.putExtra("postId", post);
            context.startActivity(intent);
        }
    }

    private void goToProfile(String id) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }
}
