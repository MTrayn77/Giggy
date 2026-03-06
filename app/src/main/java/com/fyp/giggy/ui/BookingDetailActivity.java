// C21361681 – Michael Traynor
// BookingDetailActivity.java – Venue views a single booking request and confirms or declines
// Sprint 4: Booking System

package com.fyp.giggy.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.Booking;
import com.fyp.giggy.utils.SessionManager;

public class BookingDetailActivity extends AppCompatActivity {

    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        long bookingId = getIntent().getLongExtra("bookingId", -1);
        if (bookingId == -1) { finish(); return; }

        Button btnBack    = findViewById(R.id.btnBack);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnDecline = findViewById(R.id.btnDecline);

        btnBack.setOnClickListener(v -> finish());

        new Thread(() -> {
            booking = AppDatabase.getInstance(this).bookingDao().getBookingById(bookingId);
            runOnUiThread(() -> {
                if (booking == null) { finish(); return; }
                populateViews(booking);

                // Only show confirm/decline buttons if still pending
                if (!"pending".equals(booking.status)) {
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

            // If confirmed, mark the gig as filled
            if ("confirmed".equals(newStatus)) {
                db.gigListingDao().updateStatus(booking.gigId, "filled");
            }

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