package com.example.christine_emerson_weight_tracker_app;

public final class DatabaseContract {
    private DatabaseContract() {}

    public static class UserEntry {
        public static final String TABLE_NAME = "Users";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    }
}
