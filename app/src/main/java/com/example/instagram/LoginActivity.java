package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private TextView tvUsername;
    private TextView tvPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find items on View and add OnClickListener on Login Button pressed
        this.tvUsername = findViewById(R.id.tvUsername);
        this.tvPassword = findViewById(R.id.tvPassword);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = tvUsername.getText().toString();
                String password = tvPassword.getText().toString();
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

    }
}