package com.example.christine_emerson_weight_tracker_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDatabase.db"; // Database name
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE_NUMBER = "phoneNumber";

    private String databaseName;

    public DatabaseHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
        this.databaseName = dbName; // Initialize the database name
    }

    public long addUserPhoneNumber(String username, String phoneNumber) {
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        if (db != null) {
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_USERNAME, username);
                values.put(COLUMN_PHONE_NUMBER, phoneNumber); // Use the correct column name

                int rowsAffected = db.update(TABLE_NAME, values, COLUMN_USERNAME + " = ?", new String[]{username});

                if (rowsAffected > 0) {
                    result = rowsAffected;
                } else {
                    result = db.insert(TABLE_NAME, null, values);
                }

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }

        return result;
    }


    public String getUserPhoneNumber(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String phoneNumber = null;

        try {
            String[] columns = {COLUMN_PHONE_NUMBER};
            String selection = COLUMN_USERNAME + " = ?";
            String[] selectionArgs = {username};

            Cursor cursor = db.query(
                    TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int phoneNumberIndex = cursor.getColumnIndex(COLUMN_PHONE_NUMBER);
                phoneNumber = cursor.getString(phoneNumberIndex);
                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY," +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PHONE_NUMBER + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addUser(String username, String password) {
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        if (db != null) {
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_USERNAME, username);
                values.put(COLUMN_PASSWORD, password);

                result = db.insert(TABLE_NAME, null, values);
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }

        return result;
    }
    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {COLUMN_USERNAME};
            String selection = COLUMN_USERNAME + " = ?";
            String[] selectionArgs = {username};

            cursor = db.query(
                    TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            return cursor != null && cursor.getCount() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean isValidUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {COLUMN_USERNAME};
            String selection = COLUMN_USERNAME + " = ? AND " +
                    COLUMN_PASSWORD + " = ?";
            String[] selectionArgs = {username, password};

            cursor = db.query(
                    TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }
}
