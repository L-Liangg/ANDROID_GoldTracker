package com.liang.goldtracker;

import android.app.Application;

import com.liang.goldtracker.util.AlarmScheduler;

public class GoldTrackerApp extends Application {
    private static GoldTrackerApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AlarmScheduler.scheduleFromSaved(this);
    }

    public static GoldTrackerApp getInstance() {
        return instance;
    }
}
