package com.example.christine_emerson_weight_tracker_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.christineemersonweighttrackingapp.R;

import java.util.List;

public class GoalWeightEntryAdapter extends BaseAdapter {
    private Context context;
    private List<GoalWeightEntry> goalWeightEntries;

    public GoalWeightEntryAdapter(Context context, List<GoalWeightEntry> goalWeightEntries) {
        this.context = context;
        this.goalWeightEntries = goalWeightEntries;
    }

    @Override
    public int getCount() {
        return goalWeightEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return goalWeightEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_goal_weight_entry, parent, false);
        }

        // Get the current goal weight entry
        GoalWeightEntry goalWeightEntry = goalWeightEntries.get(position);

        // Initialize TextViews in the item layout
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView weightTextView = convertView.findViewById(R.id.weightTextView);

        // Set the text for date and weight
        dateTextView.setText(goalWeightEntry.getFormattedDate());
        weightTextView.setText(String.valueOf(goalWeightEntry.getWeight()));

        return convertView;
    }
}
