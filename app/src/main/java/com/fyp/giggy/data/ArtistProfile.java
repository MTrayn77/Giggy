// C21361681 – Michael Traynor
// Room Entity for Artist EPK-style profile

package com.fyp.giggy.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "artist_profiles",
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
public class ArtistProfile {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId; // FK → users.id

    @NonNull public String stageName;
    @NonNull public String genre;       // e.g. "Rock", "Jazz", "Traditional"
    @NonNull public String actType;     // e.g. "Solo", "Duo", "Band"
    @NonNull public String location;    // e.g. "Dublin", "Cork"

    @Nullable public String bio;
    @Nullable public String spotifyUrl;
    @Nullable public String instagramUrl;
    @Nullable public String facebookUrl;
    @Nullable public String youtubeUrl;
    @Nullable public String websiteUrl;

    // Average rating (updated when reviews are added)
    public float averageRating = 0.0f;
    public int reviewCount = 0;

    public ArtistProfile(
            long userId,
            @NonNull String stageName,
            @NonNull String genre,
            @NonNull String actType,
            @NonNull String location
    ) {
        this.userId = userId;
        this.stageName = stageName;
        this.genre = genre;
        this.actType = actType;
        this.location = location;
    }
}