package com.example.christine_emerson_weight_tracker_app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class WeightEntryDatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;  // Store the database instance

    private static final String DATABASE_NAME = "WeightEntries.db";

    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    private static final String TABLE_NAME = "WeightEntries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_IS_KG = "isKgUnit";

    // Constructor
    public WeightEntryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();  // Open the database connection
    }

    // onDestroy method in your activity to close the database connection
    public void onDestroy() {
        // Do not close the database connection here
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_WEIGHT + " REAL, " +
                COLUMN_IS_KG + " INTEGER);";

        db.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Add a new weight entry to the database
    public long addWeightEntry(WeightEntry weightEntry) {
        SQLiteDatabase db = this.getWritableDatabase(); // Open a writable database
        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, weightEntry.getDate());
        values.put(COLUMN_WEIGHT, weightEntry.getWeight());
        values.put(COLUMN_IS_KG, weightEntry.isKgUnit() ? 1 : 0);

        long result = db.insert(TABLE_NAME, null, values);

        return result;
    }

    // Update an existing weight entry in the database
    public int updateWeightEntry(WeightEntry updatedEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_WEIGHT, updatedEntry.getWeight());
        values.put(COLUMN_IS_KG, updatedEntry.isKgUnit() ? 1 : 0);

        int result = db.update(TABLE_NAME, values, COLUMN_DATE + " = ?", new String[]{updatedEntry.getDate()});

        return result;
    }

    // Delete a weight entry from the database using date
    public int deleteWeightEntry(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_DATE + " = ?", new String[]{date});

        return result;
    }

    // Check if an entry with the given date already exists in the database
    public boolean doesEntryExistForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});
        boolean entryExists = cursor.getCount() > 0;
        cursor.close();
        return entryExists;
    }

    // Get all weight entries from the database
    public List<WeightEntry> getAllWeightEntries() {
        List<WeightEntry> weightEntries = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                @SuppressLint("Range") double weight = cursor.getDouble(cursor.getColumnIndex(COLUMN_WEIGHT));
                @SuppressLint("Range") int isKgUnit = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_KG));

                boolean isKg = isKgUnit == 1;
                WeightEntry entry = new WeightEntry(date, weight, isKg);
                weightEntries.add(entry);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return weightEntries;
    }
}
