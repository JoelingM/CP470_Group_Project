package com.group.project;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;

import java.util.Calendar;

/**
 * A class to schedule notifications
 *
 * @author Aaron Exley
 */
public class NotificationScheduler {

    // Constants used
    private static int ID = 0;
    public static String CHANNEL_ID = "calender_channel_01";
    public static CharSequence name = "calender_channel_01";
    public static String description = "For reminders of events";

    /**
     * Schedules a notification in the future
     * @param ctx The context of the app
     * @param notification The notification to schedule
     * @param delay The delay before sending it in milliseconds
     * @param repeating is the event repeating every week
     * @return The id of the scheduled notification
     */
    public static int scheduleNotification(Context ctx, Notification notification, long delay, boolean repeating) {

        // Create an intent for the notification
        Intent notificationIntent = new Intent(ctx, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, ++ID);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        // Turns that intent into a pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Calculates the time for when the notification should go off
        long futureInMillis = Calendar.getInstance().getTimeInMillis() + delay;
        // Gets the alarm manager
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        if (repeating){
            // If repeating schedules the alarm to be repeating weekly
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis, AlarmManager.INTERVAL_DAY * 7 , pendingIntent);
        } else {
            // otherwise sets it normally
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
        }

        return ID;
    }

    /**
     * Creates a new notification
     * @param ctx The context of the app
     * @param content The text of the notification
     * @param title The title of the notification
     * @return The created notification
     */
    public static Notification getNotification(Context ctx, String content, String title) {
        Notification.Builder builder = new Notification.Builder(ctx, CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    /**
     * Cancels a scheduled notification
     * @param ctx The context of the app
     * @param id The id of the notification to cancel
     */
    public static void cancelNotification(Context ctx, int id) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(id);

    }

    /**
     * Creates a new notification channel for the app,
     * Will do nothing after its been called once
     * @param ctx The context of the app
     */
    public static void createChannel(Context ctx) {

        // Gets the manager
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Sets the contents of the channel
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.setShowBadge(true);

            // Adds the channel to the manager
            notificationManager.createNotificationChannel(channel);

        }

    }



}
