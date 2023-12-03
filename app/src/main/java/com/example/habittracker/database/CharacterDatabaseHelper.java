package com.example.habittracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CharacterDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "character_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "character_table";
    private static final String COLUMN_BODY = "body";
    private static final String COLUMN_HAIR = "hair";
    private static final String COLUMN_PANTS = "pants";

    public CharacterDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_BODY + " INTEGER, " +
                COLUMN_HAIR + " INTEGER, " +
                COLUMN_PANTS + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveCharacterData(int body, int hair, int pants) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BODY, body);
        values.put(COLUMN_HAIR, hair);
        values.put(COLUMN_PANTS, pants);

        try {
            db.insertOrThrow(TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.close();
    }

    public Cursor getSavedCharacterData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_BODY, COLUMN_HAIR, COLUMN_PANTS};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }
}
