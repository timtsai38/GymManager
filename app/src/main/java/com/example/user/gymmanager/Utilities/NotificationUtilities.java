package com.example.user.gymmanager.Utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.user.gymmanager.MainActivity;
import com.example.user.gymmanager.R;

/**
 * Created by User on 2018/4/6.
 */

public class NotificationUtilities {
    private static final int GYM_REMINDER_NOTIFICATION_ID = 5257;
    private static final int GYM_REMINDER_PENDINGINTENT_ID = 4788;

    public static void sendNotification(Context context){
        PendingIntent startActivityIntent = startActivity(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_move_black_24dp)
                .setContentTitle(context.getString(R.string.message_remind_title))
                .setContentText(context.getString(R.string.message_remind_text))
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(startActivityIntent)
                .setWhen(System.currentTimeMillis())
                .build();

        notificationManager.notify(GYM_REMINDER_NOTIFICATION_ID, notification);
    }



    private static PendingIntent startActivity(Context context){
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                GYM_REMINDER_PENDINGINTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
