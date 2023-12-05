package com.example.christine_emerson_weight_tracker_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.christineemersonweighttrackingapp.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class WeightRecordActivity extends AppCompatActivity implements WeightEntryAdapter.OnItemClickListener {

    private List<WeightEntry> weightEntries;
    private WeightEntryAdapter adapter;
    private GraphView graphView;
    private double goalWeight = 0.0; // Initialize with a default value
    private boolean isKgUnit = true; // Default to kg

    private WeightEntryDatabaseHelper databaseHelper; // Database helper instance

    SharedPreferences sharedPreferences;

    private TextView noEntriesMessage; // Declare the TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_record);

        // Initialize the database helper
        databaseHelper = new WeightEntryDatabaseHelper(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Initialize the RecyclerView
        RecyclerView recyclerViewWeightEntries = findViewById(R.id.recyclerViewWeightEntries);
        recyclerViewWeightEntries.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list of weight entries (load from the database)
        weightEntries = databaseHelper.getAllWeightEntries();

        // Initialize the adapter and set the click listener
        adapter = new WeightEntryAdapter(weightEntries, this);
        recyclerViewWeightEntries.setAdapter(adapter);

        // Initialize the noEntriesMessage TextView
        noEntriesMessage = findViewById(R.id.noEntriesMessage); // Initialize it here

        // Check if there are no weight entries
        if (weightEntries.isEmpty()) {
            // If no weight entries, show the message
            noEntriesMessage.setVisibility(View.VISIBLE);
        } else {
            // If there are weight entries, hide the message
            noEntriesMessage.setVisibility(View.GONE);
        }
        // Initialize the GraphView
        graphView = findViewById(R.id.graph);
        graphView.setVisibility(View.GONE); // Initially hide the graph

        // Show the logout confirmation dialog when clicked
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Find the back button view
        ImageButton backButton = findViewById(R.id.backButton);

        // Set a click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the WelcomeActivity
                Intent intent = new Intent(WeightRecordActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

        // Initialize the goal_weight_button
        Button goalWeightButton = findViewById(R.id.goal_weight_button);
        goalWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the EditGoalWeightActivity
                Intent intent = new Intent(WeightRecordActivity.this, EditGoalWeightActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the goalWeight variable
        goalWeight = getGoalWeight();

        // Add Weight Button
        Button buttonAddWeight = findViewById(R.id.buttonAddWeight);
        buttonAddWeight.setOnClickListener(v -> showAddWeightDialog());

        // Swipe to Delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                // Delete the item from the database
                databaseHelper.deleteWeightEntry(weightEntries.get(position).getDate());

                // Remove the swiped item from the list
                weightEntries.remove(position);

                // Notify the adapter about the item removal
                adapter.notifyItemRemoved(position);

                updateGraph(); // Update the graph after deletion
            }


        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewWeightEntries);

        // Initialize the toggle switch
        SwitchMaterial toggleSwitch = findViewById(R.id.toggleSwitch);

        // Set an OnCheckedChangeListener to update the isKgUnit variable
        toggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isKgUnit = isChecked;
            // Update the unit for existing weight entries in the list
            if (!weightEntries.isEmpty()) {
                for (WeightEntry entry : weightEntries) {
                    double weight = entry.getWeight();
                    if (isKgUnit) {
                        // Convert to kg
                        weight = weight * 2.20462;
                    } else {
                        // Convert to lb
                        weight = weight / 2.20462;
                    }
                    entry.setWeight(weight, isKgUnit);
                }
                // Update the RecyclerView
                adapter.notifyDataSetChanged();
            }
            // Update the graph after changing the unit
            updateGraph();
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
            Intent intent = new Intent(WeightRecordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });

        cancelLogoutButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void showAddWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a vertical LinearLayout to hold the title and content
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create a TextView for the title
        TextView titleTextView = new TextView(this);
        titleTextView.setText(R.string.add_weight_entry);
        titleTextView.setTextSize(20); // Adjust the text size as needed
        titleTextView.setGravity(Gravity.CENTER); // Center the title
        titleTextView.setPadding(0, 20, 0, 20); // Add space under/above the title

        // Add the title TextView to the layout
        layout.addView(titleTextView);

        // Inflate the content view from XML
        View view = getLayoutInflater().inflate(R.layout.dialog_add_weight_entry, null);
        final TextView textViewDate = view.findViewById(R.id.textViewDate);
        final EditText editTextWeight = view.findViewById(R.id.editTextWeight);
        final DatePicker datePicker = view.findViewById(R.id.datePicker);

        // Add the content view to the layout
        layout.addView(view);

        // Set the custom layout as the dialog's view
        builder.setView(layout);


        builder.setPositiveButton("Add", (dialog, which) -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();
            String selectedDate = String.format(Locale.US, "%02d-%02d", month + 1, day);

            if (databaseHelper.doesEntryExistForDate(selectedDate)) {
                // An entry with the same date already exists
                Toast.makeText(WeightRecordActivity.this, "Weight entry for this date already exists.", Toast.LENGTH_SHORT).show();
            } else {
                String weightStr = editTextWeight.getText().toString().trim();

                if (!selectedDate.isEmpty() && isValidWeight(weightStr)) {
                    double weight = Double.parseDouble(weightStr);

                    // Display the selected unit (lb or kg) next to the weight in the list
                    String unit = isKgUnit ? "kg" : "lb";
                    String formattedWeight = String.format(Locale.US, "%.2f %s", weight, unit);

                    WeightEntry newEntry = new WeightEntry(selectedDate, weight, isKgUnit);

                    // Add the new entry to the list and update the RecyclerView
                    weightEntries.add(newEntry);
                    adapter.notifyItemInserted(weightEntries.size() - 1);

                    // Save the new entry to the database
                    long result = databaseHelper.addWeightEntry(newEntry);
                    if (result != -1) {
                        Toast.makeText(WeightRecordActivity.this, "Weight entry added successfully", Toast.LENGTH_SHORT).show();

                        // Hide the "noEntriesMessage" view
                        noEntriesMessage.setVisibility(View.GONE);

                        // Send SMS if goal weight is met
                        if (newEntry.getWeight() <= goalWeight) {
                            sendGoalWeightSMS(weight);
                        }
                    } else {
                        Toast.makeText(WeightRecordActivity.this, "Failed to add weight entry", Toast.LENGTH_SHORT).show();
                    }

                    // Update the graph
                    updateGraph();

                    dialog.dismiss();
                } else {
                    // Show a toast message if weight input is empty or invalid
                    Toast.makeText(WeightRecordActivity.this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Customize button text color
            positiveButton.setTextColor(Color.parseColor("#000000")); // Set text color to black
            negativeButton.setTextColor(Color.parseColor("#FF5733")); // Replace with your desired color

            // Center the buttons
            positiveButton.setGravity(Gravity.CENTER);
            negativeButton.setGravity(Gravity.CENTER);
        });

        alertDialog.show();
    }

    // Check if the weight input is valid
    private boolean isValidWeight(@NonNull String weightStr) {
        if (weightStr.isEmpty()) {
            return false; // Weight is empty, not valid
        }

        try {
            double weight = Double.parseDouble(weightStr);
            // Check if weight is within a valid range (for example, between 1 and 1000)
            return weight >= 1.0 && weight <= 1000.0; // Adjust the range as needed
        } catch (NumberFormatException e) {
            return false; // Weight is not a valid number
        }
    }

    private double getGoalWeight() {
        // Instantiate a GoalWeightDatabaseHelper
        GoalWeightDatabaseHelper goalWeightDatabaseHelper = new GoalWeightDatabaseHelper(this);

        // Retrieve the most recent goal weight entry
        GoalWeightEntry mostRecentEntry = goalWeightDatabaseHelper.getMostRecentGoalWeightEntry();

        if (mostRecentEntry != null) {
            double goalWeight = mostRecentEntry.getWeight();
            Log.d("SMS_DEBUG", "Goal weight: " + goalWeight);
            return goalWeight;
        } else {
            Log.d("SMS_DEBUG", "No goal weight entry found.");
            return 0.0;
        }
    }

    public String getUserPhoneNumber(String username) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String phoneNumber = null;

        try {
            String[] columns = {DatabaseContract.UserEntry.COLUMN_PHONE_NUMBER};
            String selection = DatabaseContract.UserEntry.COLUMN_USERNAME + " = ?";
            String[] selectionArgs = {username};

            Cursor cursor = db.query(
                    DatabaseContract.UserEntry.TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int phoneNumberIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_PHONE_NUMBER);
                phoneNumber = cursor.getString(phoneNumberIndex);
                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }

    private void sendGoalWeightSMS(double currentWeight) {
        // Check if the user has accepted SMS messages
        boolean acceptsSMS = sharedPreferences.getBoolean("acceptsSMS", false);

        if (acceptsSMS) {
            double goalWeight = getGoalWeight();

            if (currentWeight <= goalWeight) {
                String username = sharedPreferences.getString("username", ""); // Retrieve the actual username
                String phoneNumber = getUserPhoneNumber(username); // Retrieve the user's phone number using the actual username
                String message = "Congratulations! You've reached your goal weight.";

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);

                // Log confirmation
                Log.d("SMS_DEBUG", "Goal weight SMS sent successfully.");

                // You can also show a Toast or any other notification to inform the user
                Toast.makeText(this, "Goal weight reached! SMS sent.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String formatDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM dd", Locale.US);

        try {
            Date parsedDate = inputFormat.parse(date);
            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date; // Return the original date if parsing fails or parsedDate is null
    }

    // Implement this method to handle editing a weight entry
    @Override
    public void onEditClick(int position) {
        showEditWeightDialog(position);
    }

    // Implement this method to handle deleting a weight entry
    @Override
    public void onDeleteClick(int position) {
        if (position >= 0 && position < weightEntries.size()) {
            // Delete the item from the database
            int result = databaseHelper.deleteWeightEntry(weightEntries.get(position).getDate());

            if (result > 0) {
                // Remove the item from the list
                weightEntries.remove(position);

                // Notify the adapter that the item is removed
                adapter.notifyItemRemoved(position);

                // Notify the adapter of item range change for positions after the deleted item
                adapter.notifyItemRangeChanged(position, weightEntries.size() - position);

                // Update the graph after deletion
                updateGraph();
            } else {
                // Handle the case where the deletion from the database was not successful
                Toast.makeText(this, "Failed to delete weight entry", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showEditWeightDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Weight Entry");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_weight_entry, null);
        final TextView textViewDate = view.findViewById(R.id.textViewDate);
        final EditText editTextWeight = view.findViewById(R.id.editTextWeight);

        // Populate the EditText fields with the current data
        WeightEntry entry = weightEntries.get(position);
        textViewDate.setText(entry.getDate());
        editTextWeight.setText(String.valueOf(entry.getWeight()));

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String date = textViewDate.getText().toString().trim();
            String weightStr = editTextWeight.getText().toString().trim();

            if (!date.isEmpty() && !weightStr.isEmpty()) {
                double weight = Double.parseDouble(weightStr);

                // Convert the weight value to kg or lb based on the selected unit
                if (!isKgUnit) {
                    // Convert to kg
                    weight = weight / 2.20462;
                }

                // Create a new WeightEntry with the updated unit
                WeightEntry updatedEntry = new WeightEntry(date, weight, isKgUnit);

                // Update the existing entry in the list and RecyclerView
                weightEntries.set(position, updatedEntry);
                adapter.notifyItemChanged(position);

                // Update the entry in the database
                databaseHelper.updateWeightEntry(updatedEntry);

                dialog.dismiss();

                updateGraph(); // Update the graph after editing
            } else {
                Toast.makeText(WeightRecordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Apply custom button text color and centering
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Customize button text color
            positiveButton.setTextColor(Color.parseColor("#000000")); // Replace with your desired color
            negativeButton.setTextColor(Color.parseColor("#FF5733")); // Replace with your desired color

            // Center the buttons
            positiveButton.setGravity(android.view.Gravity.CENTER);
            negativeButton.setGravity(android.view.Gravity.CENTER);
        });

        alertDialog.show();
    }

    // Method to update the graph when a new weight entry is added or the unit is changed
// Modify the updateGraph method to display a Line Graph with date labels
    private void updateGraph() {
        if (weightEntries.size() >= 2) {
            // Convert weight data to DataPoint
            DataPoint[] dataPoints = new DataPoint[weightEntries.size()];
            for (int i = 0; i < weightEntries.size(); i++) {
                WeightEntry entry = weightEntries.get(i);
                double weight = entry.getWeight();
                // In a LineGraph, the x-coordinate is the position in the list and the y-coordinate is the weight
                dataPoints[i] = new DataPoint(i, weight);
            }

            // Create a LineGraphSeries to display the line graph
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

            // Clear existing series and add the new line series
            graphView.removeAllSeries();
            graphView.addSeries(series);

            // Set custom labels for the X and Y axes
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);

            // Create an array of date labels
            String[] dateLabels = new String[weightEntries.size()];
            for (int i = 0; i < weightEntries.size(); i++) {
                dateLabels[i] = formatDate(weightEntries.get(i).getDate()); // Assuming formatDate is defined
            }
            staticLabelsFormatter.setHorizontalLabels(dateLabels);

            staticLabelsFormatter.setVerticalLabels(new String[]{"0", "50", "100", "150", "200", "250", "300"});
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

            // Customize the X-axis title
            graphView.getGridLabelRenderer().setHorizontalAxisTitle("Date");

            // Customize the Y-axis title
            if (!isKgUnit) {
                graphView.getGridLabelRenderer().setVerticalAxisTitle("Weight (KG)");
            } else {
                graphView.getGridLabelRenderer().setVerticalAxisTitle("Weight (LB)");
            }

            // Set the graph's visibility to VISIBLE
            graphView.setVisibility(View.VISIBLE);
        } else {
            // If there are fewer than two entries, hide the graph
            graphView.setVisibility(View.GONE);

            // Handle the case when there are not enough entries to update the graph
            Toast.makeText(this, "Add at least two weight entries to update the graph.", Toast.LENGTH_SHORT).show();
        }
    }
}