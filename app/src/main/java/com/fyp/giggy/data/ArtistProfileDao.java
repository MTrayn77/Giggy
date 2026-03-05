// C21361681 – Michael Traynor
// DAO for ArtistProfile queries

package com.fyp.giggy.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ArtistProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProfile(ArtistProfile profile);

    @Update
    void updateProfile(ArtistProfile profile);

    // Get profile by the logged-in user's ID
    @Query("SELECT * FROM artist_profiles WHERE userId = :userId LIMIT 1")
    ArtistProfile getProfileByUserId(long userId);

    // Get profile by its own ID (e.g. when viewing another artist)
    @Query("SELECT * FROM artist_profiles WHERE id = :profileId LIMIT 1")
    ArtistProfile getProfileById(long profileId);

    // Get all artist profiles – used for search/browse
    @Query("SELECT * FROM artist_profiles")
    List<ArtistProfile> getAllProfiles();

    // Search by genre (case-insensitive contains)
    @Query("SELECT * FROM artist_profiles WHERE genre LIKE '%' || :genre || '%'")
    List<ArtistProfile> searchByGenre(String genre);

    // Search by location (case-insensitive contains)
    @Query("SELECT * FROM artist_profiles WHERE location LIKE '%' || :location || '%'")
    List<ArtistProfile> searchByLocation(String location);

    // Search by genre AND location
    @Query("SELECT * FROM artist_profiles WHERE genre LIKE '%' || :genre || '%' AND location LIKE '%' || :location || '%'")
    List<ArtistProfile> searchByGenreAndLocation(String genre, String location);

    // Delete profile for a user (e.g. on account deletion)
    @Query("DELETE FROM artist_profiles WHERE userId = :userId")
    void deleteProfileByUserId(long userId);
}