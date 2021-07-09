package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "ProfileActivity";

    // Attributes
    ParseUser user;
    String uid;
    Toolbar toolbar;
    List<Post> posts;
    MenuItem loading;
    ImageView ivProfile;
    TextView tvName;
    TextView tvHandle;
    TextView tvBio;
    RecyclerView rvPosts;
    ProfilePostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get intent
        Intent intent = getIntent();
        uid = intent.getStringExtra("id");

        // Setup toolbar
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle("");
        setSupportActionBar(this.toolbar);

        // Find items on View
        this.ivProfile = findViewById(R.id.ivProfile);
        this.tvName = findViewById(R.id.tvName);
        this.tvHandle = findViewById(R.id.tvHandle);
        this.tvBio = findViewById(R.id.tvBio);
        this.rvPosts = findViewById(R.id.rvPosts);

        // Instantiate array of posts
        this.posts = new ArrayList<>();

        // Instantiate adapter
        this.adapter = new ProfilePostsAdapter(this, this.posts);
        this.rvPosts.setAdapter(this.adapter);
        this.rvPosts.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 3));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        loading = menu.findItem(R.id.progressBar);

        // Move queryPosts() here so that loading was found in View before it is called
        //  to show that the app is loading
        queryPosts();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.logout ) {
            this.onLogout();
            return true;
        } else if( id == R.id.post ) {
            Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLogout() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if( currentUser == null ) {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            // With finish the MainActivity is removed from back-stack
            finish();
        } else {
            Toast.makeText(this, "An error ocurred", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Could not log out");
        }
    }

    /**
     * Functions that retrieves posts from Parse database
     */
    private void queryPosts() {

        this.loading.setVisible(true);

        // Create a query of Posts
        ParseQuery<ParseUser> q = ParseUser.getQuery();
        q.getInBackground(uid, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(e == null) {
                    user = object;
                    ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                    query.whereEqualTo("user", object);
                    query.orderByDescending("createdAt");
                    query.findInBackground(new FindCallback<Post>() {
                        @Override
                        public void done(List<Post> objects, ParseException e) {
                            if(e == null) {
                                posts.addAll(objects);
                                // Toast.makeText(ProfileActivity.this, "SIZE: " + posts.size(), Toast.LENGTH_LONG).show();
                                bindInformation();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error retrieving information", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error retrieving information", e);
                                finish();
                            }
                            loading.setVisible(false);
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Error retrieving information", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error retrieving information", e);
                    finish();
                    loading.setVisible(false);
                }
            }
        });
    }

    private void bindInformation() {
        this.tvName.setText(user.getString("handle"));
        this.tvHandle.setText(String.format("@%s", user.getUsername()));
        this.tvBio.setText(user.getString("bio"));

        try {
            Glide.with(ProfileActivity.this)
                    .load(user.getParseFile("picture").getUrl())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.error)
                    .circleCrop()
                    .into(ivProfile);
        } catch (NullPointerException e) {
            Glide.with(ProfileActivity.this)
                    .load(R.drawable.avatar)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.error)
                    .circleCrop()
                    .into(ivProfile);
        }
    }

}