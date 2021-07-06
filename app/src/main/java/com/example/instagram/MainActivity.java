package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";

    // Attributes
    private Toolbar toolbar;
    private MenuItem loading;
    List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle("Instagram");
        setSupportActionBar(this.toolbar);

        // Instantiate array of posts
        this.posts = new ArrayList<>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        loading = menu.findItem(R.id.progressBar);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.logout ) {
            this.onLogout();
            return true;
        } else if( id == R.id.post ) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
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

        // Create a query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        // Execute in background thread to find all list of objects
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> _posts, ParseException e) {
                if( e != null ) {
                    // Error has ocurred
                    Log.e(TAG, "Error while retrieving posts", e);
                    Toast.makeText(MainActivity.this, "Could not retrieve posts", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // Add all posts to our data array
                    posts.addAll(_posts);
                }
            }
        });
    }
}