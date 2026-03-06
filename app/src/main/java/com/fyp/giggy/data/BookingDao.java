// C21361681 – Michael Traynor
// BookingDao.java – DAO for bookings
// Sprint 4: Booking System

package com.fyp.giggy.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Booking booking);

    // Artist: all my booking requests (any status)
    @Query("SELECT * FROM bookings WHERE artistUserId = :artistUserId ORDER BY createdAt DESC")
    List<Booking> getBookingsForArtist(long artistUserId);

    // Venue: all incoming requests for a specific gig
    @Query("SELECT * FROM bookings WHERE gigId = :gigId ORDER BY createdAt DESC")
    List<Booking> getBookingsForGig(long gigId);

    // Venue: all incoming requests across all their gigs
    @Query("SELECT * FROM bookings WHERE venueUserId = :venueUserId ORDER BY createdAt DESC")
    List<Booking> getBookingsForVenue(long venueUserId);

    // Venue: pending requests only
    @Query("SELECT * FROM bookings WHERE venueUserId = :venueUserId AND status = 'pending' ORDER BY createdAt DESC")
    List<Booking> getPendingBookingsForVenue(long venueUserId);

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    Booking getBookingById(long id);

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    void updateStatus(long id, String status);

    // Check if artist already applied for this gig
    @Query("SELECT COUNT(*) FROM bookings WHERE gigId = :gigId AND artistUserId = :artistUserId")
    int hasApplied(long gigId, long artistUserId);
}