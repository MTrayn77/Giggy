// C21361681 – Michael Traynor
// VenueBookingsActivity.java – Venue views incoming booking requests and accepts/declines
// Sprint 4: Fixed infinite recreate() loop in onResume

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.Booking;
import com.fyp.giggy.utils.SessionManager;

import java.util.List;

public class VenueBookingsActivity extends AppCompatActivity {

    private List<Booking> bookings;
    private ListView listView;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_bookings);

        session = new SessionManager(this);
        listView = findViewById(R.id.listBookings);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (bookings != null && position < bookings.size()) {
                Booking b = bookings.get(position);
                Intent i = new Intent(this, BookingDetailActivity.class);
                i.putExtra("bookingId", b.id);
                startActivity(i);
            }
        });

        loadBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the list when returning from BookingDetailActivity
        // without using recreate() which caused an infinite loop
        loadBookings();
    }

    private void loadBookings() {
        new Thread(() -> {
            bookings = AppDatabase.getInstance(this)
                    .bookingDao().getBookingsForVenue(session.getUserId());

            String[] items = new String[bookings.size()];
            for (int i = 0; i < bookings.size(); i++) {
                Booking b = bookings.get(i);
                items[i] = b.artistName + " — " + b.gigDate + " at " + b.gigTime
                        + "\n" + statusLabel(b.status);
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
            case "pending":   return "⏳ Awaiting your response";
            case "confirmed": return "✅ Confirmed";
            case "declined":  return "❌ Declined";
            default:          return status;
        }
    }
}