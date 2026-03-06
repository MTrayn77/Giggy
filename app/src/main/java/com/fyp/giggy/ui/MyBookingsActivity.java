// C21361681 – Michael Traynor
// MyBookingsActivity.java – Artist views all their booking applications and statuses
// Sprint 4: Booking System

package com.fyp.giggy.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.Booking;
import com.fyp.giggy.utils.SessionManager;

import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        SessionManager session = new SessionManager(this);
        Button btnBack = findViewById(R.id.btnBack);
        ListView listView = findViewById(R.id.listBookings);

        btnBack.setOnClickListener(v -> finish());

        new Thread(() -> {
            List<Booking> bookings = AppDatabase.getInstance(this)
                    .bookingDao().getBookingsForArtist(session.getUserId());

            String[] items = new String[bookings.size()];
            for (int i = 0; i < bookings.size(); i++) {
                Booking b = bookings.get(i);
                items[i] = b.venueName + " — " + b.gigDate + " at " + b.gigTime
                        + "\n" + statusLabel(b.status)
                        + (b.payAmount > 0 ? "  ·  €" + String.format("%.0f", b.payAmount) : "");
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_2,
                        android.R.id.text1, items);
                listView.setAdapter(adapter);

                if (bookings.isEmpty()) {
                    Toast.makeText(this, "No applications yet", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private String statusLabel(String status) {
        if (status == null) return "Unknown";
        switch (status) {
            case "pending":   return "⏳ Pending";
            case "confirmed": return "✅ Confirmed";
            case "declined":  return "❌ Declined";
            case "cancelled": return "🚫 Cancelled";
            default:          return status;
        }
    }
}