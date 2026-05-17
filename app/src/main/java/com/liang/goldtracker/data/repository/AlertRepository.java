package com.liang.goldtracker.data.repository;

import androidx.lifecycle.LiveData;

import com.liang.goldtracker.data.db.AlertEntity;
import com.liang.goldtracker.data.db.AppDatabase;

import java.util.List;

public class AlertRepository {

    public static LiveData<List<AlertEntity>> getAll() {
        return AppDatabase.getInstance().alertDao().getAll();
    }

    public static List<AlertEntity> getAllEnabledSync() {
        return AppDatabase.getInstance().alertDao().getAllEnabledSync();
    }

    public static void insert(AlertEntity item) {
        new Thread(() ->
                AppDatabase.getInstance().alertDao().insert(item)
        ).start();
    }

    public static void delete(String goldKey) {
        new Thread(() ->
                AppDatabase.getInstance().alertDao().delete(goldKey)
        ).start();
    }
}
