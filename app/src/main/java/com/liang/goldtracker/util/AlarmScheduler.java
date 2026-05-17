package com.liang.goldtracker.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.liang.goldtracker.GoldTrackerApp;
import com.liang.goldtracker.receiver.GoldPriceFetchReceiver;

import java.util.Calendar;

public class AlarmScheduler {

    private static final String PREF_NAME    = "alarm_prefs";
    private static final String PREF_HOUR    = "alarm_hour";
    private static final String PREF_MINUTE  = "alarm_minute";
    private static final int    DEFAULT_HOUR = 9;
    private static final int    DEFAULT_MIN  = 30;
    private static final int    REQUEST_CODE = 1001;

    public static void schedule(Context context, int hour, int minute) {
        // Lưu giờ
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(PREF_HOUR, hour)
                .putInt(PREF_MINUTE, minute)
                .apply();

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Check permission trên Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Mở Settings để user cấp permission
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                );
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(context, GoldPriceFetchReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Nếu giờ đã qua hôm nay thì sang ngày mai
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Log.d("AlarmScheduler", "Scheduled: " + new java.util.Date(calendar.getTimeInMillis()));
    }

    public static void scheduleFromSaved(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int hour   = prefs.getInt(PREF_HOUR, DEFAULT_HOUR);
        int minute = prefs.getInt(PREF_MINUTE, DEFAULT_MIN);
        schedule(context, hour, minute);
    }

    public static int getSavedHour(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(PREF_HOUR, DEFAULT_HOUR);
    }

    public static int getSavedMinute(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(PREF_MINUTE, DEFAULT_MIN);
    }

    public static void cancel(Context context) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, GoldPriceFetchReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
}
