package com.liang.goldtracker.data.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.liang.goldtracker.GoldTrackerApp;

@Database(entities = {HistoryEntity.class, AlertEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract HistoryDao historyDao();
    public abstract AlertDao alertDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            GoldTrackerApp.getInstance().getApplicationContext(),
                            AppDatabase.class,
                            "gold_tracker_db"
                    ).fallbackToDestructiveMigration(true).build();
                }
            }
        }
        return INSTANCE;
    }
}