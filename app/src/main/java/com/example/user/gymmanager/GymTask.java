package com.example.user.gymmanager;

/**
 * Created by User on 2018/4/4.
 */

public class GymTask {
    private int mId;
    private String mTitle;
    private String mTime;
    private boolean mIsEnabled;

    public int getId() {
        return mId;
    }

    public GymTask setId(int id) {
        this.mId = id;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public GymTask setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public String getTime() {
        return mTime;
    }

    public GymTask setTime(String time) {
        this.mTime = time;
        return this;
    }

    public boolean getIsEnabled() {
        return mIsEnabled;
    }

    public GymTask setIsEnabled(boolean isEnabled) {
        mIsEnabled = isEnabled;
        return this;
    }
}
