package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {

    // Attributes
    private Context context;
    private List<Post> posts;

    public ProfilePostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_image, parent, false);
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
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find items on View
            this.image = itemView.findViewById(R.id.image);
        }

        public void bind(Post post) {

            ParseFile i = post.getImage();
            if (i != null) {
                Glide.with(context)
                        .load(i.getUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(image);
            } else {
                Glide.with(context)
                        .load(R.drawable.error)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(image);
            }

            this.image.setOnClickListener(new View.OnClickListener() {
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
