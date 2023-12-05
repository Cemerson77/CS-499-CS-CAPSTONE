package com.example.christine_emerson_weight_tracker_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.christineemersonweighttrackingapp.R;

import java.util.List;
import java.util.Locale;

public class WeightEntryAdapter extends RecyclerView.Adapter<WeightEntryAdapter.ViewHolder> {
    private final List<WeightEntry> weightEntries;
    private final OnItemClickListener listener;

    public WeightEntryAdapter(List<WeightEntry> weightEntries, OnItemClickListener listener) {
        this.weightEntries = weightEntries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weight_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeightEntry weightEntry = weightEntries.get(position);
        holder.textViewDate.setText(weightEntry.getDate());

        // Display the weight and unit (kg or lb) next to it
        String unit = weightEntry.isKgUnit() ? "lb" : "kg";
        String weightText = String.format(Locale.US, "%.2f %s", weightEntry.getWeight(), unit);
        holder.textViewWeight.setText(weightText);
    }

    @Override
    public int getItemCount() {
        return weightEntries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewWeight;
        Button buttonEdit;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewWeight = itemView.findViewById(R.id.textViewWeight);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            // Set click listeners for edit and delete buttons
            buttonEdit.setOnClickListener(v -> {
                int position = getBindingAdapterPosition(); // Use getBindingAdapterPosition() instead
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(position);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getBindingAdapterPosition(); // Use getBindingAdapterPosition() instead
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position);
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
}
