package com.andruid.magic.makeitspeak.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AudioText.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "tts_db";
    private static HistoryDatabase database;

    public abstract HistoryDao historyDao();

    public static HistoryDatabase getInstance(Context context){
        if(database == null)
            database = Room.databaseBuilder(context.getApplicationContext(), HistoryDatabase.class,
                    DATABASE_NAME).build();
        return database;
    }
}