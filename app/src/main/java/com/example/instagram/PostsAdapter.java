package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

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
        }

        public void bind(Post post) {
            // Set buttons color
            this.btnLike.setColorFilter(Color.argb(255, 0, 0, 0));
            this.btnComment.setColorFilter(Color.argb(255, 0, 0, 0));
            this.btnShare.setColorFilter(Color.argb(255, 0, 0, 0));
            this.btnSave.setColorFilter(Color.argb(255, 0, 0, 0));



            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUserName.setText(post.getUser().getUsername());
            tvHandle.setText(String.format("@%s", post.getUser().get("handle").toString()));
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
}
