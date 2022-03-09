package com.danlevy.todo.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.danlevy.todo.model.Tache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Tache.class}, version = 1, exportSchema = true)
public abstract class TodoRoomDatabase extends RoomDatabase {
    public static volatile TodoRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract TodoDao todoDao();

    public static TodoRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TodoRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TodoRoomDatabase.class, "todo")
                        .build();
            }
        }
        return INSTANCE;
    }
}
