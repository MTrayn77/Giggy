// C21361681 – Michael Traynor
// Booking.java – Room entity representing a booking request between an artist and a venue
// Sprint 4: Booking System

package com.fyp.giggy.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "bookings",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "artistUserId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "venueUserId",  onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = GigListing.class, parentColumns = "id", childColumns = "gigId",  onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("artistUserId"),
                @Index("venueUserId"),
                @Index("gigId")
        }
)
public class Booking {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long gigId;            // FK to GigListing
    public long artistUserId;     // FK to User (artist)
    public long venueUserId;      // FK to User (venue)

    // Denormalised for easy display
    public String artistName;
    public String venueName;
    public String gigDate;
    public String gigTime;
    public String location;
    public double payAmount;

    // "pending" | "confirmed" | "declined" | "cancelled"
    public String status;

    public String artistMessage;  // optional message from artist when applying
    public long createdAt;
}