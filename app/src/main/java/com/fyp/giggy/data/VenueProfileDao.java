// C21361681 – Michael Traynor
// DAO for VenueProfile queries

package com.fyp.giggy.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VenueProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProfile(VenueProfile profile);

    @Update
    void updateProfile(VenueProfile profile);

    // Get profile by the logged-in user's ID
    @Query("SELECT * FROM venue_profiles WHERE userId = :userId LIMIT 1")
    VenueProfile getProfileByUserId(long userId);

    // Get profile by its own ID
    @Query("SELECT * FROM venue_profiles WHERE id = :profileId LIMIT 1")
    VenueProfile getProfileById(long profileId);

    // Get all venue profiles
    @Query("SELECT * FROM venue_profiles")
    List<VenueProfile> getAllProfiles();

    // Search by venue type (e.g. "Pub", "Late Bar")
    @Query("SELECT * FROM venue_profiles WHERE venueType LIKE '%' || :venueType || '%'")
    List<VenueProfile> searchByVenueType(String venueType);

    // Search by location
    @Query("SELECT * FROM venue_profiles WHERE location LIKE '%' || :location || '%'")
    List<VenueProfile> searchByLocation(String location);

    // Delete profile for a user
    @Query("DELETE FROM venue_profiles WHERE userId = :userId")
    void deleteProfileByUserId(long userId);
}