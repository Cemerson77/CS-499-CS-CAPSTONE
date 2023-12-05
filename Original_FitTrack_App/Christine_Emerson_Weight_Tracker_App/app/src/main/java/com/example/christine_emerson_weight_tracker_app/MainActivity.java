package com.example.christine_emerson_weight_tracker_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.christineemersonweighttrackingapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView loginLink = findViewById(R.id.login_button);
        Button signUpButton = findViewById(R.id.signup_button);

        // Initialize DatabaseHelper for the main database
        // Added database helper here
        DatabaseHelper dbHelper = new DatabaseHelper(this, "UserDatabase.db");

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

}
