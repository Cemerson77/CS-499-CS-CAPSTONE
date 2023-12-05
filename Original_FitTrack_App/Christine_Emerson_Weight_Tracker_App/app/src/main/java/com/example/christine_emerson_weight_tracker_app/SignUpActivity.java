package com.example.christine_emerson_weight_tracker_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.christineemersonweighttrackingapp.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText nameField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText phoneNumberField;
    private DatabaseHelper dbHelper;

    private static final int SMS_PERMISSION_CODE = 101;
    private static final int CUSTOM_PERMISSION_CODE = 103;

    private static final String CHANNEL_ID = "MyChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String PREF_KEY_FIRST_LOGIN = "first_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameField = findViewById(R.id.signup_name);
        usernameField = findViewById(R.id.signup_username);
        passwordField = findViewById(R.id.signup_password);
        phoneNumberField = findViewById(R.id.signup_phone_number);
        TextView signupRedirectText = findViewById(R.id.signupRedirectText);

        // Initialize DatabaseHelper for the sign-up database
        dbHelper = new DatabaseHelper(this, "UserDatabase.db");
        Toast.makeText(this, "DatabaseHelper initialized successfully", Toast.LENGTH_SHORT).show();

        signupRedirectText.setOnClickListener(v -> navigateToLoginScreen());


        Button signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(v -> {
            String name = nameField.getText().toString();
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            String phoneNumber = phoneNumberField.getText().toString(); // Get the phone number

            if (isInputValid(name, username, password, phoneNumber)) {
                signUpButton.setEnabled(false);

                // Check for SMS permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                }

                if (checkCustomPermission()) {
                    long result = dbHelper.addUser(username, password);

                    if (result != -1) {
                        sendNotification();
                        if (isFirstLogin()) {
                            requestPhoneNumber();
                        } else {
                            navigateToLoginScreen();
                        }
                    } else {
                        showError("Error creating user.");
                    }
                } else {
                    requestCustomPermission();
                }
                signUpButton.setEnabled(true);
            }
        });


    }

    private boolean isInputValid(String name, String username, String password, String phoneNumber) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumber)) {
            showError("All fields are required");
            return false;
        }

        if (!isValidName(name)) {
            showError("Invalid name format");
            return false;
        }

        if (!isValidUsername(username)) {
            showError("Invalid username format");
            return false;
        }

        // Additional validation for the phone number
        if (!isValidPhoneNumber(phoneNumber)) {
            showError("Invalid phone number format");
            return false;
        }

        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Implement your phone number validation logic here
        // For example, you can check if it contains only digits and has a specific length.
        // You can use regular expressions or other validation rules.

        // Example: Check if the phone number contains only digits and has a length of 10.
        return phoneNumber.matches("^[0-9]{10}$");
    }


    private boolean isValidName(String name) {
        return name.matches("^[a-zA-Z ]+$");
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    private void showError(String errorMessageText) {
        Toast.makeText(this, errorMessageText, Toast.LENGTH_SHORT).show();
    }

    private boolean checkCustomPermission() {
        return ContextCompat.checkSelfPermission(this, "com.example.christine_emerson_weight_tracker_app.permission.POST_NOTIFICATIONS")
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCustomPermission() {
        if (ContextCompat.checkSelfPermission(this, "com.example.christine_emerson_weight_tracker_app.permission.POST_NOTIFICATIONS")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"com.example.christine_emerson_weight_tracker_app.permission.POST_NOTIFICATIONS"},
                    CUSTOM_PERMISSION_CODE);
        } else {
            sendNotification();
            if (isFirstLogin()) {
                requestPhoneNumber();
            } else {
                navigateToLoginScreen();
            }
        }
    }

    private void sendNotification() {
        if (ContextCompat.checkSelfPermission(this, "com.example.christine_emerson_weight_tracker_app.permission.POST_NOTIFICATIONS")
                == PackageManager.PERMISSION_GRANTED) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_alert)
                    .setContentTitle("Welcome to FitTrack!")
                    .setContentText("Your account has been created successfully.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            showError("Custom notification permission is not granted. Please grant the permission.");
        }
    }

    private boolean isFirstLogin() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_KEY_FIRST_LOGIN, true);
    }

    private void saveUserPhoneNumber(String username, String phoneNumber) {
        long result = dbHelper.addUserPhoneNumber(username, phoneNumber);

        if (result != -1) {
            Toast.makeText(this, "Phone number saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            showError("Failed to save phone number.");
        }
    }

    // requestPhoneNumber method to call saveUserPhoneNumber
    private void requestPhoneNumber() {
        String username = usernameField.getText().toString(); // Get the username
        String phoneNumber = phoneNumberField.getText().toString(); // Get the phone number

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Phone Number");
        builder.setMessage("Please confirm your phone number so FitTrack can send you updates.");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(phoneNumber); // Pre-fill the phone number
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newPhoneNumber = input.getText().toString();
            Log.d("SignUpActivity", "Phone Number: " + newPhoneNumber);
            Log.d("SignUpActivity", "Saving Phone Number: " + newPhoneNumber + " for User: " + username);

            if (!TextUtils.isEmpty(newPhoneNumber)) {
                // Save the updated phone number to the database
                saveUserPhoneNumber(username, newPhoneNumber);

                // Send the welcome SMS with the updated phone number
                sendWelcomeSMS(newPhoneNumber);
            } else {
                // Save an empty phone number to the database
                saveUserPhoneNumber(username, "");

                // Navigate to the login screen
                navigateToLoginScreen();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> navigateToLoginScreen());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.BLACK);

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.RED);
    }


    private void sendWelcomeSMS(String phoneNumber) {
        String message = "Welcome to FitTrack! Your account has been created successfully.";
        sendSMS(phoneNumber, message);
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Welcome SMS sent", Toast.LENGTH_SHORT).show();
            navigateToLoginScreen(); // Navigate to the login screen if SMS is successfully sent.
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("SMS_ERROR", "Failed to send SMS: " + e.getMessage());
            showSmsSendingFailureDialog();
        }
    }


    private void showSmsSendingFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SMS Sending Failed");
        builder.setMessage("We were unable to send the SMS. Please check your phone number and try again.");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);

        builder.setPositiveButton("Try Again", (dialog, which) -> {
            String phoneNumber = input.getText().toString();
            if (!TextUtils.isEmpty(phoneNumber)) {
                sendWelcomeSMS(phoneNumber);
            } else {
                showSmsSendingFailureDialog(); // Reopen the SMS failure dialog for re-entry.
            }
        });

        builder.setNegativeButton("Skip", (dialog, which) -> navigateToLoginScreen());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.BLACK);

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.RED);
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}