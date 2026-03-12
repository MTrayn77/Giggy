// C21361681 – Michael Traynor
// MessageDao.java – DAO for messages
// Sprint 5: Messaging System

package com.fyp.giggy.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMessage(Message message);

    // Get all messages for a booking conversation, ordered oldest first
    @Query("SELECT * FROM messages WHERE bookingId = :bookingId ORDER BY timestamp ASC")
    List<Message> getMessagesByBookingId(long bookingId);

    // Count unread messages for a user across all conversations
    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0")
    int getUnreadCount(long userId);

    // Count unread messages for a specific booking
    @Query("SELECT COUNT(*) FROM messages WHERE bookingId = :bookingId AND receiverId = :userId AND isRead = 0")
    int getUnreadCountForBooking(long bookingId, long userId);

    // Mark all messages in a booking as read for a user
    @Query("UPDATE messages SET isRead = 1 WHERE bookingId = :bookingId AND receiverId = :userId")
    void markBookingMessagesRead(long bookingId, long userId);
}