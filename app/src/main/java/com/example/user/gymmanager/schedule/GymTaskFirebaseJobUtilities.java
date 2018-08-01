package com.example.user.gymmanager.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.user.gymmanager.R;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by User on 2018/4/6.
 */

public class GymTaskFirebaseJobUtilities {
    private static final int REMIND_INTERVAL_HOUR = 24;
    private static final int REMIND_INTERVAL_SECONDS = (int)TimeUnit.HOURS.toSeconds(REMIND_INTERVAL_HOUR);
    private static final int REMIND_FLEXTIME_SECONDS = 30;

    private static final String TAG_FIRST_GYM_REMINDER = "first_gym_reminder";
    private static final String TAG_GYM_REMINDER = "gym_reminder";


    public static void scheduleFirstGymReminder(@NonNull final Context context,
                                                int gymTaskId,
                                                int gymTaskTimeHour,
                                                int gymTaskTimeMinute){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);

        Calendar currentTime = Calendar.getInstance();
        Calendar gymTaskTime = Calendar.getInstance();
        gymTaskTime.set(Calendar.AM_PM,gymTaskTimeHour);
        gymTaskTime.set(Calendar.MINUTE,gymTaskTimeMinute);
        gymTaskTime.set(Calendar.SECOND, 0);

        final int afterDays = 1;
        if(currentTime.after(gymTaskTime)){
            gymTaskTime.add(Calendar.DATE, afterDays);
        }

        int firstRemindIntervalSeconds = (int)((gymTaskTime.getTimeInMillis() - currentTime.getTimeInMillis()) /1000);

        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(R.string.key_gym_task_id), gymTaskId);

        Job scheduleFirstGymReminderJob = jobDispatcher.newJobBuilder()
                .setService(GymTaskFirstFireJobService.class)
                .setTag(TAG_FIRST_GYM_REMINDER + gymTaskId)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(firstRemindIntervalSeconds, firstRemindIntervalSeconds + REMIND_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .setExtras(bundle)
                .build();

        jobDispatcher.schedule(scheduleFirstGymReminderJob);
    }

    public static void cancelScheduleFirstGymReminder(@NonNull final Context context, int gymTaskId){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);
        jobDispatcher.cancel(TAG_FIRST_GYM_REMINDER + gymTaskId);
    }

    public static void scheduleGymReminder(@NonNull final Context context, int gymTaskId){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);

        Job scheduleGymReminderJob = jobDispatcher.newJobBuilder()
                .setService(GymTaskFireJobSrevice.class)
                .setTag(TAG_GYM_REMINDER + gymTaskId)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(REMIND_INTERVAL_SECONDS, REMIND_INTERVAL_SECONDS + REMIND_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        jobDispatcher.schedule(scheduleGymReminderJob);
    }

    public static void cancelScheduleGymReminder(@NonNull final Context context, int gymTaskId){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);
        jobDispatcher.cancel(TAG_GYM_REMINDER + gymTaskId);
    }
}
