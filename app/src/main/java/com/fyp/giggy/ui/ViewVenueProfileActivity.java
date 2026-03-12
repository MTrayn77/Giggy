// C21361681 – Michael Traynor
// ViewVenueProfileActivity.java – View a venue's profile
// Sprint 4 fix: viewUserId support | Sprint 5: Send Message button

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.Booking;
import com.fyp.giggy.data.VenueProfile;
import com.fyp.giggy.utils.SessionManager;

import java.util.List;

public class ViewVenueProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_venue_profile);

        SessionManager session = new SessionManager(this);
        Button btnEdit    = findViewById(R.id.btnEditProfile);
        Button btnBack    = findViewById(R.id.btnBack);
        Button btnMessage = findViewById(R.id.btnMessageProfile);

        btnBack.setOnClickListener(v -> finish());

        long viewUserId    = getIntent().getLongExtra("viewUserId", -1);
        long sessionUserId = session.getUserId();
        boolean isOwnProfile = (viewUserId == -1 || viewUserId == sessionUserId);
        long targetUserId    = isOwnProfile ? sessionUserId : viewUserId;

        if (btnEdit != null) {
            btnEdit.setVisibility(isOwnProfile ? View.VISIBLE : View.GONE);
            btnEdit.setOnClickListener(v ->
                    startActivity(new Intent(this, EditVenueProfileActivity.class)));
        }

        // Message button: only show when viewing someone else's profile
        if (btnMessage != null)
            btnMessage.setVisibility(isOwnProfile ? View.GONE : View.VISIBLE);

        if (btnMessage != null && !isOwnProfile) {
            final long otherUserId = targetUserId;
            btnMessage.setOnClickListener(v -> {
                new Thread(() -> {
                    // Find a booking shared between this artist and the venue
                    List<Booking> bookings = AppDatabase.getInstance(this)
                            .bookingDao().getBookingsForArtist(sessionUserId);

                    Booking shared = null;
                    for (Booking b : bookings) {
                        if (b.venueUserId == otherUserId) {
                            shared = b;
                            break;
                        }
                    }

                    final Booking booking = shared;
                    runOnUiThread(() -> {
                        if (booking == null) {
                            Toast.makeText(this,
                                    "No booking found with this venue. Apply to a gig first.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Intent i = new Intent(this, ConversationActivity.class);
                            i.putExtra("bookingId", booking.id);
                            startActivity(i);
                        }
                    });
                }).start();
            });
        }

        new Thread(() -> {
            VenueProfile profile = AppDatabase.getInstance(this)
                    .venueProfileDao().getProfileByUserId(targetUserId);
            runOnUiThread(() -> {
                if (profile == null) {
                    if (isOwnProfile) {
                        startActivity(new Intent(this, EditVenueProfileActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;
                }
                populateViews(profile);
            });
        }).start();
    }

    private void populateViews(VenueProfile p) {
        setText(R.id.tvVenueName,   p.venueName);
        setText(R.id.tvVenueType,   p.venueType);
        setText(R.id.tvLocation,    p.location);
        setText(R.id.tvDescription, p.description);
        setText(R.id.tvPhone,       p.phoneNumber);
        setText(R.id.tvWebsite,     p.websiteUrl);
        setText(R.id.tvInstagram,   p.instagramUrl);
        setText(R.id.tvFacebook,    p.facebookUrl);

        TextView tvCapacity = findViewById(R.id.tvCapacity);
        if (tvCapacity != null)
            tvCapacity.setText(p.capacity > 0 ? p.capacity + " people" : "—");

        TextView tvRating = findViewById(R.id.tvRating);
        if (tvRating != null)
            tvRating.setText(p.reviewCount > 0
                    ? String.format("%.1f ★  (%d reviews)", p.averageRating, p.reviewCount)
                    : "No reviews yet");

        setRowVisible(R.id.rowPhone,     p.phoneNumber);
        setRowVisible(R.id.rowWebsite,   p.websiteUrl);
        setRowVisible(R.id.rowInstagram, p.instagramUrl);
        setRowVisible(R.id.rowFacebook,  p.facebookUrl);
    }

    private void setText(int id, String value) {
        TextView tv = findViewById(id);
        if (tv != null) tv.setText(value != null && !value.isEmpty() ? value : "—");
    }

    private void setRowVisible(int rowId, String value) {
        View row = findViewById(rowId);
        if (row != null)
            row.setVisibility(value != null && !value.isEmpty() ? View.VISIBLE : View.GONE);
    }
}