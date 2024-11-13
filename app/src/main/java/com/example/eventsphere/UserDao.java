package com.example.eventsphere;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {

    private SQLiteDatabase db;

    public UserDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Save the user (salt and hashed password)
    public void save(User user) {
        String sql = "INSERT INTO users (email, salt, hashed_password) VALUES (?, ?, ?)";
        db.execSQL(sql, new Object[]{user.getEmail(), user.getSalt(), user.getHashedPassword()});
    }

    // Find user by email
    public User findByEmail(String email) {
        User user = null;
        String sql = "SELECT email, salt, hashed_password FROM users WHERE email = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{email});
        if (cursor.moveToFirst()) {
            user = new User();
            user.setEmail(cursor.getString(0));
            user.setSalt(cursor.getString(1));
            user.setHashedPassword(cursor.getString(2));
        }
        cursor.close();
        return user;
    }
}
