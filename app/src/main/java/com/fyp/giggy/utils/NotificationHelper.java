// C21361681 – Michael Traynor
// NotificationHelper.java – Local notifications for messages and booking updates
// Sprint 5: Notifications (FR9)

package com.fyp.giggy.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fyp.giggy.R;

public class NotificationHelper {

    private static final String CHANNEL_MESSAGES = "giggy_messages";
    private static final String CHANNEL_BOOKINGS  = "giggy_bookings";

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);

            NotificationChannel msgChannel = new NotificationChannel(
                    CHANNEL_MESSAGES, "Messages", NotificationManager.IMPORTANCE_HIGH);
            msgChannel.setDescription("New messages from artists and venues");

            NotificationChannel bookingChannel = new NotificationChannel(
                    CHANNEL_BOOKINGS, "Bookings", NotificationManager.IMPORTANCE_HIGH);
            bookingChannel.setDescription("Booking requests and updates");

            nm.createNotificationChannel(msgChannel);
            nm.createNotificationChannel(bookingChannel);
        }
    }

    // Called when a new message is sent — notifies the receiver
    public static void sendMessageNotification(Context context, long receiverUserId, String messagePreview) {
        createChannels(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MESSAGES)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Message")
                .setContentText(messagePreview.length() > 50
                        ? messagePreview.substring(0, 50) + "…"
                        : messagePreview)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify((int) (receiverUserId * 1000 + System.currentTimeMillis() % 1000), builder.build());
    }

    // Called when a booking status changes (confirmed / declined)
    public static void sendBookingStatusNotification(Context context, long receiverUserId,
                                                     String status, String gigDate) {
        createChannels(context);

        String title = "confirmed".equals(status) ? "Booking Confirmed! 🎉" : "Booking Update";
        String body  = "confirmed".equals(status)
                ? "Your booking for " + gigDate + " has been confirmed."
                : "Your booking for " + gigDate + " was declined.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_BOOKINGS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify((int) (receiverUserId * 100 + System.currentTimeMillis() % 100), builder.build());
    }

    // Called when a new booking request is received by a venue
    public static void sendNewBookingNotification(Context context, long venueUserId,
                                                  String artistName, String gigDate) {
        createChannels(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_BOOKINGS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Booking Request")
                .setContentText(artistName + " wants to play on " + gigDate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify((int) (venueUserId * 100 + System.currentTimeMillis() % 100), builder.build());
    }
}