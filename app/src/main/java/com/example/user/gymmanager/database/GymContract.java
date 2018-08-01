package com.example.user.gymmanager.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by User on 2018/4/4.
 */

public class GymContract {

    public static final String TABLE_NAME = "gym";

    public static final String CONTENT_AUTHORITY = "com.example.user.gymmanager";

    public static final Uri CONTENT_URI = new Uri.Builder()
            .scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_NAME)
            .build();

    public static abstract class GymEntry implements BaseColumns{
        public static final String TITLE = "title";
        public static final String TIME = "time";
        public static final String IS_ENABLED = "isEnabled";
    }
}
