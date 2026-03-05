// C21361681 – Michael Traynor
// Room DB

package com.fyp.giggy.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public static AppDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    AppDatabase.class,
                                    "giggy.db"
                            )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()  // For early development
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
