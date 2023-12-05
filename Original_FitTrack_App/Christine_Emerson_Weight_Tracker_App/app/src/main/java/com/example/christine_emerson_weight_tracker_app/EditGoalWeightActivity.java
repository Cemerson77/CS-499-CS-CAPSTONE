package com.example.christine_emerson_weight_tracker_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.christineemersonweighttrackingapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditGoalWeightActivity extends AppCompatActivity {

    private EditText editGoalWeightEditText;
    private DatePicker datePicker;
    private List<GoalWeightEntry> goalWeightEntries;
    private GoalWeightEntryAdapter goalWeightAdapter;
    private GoalWeightDatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    private TextView mostRecentGoalWeightTextView; // TextView to display the most recent goal weight

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goal_weight);

        dbHelper = new GoalWeightDatabaseHelper(this);

        //Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);


        // Initialize views
        editGoalWeightEditText = findViewById(R.id.editGoalWeight);
        datePicker = findViewById(R.id.datePicker);
        GridView weightGoalsGridView = findViewById(R.id.weight_goals);

        // Initialize mostRecentGoalWeightTextView
        mostRecentGoalWeightTextView = findViewById(R.id.mostRecentGoalWeightTextView);

        // Initialize "Add" button
        ImageView addGoalWeightButton = findViewById(R.id.addGoalWeight);
        addGoalWeightButton.setOnClickListener(v -> changeGoalWeight(true));

        // Initialize "Minus" button
        ImageView minusGoalWeightButton = findViewById(R.id.minusGoalWeight);
        minusGoalWeightButton.setOnClickListener(v -> changeGoalWeight(false));

        // Initialize "Save Goal Weight" button
        Button saveGoalWeightButton = findViewById(R.id.saveGoalWeightButton);
        saveGoalWeightButton.setOnClickListener(v -> saveGoalWeight());

        // Load the most recent goal weight from the database
        loadMostRecentGoalWeight();

        // Initialize the list of goal weight entries from the database
        goalWeightEntries = dbHelper.getAllGoalWeightEntries();

        // Initialize the adapter for the GridView
        goalWeightAdapter = new GoalWeightEntryAdapter(this, goalWeightEntries);
        weightGoalsGridView.setAdapter(goalWeightAdapter);

        //show the logout confirmation dialog when clicked
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Find the home button
        ImageButton homeButton = findViewById(R.id.homeButton);

        // Set a click listener for the home button
        homeButton.setOnClickListener(v -> {
            // Create an intent to navigate to WeightRecordActivity
            Intent intent = new Intent(EditGoalWeightActivity.this, WelcomeActivity.class);

            // Start the WeightRecordActivity
            startActivity(intent);

            // Finish the current activity (optional)
            finish();
        });

    }

    private void updateMostRecentGoalWeight() {
        // Retrieve the most recent goal weight from the database
        GoalWeightEntry mostRecentGoalWeightEntry = dbHelper.getMostRecentGoalWeightEntry();

        if (mostRecentGoalWeightEntry != null) {
            // Format and display the most recent goal weight as an integer (no decimals)
            int goalWeight = (int) mostRecentGoalWeightEntry.getGoalWeight();
            String formattedGoalWeight = goalWeight + " lbs";
            mostRecentGoalWeightTextView.setText(formattedGoalWeight);
            mostRecentGoalWeightTextView.setTextSize(24); // Adjust text size as needed
            mostRecentGoalWeightTextView.setGravity(Gravity.CENTER); // Center the text
        } else {
            // No goal weight entry found, display a message
            mostRecentGoalWeightTextView.setText(" ");
        }
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
            Intent intent = new Intent(EditGoalWeightActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });

        cancelLogoutButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void changeGoalWeight(boolean isAdd) {
        String goalWeightStr = editGoalWeightEditText.getText().toString().trim();
        if (!goalWeightStr.isEmpty()) {
            double currentWeight = Double.parseDouble(goalWeightStr);
            double newGoalWeight = isAdd ? currentWeight + 1.0 : currentWeight - 1.0;

            // Update the goal weight EditText with the new value
            editGoalWeightEditText.setText(String.valueOf(newGoalWeight));
        } else {
            Toast.makeText(this, "Please enter a goal weight first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveGoalWeight() {
        // Get the goal weight and date from the input fields
        String goalWeightStr = editGoalWeightEditText.getText().toString().trim();
        if (!goalWeightStr.isEmpty()) {
            // Get the selected date from the DatePicker
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();

            // Create a Calendar instance and set the selected date
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            // Get the date in the desired format (e.g., "MM-dd-yyyy")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            String formattedDate = dateFormat.format(calendar.getTime());

            // Parse the goal weight as a double
            double goalWeight = Double.parseDouble(goalWeightStr);


            // Check if a goal weight entry already exists for the same date
            boolean goalWeightExists = false;
            for (GoalWeightEntry entry : goalWeightEntries) {
                if (entry.getDate().equals(formattedDate)) {
                    goalWeightExists = true;
                    break;
                }
            }

            if (goalWeightExists) {
                Toast.makeText(this, "An entry for this date already exists.", Toast.LENGTH_SHORT).show();
            } else {
                // No matching entry exists, so add the new goal weight entry
                GoalWeightEntry goalWeightEntry = new GoalWeightEntry(formattedDate, goalWeight);
                goalWeightEntries.add(goalWeightEntry);

                // Save the goal weight entry to the database
                dbHelper.addGoalWeightEntry(formattedDate, goalWeight);

                // Update the most recent goal weight
                loadMostRecentGoalWeight();

                // Notify the adapter that the data has changed
                goalWeightAdapter.notifyDataSetChanged();

                // Clear the EditText and DatePicker for the next entry
                editGoalWeightEditText.setText("");
                clearDatePicker();

                // Show a message indicating that the goal weight is saved
                Toast.makeText(this, "Goal weight saved", Toast.LENGTH_SHORT).show();

                // Close the keyboard
                closeKeyboard();

            }
        } else {
            Toast.makeText(this, "Please enter a goal weight first.", Toast.LENGTH_SHORT).show();
        }
        // Notify the adapter that the data has changed
        goalWeightAdapter.notifyDataSetChanged();
        // Clear the EditText and DatePicker for the next entry
        editGoalWeightEditText.setText("");
        clearDatePicker();

        // Show a message indicating that the goal weight is saved
        Toast.makeText(this, "Goal weight saved", Toast.LENGTH_SHORT).show();

        // Update the most recent goal weight
        updateMostRecentGoalWeight();

        // Close the keyboard
        closeKeyboard();
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void clearDatePicker() {
        // Set the DatePicker to the current date
        Calendar calendar = Calendar.getInstance();
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void loadMostRecentGoalWeight() {
        // Retrieve the most recent goal weight from the database
        GoalWeightEntry mostRecentGoalWeightEntry = dbHelper.getMostRecentGoalWeightEntry();

        if (mostRecentGoalWeightEntry != null) {
            // Format and display the most recent goal weight as an integer (no decimals)
            int goalWeight = (int) mostRecentGoalWeightEntry.getGoalWeight();
            String formattedGoalWeight = goalWeight + " lbs";
            mostRecentGoalWeightTextView.setText(formattedGoalWeight);
            mostRecentGoalWeightTextView.setTextSize(24); // Adjust text size as needed
            mostRecentGoalWeightTextView.setGravity(Gravity.CENTER); // Center the text

        } else {
            // No goal weight entry found, display a message
            mostRecentGoalWeightTextView.setText(" ");
        }
    }

}
