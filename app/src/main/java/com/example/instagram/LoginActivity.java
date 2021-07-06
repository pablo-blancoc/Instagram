package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // There is already an user logged in
        if(ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // Find items on View and add OnClickListener on Login Button pressed
        this.etUsername = findViewById(R.id.etUsername);
        this.etPassword = findViewById(R.id.etPassword);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(username, password);
            }
        });
    }

    /**
     * Attemps to login using credentials given
     * @param username: The username provided
     * @param password: The password provided
     */
    private void login(String username, String password) {

        // User Parse built-in LogInInBackground so function is not executed on Main thread
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if( e != null ) {
                    // Could not login
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error on login: ", e);
                    return;
                } else {
                    // Logged in
                    goMainActivity();
                    Toast.makeText(LoginActivity.this, "Welcome again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // With finish the LoginActivity is removed from back-stack
        finish();
    }
}