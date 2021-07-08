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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

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
        this.btnSignup = findViewById(R.id.btnSignup);

        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(username, password);
            }
        });
        this.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signup(username, password);
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

    /**
     * Signs up in the platform using credentials provided
     * @param username: The username provided
     * @param password: The password provided
     */
    private void signup(String username, String password) {

        // User Parse built-in LogInInBackground so function is not executed on Main thread
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.put("handle", username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    login(username, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Could not signup", Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Could not signup", e);
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