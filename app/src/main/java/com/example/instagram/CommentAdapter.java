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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    // Attributes
    private Context context;
    private List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseObject comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return this.comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // Attributes
        private ImageView ivProfile;
        private TextView tvName;
        private TextView tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find items on View
            this.ivProfile = itemView.findViewById(R.id.ivProfile);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvComment = itemView.findViewById(R.id.tvComment);
        }

        public void bind(ParseObject comment) {

            // Bind the post data to the view elements
            tvComment.setText(comment.getString("text"));
            tvName.setText(comment.getParseUser("user").getString("handle"));
            ParseFile profile = (ParseFile) comment.getParseUser("user").get("picture");
            if (profile != null) {
                Glide.with(context)
                        .load(profile.getUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .circleCrop()
                        .into(ivProfile);
            } else {
                Glide.with(context)
                        .load(R.drawable.avatar)
                        .circleCrop()
                        .into(ivProfile);
            }

            // Listeners to go to profile activity
            this.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(comment.getParseUser("user").getObjectId());
                }
            });
            this.ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(comment.getParseUser("user").getObjectId());
                }
            });
        }
    }

    private void goToProfile(String id) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }
}
