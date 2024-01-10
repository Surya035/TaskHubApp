package com.example.taskhubapp.localstorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TasksDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    public TasksDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "tasks" table
        db.execSQL(TasksContract.TaskEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database if needed
        db.execSQL(TasksContract.TaskEntry.DELETE_TABLE);
        onCreate(db);
    }
    public void deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = TasksContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(taskId)};
        db.delete(TasksContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }
}
