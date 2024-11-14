package tn.esprit.eventsphere;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EventSphere.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_PROFILE_IMAGE = "profile_image";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_FIRST_NAME + " TEXT, "
            + COLUMN_LAST_NAME + " TEXT, "
            + COLUMN_EMAIL + " TEXT UNIQUE, "
            + COLUMN_PASSWORD + " TEXT, "
            + COLUMN_ROLE + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the initial table without profile_image column
        db.execSQL(CREATE_USER_TABLE);

        // Add profile_image column
        try {
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_PROFILE_IMAGE + " BLOB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2 && !hasProfileImageColumn(db)) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_PROFILE_IMAGE + " BLOB");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public byte[] getProfileImage(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] imageBytes = null;

        try {
            // Check if profile_image column exists
            if (!hasProfileImageColumn(db)) {
                return null;
            }

            Cursor cursor = db.query(
                    TABLE_USER,
                    new String[]{COLUMN_PROFILE_IMAGE},
                    COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE));
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return imageBytes;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        try {
            // First, check if profile_image column exists
            Cursor columnCursor = db.rawQuery("PRAGMA table_info(" + TABLE_USER + ")", null);
            boolean hasProfileImage = false;
            if (columnCursor != null) {
                while (columnCursor.moveToNext()) {
                    String columnName = columnCursor.getString(columnCursor.getColumnIndex("name"));
                    if (COLUMN_PROFILE_IMAGE.equals(columnName)) {
                        hasProfileImage = true;
                        break;
                    }
                }
                columnCursor.close();
            }

            // Build the query based on available columns
            String[] columns;
            if (hasProfileImage) {
                columns = new String[]{
                        COLUMN_USER_ID,
                        COLUMN_FIRST_NAME,
                        COLUMN_LAST_NAME,
                        COLUMN_EMAIL,
                        COLUMN_PASSWORD,
                        COLUMN_ROLE,
                        COLUMN_PROFILE_IMAGE
                };
            } else {
                columns = new String[]{
                        COLUMN_USER_ID,
                        COLUMN_FIRST_NAME,
                        COLUMN_LAST_NAME,
                        COLUMN_EMAIL,
                        COLUMN_PASSWORD,
                        COLUMN_ROLE
                };
            }

            String selection = COLUMN_EMAIL + "=?";
            String[] selectionArgs = {email};

            Cursor cursor = db.query(
                    TABLE_USER,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));

                if (hasProfileImage) {
                    try {
                        user.setProfileImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE)));
                    } catch (Exception e) {
                        // Handle case where profile image might be null
                        user.setProfileImage(null);
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return user;
    }




    public boolean insertUser(String firstName, String lastName, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_PASSWORD
        };

        String selection = COLUMN_EMAIL + "=?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                // Use BCrypt to verify the password
                return BCrypt.checkpw(password, storedPassword);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_USER,
                    COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});

            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_EMAIL};
        String selection = COLUMN_EMAIL + "=?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    private boolean hasProfileImageColumn(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_USER + ")", null);
        boolean hasColumn = false;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(cursor.getColumnIndex("name"));
                if (COLUMN_PROFILE_IMAGE.equals(columnName)) {
                    hasColumn = true;
                    break;
                }
            }
            cursor.close();
        }
        return hasColumn;
    }

    public boolean saveProfileImage(int userId, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;

        try {
            // Check if profile_image column exists
            if (!hasProfileImageColumn(db)) {
                // Add the column if it doesn't exist
                db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_PROFILE_IMAGE + " BLOB");
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_PROFILE_IMAGE, imageBytes);

            int rowsAffected = db.update(
                    TABLE_USER,
                    values,
                    COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );

            success = rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return success;
    }


}