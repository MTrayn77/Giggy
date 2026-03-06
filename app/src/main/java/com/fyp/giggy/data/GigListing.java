// C21361681 – Michael Traynor
// GigListing.java – Room entity representing a gig posted by a venue
// Sprint 3: Gig Listings

package com.fyp.giggy.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "gig_listings",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "venueUserId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("venueUserId")}
)
public class GigListing {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long venueUserId;      // FK to User (venue)
    public String venueName;      // denormalised for easy display
    public String location;
    public String gigDate;        // stored as "dd/MM/yyyy"
    public String gigTime;        // stored as "HH:mm"
    public String duration;       // e.g. "2 hours"
    public String genreWanted;    // genre the venue is looking for
    public String description;    // additional details
    public double payAmount;      // in EUR
    public String status;         // "open" | "filled" | "cancelled"
    public long createdAt;        // System.currentTimeMillis()
}