// C21361681 – Michael Traynor
// Room Database – updated for Sprint 2 (Profile Management)
// VERSION 2: Added ArtistProfile and VenueProfile tables

package com.fyp.giggy.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                User.class,
                ArtistProfile.class,
                VenueProfile.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAOs
    public abstract UserDao userDao();
    public abstract ArtistProfileDao artistProfileDao();
    public abstract VenueProfileDao venueProfileDao();

    // Migration from version 1 (User only) → version 2 (+ profiles)
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create artist_profiles table
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `artist_profiles` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`userId` INTEGER NOT NULL, " +
                            "`stageName` TEXT NOT NULL, " +
                            "`genre` TEXT NOT NULL, " +
                            "`actType` TEXT NOT NULL, " +
                            "`location` TEXT NOT NULL, " +
                            "`bio` TEXT, " +
                            "`spotifyUrl` TEXT, " +
                            "`instagramUrl` TEXT, " +
                            "`facebookUrl` TEXT, " +
                            "`youtubeUrl` TEXT, " +
                            "`websiteUrl` TEXT, " +
                            "`averageRating` REAL NOT NULL DEFAULT 0.0, " +
                            "`reviewCount` INTEGER NOT NULL DEFAULT 0, " +
                            "FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON DELETE CASCADE)"
            );
            database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_artist_profiles_userId` " +
                            "ON `artist_profiles` (`userId`)"
            );

            // Create venue_profiles table
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `venue_profiles` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`userId` INTEGER NOT NULL, " +
                            "`venueName` TEXT NOT NULL, " +
                            "`location` TEXT NOT NULL, " +
                            "`venueType` TEXT NOT NULL, " +
                            "`description` TEXT, " +
                            "`phoneNumber` TEXT, " +
                            "`websiteUrl` TEXT, " +
                            "`instagramUrl` TEXT, " +
                            "`facebookUrl` TEXT, " +
                            "`capacity` INTEGER NOT NULL DEFAULT 0, " +
                            "`averageRating` REAL NOT NULL DEFAULT 0.0, " +
                            "`reviewCount` INTEGER NOT NULL DEFAULT 0, " +
                            "FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON DELETE CASCADE)"
            );
            database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_venue_profiles_userId` " +
                            "ON `venue_profiles` (`userId`)"
            );
        }
    };

    // Singleton accessor
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "giggy_db"
                            )
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}