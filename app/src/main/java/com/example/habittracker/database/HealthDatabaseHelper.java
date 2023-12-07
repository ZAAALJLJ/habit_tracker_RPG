package com.example.habittracker.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.habittracker.ui.daily.DailyModel;

import java.util.ArrayList;
import java.util.List;

public class HealthDatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    private static  final String DATABASE_NAME = "DAILY_DATABASE";
    private static  final String TABLE_NAME = "DAILY_TABLE";
    private static  final String COL_1 = "ID";
    private static  final String COL_2 = "TASK";
    private static  final String COL_3 = "STATUS";


    public HealthDatabaseHelper(@Nullable Context context ) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , TASK TEXT , STATUS INTEGER)");
        insertInitialTasks(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to insert initial tasks
    private void insertInitialTasks(SQLiteDatabase db) {
        String[] initialTasks = {"Drink 8 glasses of water", "Get 8 hours of sleep", "Meditate for 5 minutes"};

        ContentValues values = new ContentValues();

        for (String task : initialTasks) {
            values.put(COL_2, task);
            values.put(COL_3, 0);

            db.insert(TABLE_NAME, null, values);
        }
    }

    public void insertTask(DailyModel model){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2 , model.getTask());
        values.put(COL_3 , 0);
        db.insert(TABLE_NAME , null , values);
    }

    public void updateTask(int id , String task){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2 , task);
        db.update(TABLE_NAME , values , "ID=?" , new String[]{String.valueOf(id)});
    }

    public void updateStatus(int id , int status){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_3 , status);
        db.update(TABLE_NAME , values , "ID=?" , new String[]{String.valueOf(id)});
        Log.d("Status", "Status: " + status);
    }

    public void deleteTask(int id ){
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME , "ID=?" , new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public List<DailyModel> getAllTasks(){

        db = this.getWritableDatabase();
        Cursor cursor = null;
        List<DailyModel> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.query(TABLE_NAME , null , null , null , null , null , null);
            if (cursor !=null){
                if (cursor.moveToFirst()){
                    do {
                        DailyModel task = new DailyModel();
                        task.setId(cursor.getInt(cursor.getColumnIndex(COL_1)));
                        task.setTask(cursor.getString(cursor.getColumnIndex(COL_2)));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(COL_3)));
                        modelList.add(task);

                    }while (cursor.moveToNext());
                }
            }
        }finally {
            db.endTransaction();
            cursor.close();
        }
        return modelList;
    }

}
