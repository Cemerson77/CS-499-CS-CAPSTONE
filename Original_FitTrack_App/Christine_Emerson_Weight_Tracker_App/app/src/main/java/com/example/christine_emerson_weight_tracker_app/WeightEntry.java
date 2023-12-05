package com.example.christine_emerson_weight_tracker_app;

public class WeightEntry {
    private String date;
    private double weight;
    private boolean isKgUnit; // Added for unit information

    public WeightEntry(String date, double weight, boolean isKgUnit) {
        this.date = date;
        this.weight = weight;
        this.isKgUnit = isKgUnit;
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

    public void setWeight(double weight, boolean isKgUnit) {
        this.weight = weight;
        this.isKgUnit = isKgUnit;
    }

    public boolean isKgUnit() {
        return isKgUnit;
    }

    public void setIsKgUnit(boolean isKgUnit) {
        this.isKgUnit = isKgUnit;
    }
}
