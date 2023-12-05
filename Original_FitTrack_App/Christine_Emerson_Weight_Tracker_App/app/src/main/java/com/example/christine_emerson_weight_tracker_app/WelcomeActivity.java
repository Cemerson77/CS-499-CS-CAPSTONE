package com.example.christine_emerson_weight_tracker_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.christineemersonweighttrackingapp.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void navigateToBmiCalculator(View view) {
        // Navigate to the BmiCalculatorActivity
        Intent intent = new Intent(this, BmiCalculatorActivity.class);
        startActivity(intent);
    }

    public void navigateToGoalWeight(View view) {
        // Navigate to the EditGoalWeightActivity
        Intent intent = new Intent(this, EditGoalWeightActivity.class);
        startActivity(intent);
    }

    public void navigateToWeightEntry(View view) {
        // Navigate to the WeightRecordActivity
        Intent intent = new Intent(this, WeightRecordActivity.class);
        startActivity(intent);
    }
}
