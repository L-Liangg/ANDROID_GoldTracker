package com.liang.goldtracker.data.repository;

import androidx.lifecycle.LiveData;

import com.liang.goldtracker.data.db.AppDatabase;
import com.liang.goldtracker.data.db.HistoryEntity;

import java.util.List;

public class HistoryRepository {
    public static void insert(HistoryEntity item) {
        new Thread(() ->
                AppDatabase.getInstance().historyDao().insert(item)
        ).start();
    }

    public static LiveData<List<HistoryEntity>> getAll() {
        return AppDatabase.getInstance().historyDao().getAll();
    }

    public static void delete(HistoryEntity item) {
        new Thread(() ->
                AppDatabase.getInstance().historyDao().delete(item)
        ).start();
    }

    public static void deleteAll() {
        new Thread(() ->
                AppDatabase.getInstance().historyDao().deleteAll()
        ).start();
    }
}
