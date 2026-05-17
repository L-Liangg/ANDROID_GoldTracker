package com.liang.goldtracker.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Insert
    void insert(HistoryEntity item);

    @Delete
    void delete(HistoryEntity item);

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    LiveData<List<HistoryEntity>> getAll();

    @Query("DELETE FROM history")
    void deleteAll();
}
