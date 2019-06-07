package com.andruid.magic.makeitspeak.database;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM ttsHistory ORDER BY created DESC")
    DataSource.Factory<Integer, AudioText> getAllPaged();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecord(AudioText audioText);
}