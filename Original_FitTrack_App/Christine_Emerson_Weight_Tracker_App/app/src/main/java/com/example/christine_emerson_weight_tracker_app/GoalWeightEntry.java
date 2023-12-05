package com.example.christine_emerson_weight_tracker_app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GoalWeightEntry {
    private String date;
    private double weight;

    public GoalWeightEntry(String date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Add this method inside your GoalWeightEntry class
    public double getGoalWeight() {
        return weight;
    }

    public String getFormattedDate() {
        // Format the date for display, e.g., "MM/dd/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            Date dateObj = new SimpleDateFormat("yyyy/MM/dd", Locale.US).parse(date);
            return dateFormat.format(dateObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
