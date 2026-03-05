// C21361681 – Michael Traynor
// Room Entity for Venue profile

package com.fyp.giggy.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "venue_profiles",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"userId"}, unique = true)
        }
)
public class VenueProfile {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId; // FK → users.id

    @NonNull public String venueName;
    @NonNull public String location;    // e.g. "12 Main St, Dublin"
    @NonNull public String venueType;   // e.g. "Pub", "Late Bar", "Event Space"

    @Nullable public String description;
    @Nullable public String phoneNumber;
    @Nullable public String websiteUrl;
    @Nullable public String instagramUrl;
    @Nullable public String facebookUrl;

    public int capacity = 0;           // Approx. number of people

    // Average rating (updated when reviews are added)
    public float averageRating = 0.0f;
    public int reviewCount = 0;

    public VenueProfile(
            long userId,
            @NonNull String venueName,
            @NonNull String location,
            @NonNull String venueType
    ) {
        this.userId = userId;
        this.venueName = venueName;
        this.location = location;
        this.venueType = venueType;
    }
}