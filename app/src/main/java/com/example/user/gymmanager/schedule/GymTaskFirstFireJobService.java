package com.example.user.gymmanager.schedule;

import android.content.Context;
import android.os.AsyncTask;

import com.example.user.gymmanager.R;
import com.example.user.gymmanager.Utilities.NotificationUtilities;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by User on 2018/4/6.
 */

public class GymTaskFirstFireJobService extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Context context = getApplicationContext();

                int gymTaskId = -1;
                if(jobParameters.getExtras() != null){
                    gymTaskId = jobParameters.getExtras().getInt(getString(R.string.key_gym_task_id), -1);
                }

                if(gymTaskId != -1){
                    NotificationUtilities.sendNotification(context);
                    GymTaskFirebaseJobUtilities.scheduleGymReminder(context, gymTaskId);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(jobParameters, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if(mBackgroundTask != null){
            mBackgroundTask.cancel(true);
        }
        return true;
    }
}
