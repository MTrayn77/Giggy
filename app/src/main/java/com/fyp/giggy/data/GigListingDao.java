// C21361681 – Michael Traynor
// GigListingDao.java – DAO for gig listings
// Sprint 3: Gig Listings

package com.fyp.giggy.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GigListingDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(GigListing gig);

    @Update
    void update(GigListing gig);

    // All open gigs (for artists browsing)
    @Query("SELECT * FROM gig_listings WHERE status = 'open' ORDER BY createdAt DESC")
    List<GigListing> getAllOpenGigs();

    // Open gigs filtered by genre
    @Query("SELECT * FROM gig_listings WHERE status = 'open' AND genreWanted LIKE '%' || :genre || '%' ORDER BY createdAt DESC")
    List<GigListing> getOpenGigsByGenre(String genre);

    // Open gigs filtered by location
    @Query("SELECT * FROM gig_listings WHERE status = 'open' AND location LIKE '%' || :location || '%' ORDER BY createdAt DESC")
    List<GigListing> getOpenGigsByLocation(String location);

    // Open gigs filtered by both genre and location
    @Query("SELECT * FROM gig_listings WHERE status = 'open' AND genreWanted LIKE '%' || :genre || '%' AND location LIKE '%' || :location || '%' ORDER BY createdAt DESC")
    List<GigListing> getOpenGigsByGenreAndLocation(String genre, String location);

    // All gigs posted by a specific venue
    @Query("SELECT * FROM gig_listings WHERE venueUserId = :venueUserId ORDER BY createdAt DESC")
    List<GigListing> getGigsByVenue(long venueUserId);

    @Query("SELECT * FROM gig_listings WHERE id = :id LIMIT 1")
    GigListing getGigById(long id);

    @Query("UPDATE gig_listings SET status = :status WHERE id = :id")
    void updateStatus(long id, String status);
}