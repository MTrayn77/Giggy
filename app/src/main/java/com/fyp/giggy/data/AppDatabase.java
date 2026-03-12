// C21361681 – Michael Traynor
// AppDatabase.java – Room database, version 5
// Sprint 5: added messages table

package com.fyp.giggy.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {User.class, ArtistProfile.class, VenueProfile.class,
                GigListing.class, Booking.class, Message.class},
        version = 5,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao         userDao();
    public abstract ArtistProfileDao artistProfileDao();
    public abstract VenueProfileDao  venueProfileDao();
    public abstract GigListingDao    gigListingDao();
    public abstract BookingDao       bookingDao();
    public abstract MessageDao       messageDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "giggy_db"
                    )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build();
        }
        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS artist_profiles (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId INTEGER NOT NULL, stageName TEXT, genre TEXT, actType TEXT, location TEXT, bio TEXT, spotifyUrl TEXT, instagramUrl TEXT, facebookUrl TEXT, youtubeUrl TEXT, websiteUrl TEXT, averageRating REAL NOT NULL DEFAULT 0, reviewCount INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE)");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_artist_profiles_userId ON artist_profiles(userId)");
            db.execSQL("CREATE TABLE IF NOT EXISTS venue_profiles (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId INTEGER NOT NULL, venueName TEXT, location TEXT, venueType TEXT, description TEXT, phoneNumber TEXT, websiteUrl TEXT, instagramUrl TEXT, facebookUrl TEXT, capacity INTEGER NOT NULL DEFAULT 0, averageRating REAL NOT NULL DEFAULT 0, reviewCount INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE)");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_venue_profiles_userId ON venue_profiles(userId)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS gig_listings (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, venueUserId INTEGER NOT NULL, venueName TEXT, location TEXT, gigDate TEXT, gigTime TEXT, duration TEXT, genreWanted TEXT, description TEXT, payAmount REAL NOT NULL DEFAULT 0, status TEXT, createdAt INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(venueUserId) REFERENCES users(id) ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX IF NOT EXISTS index_gig_listings_venueUserId ON gig_listings(venueUserId)");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "gigId INTEGER NOT NULL," +
                    "artistUserId INTEGER NOT NULL," +
                    "venueUserId INTEGER NOT NULL," +
                    "artistName TEXT, venueName TEXT, gigDate TEXT, gigTime TEXT," +
                    "location TEXT, payAmount REAL NOT NULL DEFAULT 0," +
                    "status TEXT, artistMessage TEXT, createdAt INTEGER NOT NULL DEFAULT 0," +
                    "FOREIGN KEY(artistUserId) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(venueUserId) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(gigId) REFERENCES gig_listings(id) ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX IF NOT EXISTS index_bookings_artistUserId ON bookings(artistUserId)");
            db.execSQL("CREATE INDEX IF NOT EXISTS index_bookings_venueUserId ON bookings(venueUserId)");
            db.execSQL("CREATE INDEX IF NOT EXISTS index_bookings_gigId ON bookings(gigId)");
        }
    };

    // Sprint 5: messages table
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "bookingId INTEGER NOT NULL," +
                    "senderId INTEGER NOT NULL," +
                    "receiverId INTEGER NOT NULL," +
                    "content TEXT," +
                    "timestamp INTEGER NOT NULL DEFAULT 0," +
                    "isRead INTEGER NOT NULL DEFAULT 0," +
                    "FOREIGN KEY(senderId) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(receiverId) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(bookingId) REFERENCES bookings(id) ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX IF NOT EXISTS index_messages_bookingId ON messages(bookingId)");
            db.execSQL("CREATE INDEX IF NOT EXISTS index_messages_senderId ON messages(senderId)");
            db.execSQL("CREATE INDEX IF NOT EXISTS index_messages_receiverId ON messages(receiverId)");
        }
    };
}