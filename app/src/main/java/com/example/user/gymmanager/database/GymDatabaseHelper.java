package com.example.user.gymmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 2018/4/4.
 */

public class GymDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gym.db";
    private static final int DATABASE_VERSION = 1;

    public GymDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_TABLE = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER)",
            GymContract.TABLE_NAME,
            GymContract.GymEntry._ID,
            GymContract.GymEntry.TITLE,
            GymContract.GymEntry.TIME,
            GymContract.GymEntry.IS_ENABLED
    );

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GymContract.TABLE_NAME);
        onCreate(db);
    }
}
