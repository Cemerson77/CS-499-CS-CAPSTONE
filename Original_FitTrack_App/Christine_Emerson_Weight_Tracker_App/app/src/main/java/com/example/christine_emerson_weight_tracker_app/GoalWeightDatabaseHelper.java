package com.example.christine_emerson_weight_tracker_app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class GoalWeightDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GoalWeightDatabase.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "GoalWeightEntries";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_WEIGHT = "weight";

    public GoalWeightDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_DATE + " TEXT PRIMARY KEY," +
                COLUMN_WEIGHT + " REAL)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addGoalWeightEntry(String date, double weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_WEIGHT, weight);
        return db.insert(TABLE_NAME, null, values);
    }

    // Add the deleteGoalWeightEntry method to your GoalWeightDatabaseHelper class
    public void deleteGoalWeightEntry(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("goal_weight_entries", "date = ?", new String[]{date});
        db.close();
    }

    public GoalWeightEntry getMostRecentGoalWeightEntry() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_DATE + " DESC", "1");
        GoalWeightEntry mostRecentEntry = null;
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            @SuppressLint("Range") double weight = cursor.getDouble(cursor.getColumnIndex(COLUMN_WEIGHT));
            mostRecentEntry = new GoalWeightEntry(date, weight);
            cursor.close();
        }
        return mostRecentEntry;
    }

    public List<GoalWeightEntry> getAllGoalWeightEntries() {
        List<GoalWeightEntry> entries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                @SuppressLint("Range") double weight = cursor.getDouble(cursor.getColumnIndex(COLUMN_WEIGHT));
                GoalWeightEntry entry = new GoalWeightEntry(date, weight);
                entries.add(entry);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return entries;
    }
}
