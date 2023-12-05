package com.example.christine_emerson_weight_tracker_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.christineemersonweighttrackingapp.R;

public class BmiCalculatorActivity extends AppCompatActivity {
    EditText editTextWeight, editTextHeightFeet, editTextHeightInches;
    RadioGroup radioGroupGender;
    Button buttonCalculate;
    TextView textViewResult;
    SharedPreferences sharedPreferences;

    Button weightButton, goalWeightButton; // Declare the two buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        editTextWeight = findViewById(R.id.editTextWeight);
        editTextHeightFeet = findViewById(R.id.editTextHeightFeet);
        editTextHeightInches = findViewById(R.id.editTextHeightInches);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        textViewResult = findViewById(R.id.textViewResult);

        //Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        //show the logout confirmation dialog when clicked
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Find the home button view
        ImageButton backButton = findViewById(R.id.homeButton);

        // Set a click listener for the home button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the WelcomeActivity
                Intent intent = new Intent(BmiCalculatorActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

        // Initialize the two buttons
        weightButton = findViewById(R.id.weightButton);
        goalWeightButton = findViewById(R.id.goalWeightButton);


        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });

        // Set click listeners for weightButton and goalWeightButton
        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WeightRecordActivity
                Intent intent = new Intent(BmiCalculatorActivity.this, WeightRecordActivity.class);
                startActivity(intent);
            }
        });

        goalWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to EditGoalWeight activity
                Intent intent = new Intent(BmiCalculatorActivity.this, EditGoalWeightActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.logout_confirmation, null);
        builder.setView(dialogView);

        Button confirmLogoutButton = dialogView.findViewById(R.id.confirmLogoutButton);
        Button cancelLogoutButton = dialogView.findViewById(R.id.cancelLogoutButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        confirmLogoutButton.setOnClickListener(v -> {
            // Perform the logout action

            // Clear the user's authentication state (logged in or token, etc.)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false); // Assuming you use a "isLoggedIn" flag
            editor.apply();

            // Display a logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate back to the LoginActivity
            Intent intent = new Intent(BmiCalculatorActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });

        cancelLogoutButton.setOnClickListener(v -> dialog.dismiss());
    }
    private void calculateBMI() {
        String weightStr = editTextWeight.getText().toString();
        String feetStr = editTextHeightFeet.getText().toString();
        String inchesStr = editTextHeightInches.getText().toString();

        if (weightStr.isEmpty() || feetStr.isEmpty() || inchesStr.isEmpty()) {
            textViewResult.setText("Please enter weight, feet, and inches.");
            return;
        }

        float weight = Float.parseFloat(weightStr);
        float heightFeet = Float.parseFloat(feetStr);
        float heightInches = Float.parseFloat(inchesStr);
        int genderId = radioGroupGender.getCheckedRadioButtonId();

        // Convert height from feet and inches to inches
        float heightInInches = (heightFeet * 12) + heightInches;

        // Calculate BMI using pounds (lb) and inches (in)
        float bmi = (weight / (heightInInches * heightInInches)) * 703;

        String result;
        if (genderId == R.id.radioButtonMale) {
            result = getBMICategoryMale(bmi);
        } else {
            result = getBMICategoryFemale(bmi);
        }

        textViewResult.setText(String.format("BMI: %.2f\n%s", bmi, result));
    }

    private String getBMICategoryMale(float bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 24.9) {
            return "Normal Weight";
        } else if (bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private String getBMICategoryFemale(float bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 24.9) {
            return "Normal Weight";
        } else if (bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
}
