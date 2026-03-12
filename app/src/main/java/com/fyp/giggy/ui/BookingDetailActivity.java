// C21361681 – Michael Traynor
// BookingDetailActivity.java – View a booking, confirm/decline, open conversation
// Sprint 4: Booking System | Sprint 5: Messaging + Notifications added

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.Booking;
import com.fyp.giggy.utils.NotificationHelper;
import com.fyp.giggy.utils.SessionManager;

public class BookingDetailActivity extends AppCompatActivity {

    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        long bookingId = getIntent().getLongExtra("bookingId", -1);
        if (bookingId == -1) { finish(); return; }

        SessionManager session = new SessionManager(this);

        Button btnBack    = findViewById(R.id.btnBack);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnDecline = findViewById(R.id.btnDecline);
        Button btnMessage = findViewById(R.id.btnMessage);

        btnBack.setOnClickListener(v -> finish());

        btnMessage.setOnClickListener(v -> {
            if (booking == null) return;
            Intent i = new Intent(this, ConversationActivity.class);
            i.putExtra("bookingId", booking.id);
            startActivity(i);
        });

        new Thread(() -> {
            booking = AppDatabase.getInstance(this).bookingDao().getBookingById(bookingId);
            runOnUiThread(() -> {
                if (booking == null) { finish(); return; }
                populateViews(booking);

                // Confirm/decline only for venue, only when pending
                boolean isVenue = session.getUserId() == booking.venueUserId;
                if (!isVenue || !"pending".equals(booking.status)) {
                    btnConfirm.setVisibility(View.GONE);
                    btnDecline.setVisibility(View.GONE);
                }
            });
        }).start();

        btnConfirm.setOnClickListener(v -> updateStatus("confirmed"));
        btnDecline.setOnClickListener(v -> updateStatus("declined"));
    }

    private void updateStatus(String newStatus) {
        if (booking == null) return;
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            db.bookingDao().updateStatus(booking.id, newStatus);

            if ("confirmed".equals(newStatus)) {
                db.gigListingDao().updateStatus(booking.gigId, "filled");
            }

            // Notify the artist of the status change
            NotificationHelper.sendBookingStatusNotification(
                    this, booking.artistUserId, newStatus, booking.gigDate);

            runOnUiThread(() -> {
                String msg = "confirmed".equals(newStatus) ? "Booking confirmed!" : "Booking declined.";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private void populateViews(Booking b) {
        setText(R.id.tvArtistName, b.artistName);
        setText(R.id.tvGigDate,    b.gigDate + " at " + b.gigTime);
        setText(R.id.tvLocation,   b.location);
        setText(R.id.tvPay,        b.payAmount > 0 ? "€" + String.format("%.2f", b.payAmount) : "Not specified");
        setText(R.id.tvStatus,     b.status);
        setText(R.id.tvMessage,    b.artistMessage != null && !b.artistMessage.isEmpty()
                ? b.artistMessage : "No message provided.");
    }

    private void setText(int id, String value) {
        TextView tv = findViewById(id);
        if (tv != null) tv.setText(value != null ? value : "—");
    }
}