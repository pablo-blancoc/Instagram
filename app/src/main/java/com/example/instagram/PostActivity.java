package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PostActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "PostActivity";

    // Attributes
    EditText etDescription;
    ImageView ivImage;
    Button btnPost;
    Button btnAddImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Find attributes on View
        this.etDescription = findViewById(R.id.etDescription);
        this.btnAddImage = findViewById(R.id.btnAddImage);
        this.btnPost = findViewById(R.id.btnPost);
        this.ivImage = findViewById(R.id.ivImage);
    }
}