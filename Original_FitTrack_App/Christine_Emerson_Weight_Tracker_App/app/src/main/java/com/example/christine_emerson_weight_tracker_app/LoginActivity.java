package com.example.christine_emerson_weight_tracker_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.christineemersonweighttrackingapp.R;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private DatabaseHelper dbHelper;
    private CheckBox rememberMeCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.login_password);
        Button signInButton = findViewById(R.id.login_button);
        TextView signupRedirectText = findViewById(R.id.signupRedirectText);
        rememberMeCheckBox = findViewById(R.id.chkSaveCredentials); // Initialize the CheckBox

        dbHelper = new DatabaseHelper(this, "UserDatabase.db");

        // Check for stored credentials and update UI
        checkAndLoadStoredCredentials();

        signInButton.setEnabled(true);

        emailEditText.addTextChangedListener(new TextWatcher());
        passwordEditText.addTextChangedListener(new TextWatcher());

        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                showError("Email and password are required");
            } else {
                if (isValidUser(email, password)) {
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish(); // Optional: Close the LoginActivity to prevent going back
                } else {
                    showError("Invalid username or password");
                }
            }
        });

        signupRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private boolean isValidUser(String email, String password) {
        return dbHelper.isValidUser(email, password);
    }

    private void showError(String errorMessage) {
        // Implement your error message display logic here
        // Example: Display a Toast with the error message
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
    // Load stored credentials (if "Remember Me" is checked)
    private void checkAndLoadStoredCredentials() {
        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        boolean rememberMe = preferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String storedEmail = preferences.getString("email", "");
            String storedPassword = preferences.getString("password", "");
            emailEditText.setText(storedEmail);
            passwordEditText.setText(storedPassword);
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void saveCredentialsIfChecked() {
        if (rememberMeCheckBox.isChecked()) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("rememberMe", true);
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
        }
    }
    private class TextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean isEmailEmpty = TextUtils.isEmpty(emailEditText.getText().toString());
            boolean isPasswordEmpty = TextUtils.isEmpty(passwordEditText.getText().toString());

            Button signInButton = findViewById(R.id.login_button);
            signInButton.setEnabled(!isEmailEmpty && !isPasswordEmpty);
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Save credentials if "Remember Me" is checked
        saveCredentialsIfChecked();

    }
}

