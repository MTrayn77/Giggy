// C21361681 – Michael Traynor
// Message.java – Room entity for in-app messages between artist and venue
// Sprint 5: Messaging System

package com.fyp.giggy.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "messages",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "senderId",    onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "receiverId",  onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Booking.class, parentColumns = "id", childColumns = "bookingId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("senderId"),
                @Index("receiverId"),
                @Index("bookingId")
        }
)
public class Message {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long bookingId;   // conversation is tied to a booking
    public long senderId;    // FK → users.id
    public long receiverId;  // FK → users.id
    public String content;
    public long timestamp;
    public boolean isRead;
}
