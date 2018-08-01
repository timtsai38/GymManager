package com.example.user.gymmanager.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by User on 2018/4/4.
 */

public class GymProvider extends ContentProvider {

    private static final int GYM_TASKS = 100;
    private static final int GYM_TASK_WITH_ID = 101;

    private GymDatabaseHelper mGymDatabaseHelper;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(GymContract.CONTENT_AUTHORITY, GymContract.TABLE_NAME, GYM_TASKS);
        uriMatcher.addURI(GymContract.CONTENT_AUTHORITY, GymContract.TABLE_NAME + "/#", GYM_TASK_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mGymDatabaseHelper = new GymDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase readableDatabase = mGymDatabaseHelper.getReadableDatabase();
        int matchDirectory = URI_MATCHER.match(uri);
        Cursor returnCursor;

        if(matchDirectory == GYM_TASKS){
            returnCursor = readableDatabase.query(
                    GymContract.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
        }else {
            returnCursor = null;
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase writableDatabase = mGymDatabaseHelper.getWritableDatabase();
        int matchDirectory = URI_MATCHER.match(uri);
        Uri returnUri;

        if(matchDirectory == GYM_TASKS){
            long _id = writableDatabase.insert(GymContract.TABLE_NAME, null, values);
            if(_id > 0){
                returnUri = ContentUris.withAppendedId(GymContract.CONTENT_URI, _id);
            }else {
                returnUri = null;
            }
        }else {
            returnUri = null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase writableDatabase = mGymDatabaseHelper.getWritableDatabase();
        int matchItem = URI_MATCHER.match(uri);
        int gymTasksDeletedCounts;

        if(matchItem == GYM_TASK_WITH_ID){
            long _id = ContentUris.parseId(uri);
            selection = String.format("%s = ?", GymContract.GymEntry._ID);
            selectionArgs = new String[]{String.valueOf(_id)};
            gymTasksDeletedCounts = writableDatabase.delete(GymContract.TABLE_NAME, selection, selectionArgs);
        }else {
            gymTasksDeletedCounts = 0;
        }

        ContentResolver contentResolver = getContext().getContentResolver();

        if(contentResolver != null){
            contentResolver.notifyChange(uri, null);
        }

        return gymTasksDeletedCounts;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final  SQLiteDatabase writableDatabase = mGymDatabaseHelper.getWritableDatabase();
        int matchItem = URI_MATCHER.match(uri);
        int gymTasksUpdatedCounts;

        if(values == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        if(matchItem == GYM_TASK_WITH_ID){
            long _id = ContentUris.parseId(uri);
            selection = String.format("%s = ?", GymContract.GymEntry._ID);
            selectionArgs = new String[]{String.valueOf(_id)};

            gymTasksUpdatedCounts = writableDatabase.update(
                    GymContract.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }else{
            gymTasksUpdatedCounts = 0;
        }

        ContentResolver contentResolver = getContext().getContentResolver();

        if(contentResolver != null){
            contentResolver.notifyChange(uri, null);
        }

        return gymTasksUpdatedCounts;
    }
}
