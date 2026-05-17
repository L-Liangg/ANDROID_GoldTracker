package com.liang.goldtracker.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AlertEntity item);

    @Query("DELETE FROM alert WHERE goldKey = :goldKey")
    void delete(String goldKey);

    @Query("SELECT * FROM alert")
    LiveData<List<AlertEntity>> getAll();

    @Query("SELECT * FROM alert WHERE enabled = 1")
    List<AlertEntity> getAllEnabledSync();

    @Query("DELETE FROM alert")
    void deleteAll();
}
