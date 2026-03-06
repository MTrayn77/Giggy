// C21361681 – Michael Traynor
// GigDetailActivity.java – Full gig details with working Apply button
// Sprint 4: Apply button now creates a Booking record

package com.fyp.giggy.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.*;
import com.fyp.giggy.utils.SessionManager;

public class GigDetailActivity extends AppCompatActivity {

    private GigListing currentGig;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gig_detail);

        session = new SessionManager(this);
        long gigId = getIntent().getLongExtra("gigId", -1);

        Button btnBack  = findViewById(R.id.btnBack);
        Button btnApply = findViewById(R.id.btnApply);

        btnBack.setOnClickListener(v -> finish());
        btnApply.setOnClickListener(v -> applyForGig());

        if (gigId == -1) { finish(); return; }

        new Thread(() -> {
            currentGig = AppDatabase.getInstance(this).gigListingDao().getGigById(gigId);
            runOnUiThread(() -> {
                if (currentGig == null) { finish(); return; }
                populateViews(currentGig);

                // Hide apply button if gig is not open or user is a venue
                if (!"open".equals(currentGig.status) || "venue".equals(session.getRole())) {
                    btnApply.setVisibility(android.view.View.GONE);
                }
            });
        }).start();
    }

    private void applyForGig() {
        if (currentGig == null) return;

        long artistUserId = session.getUserId();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Check if already applied
            int already = db.bookingDao().hasApplied(currentGig.id, artistUserId);
            if (already > 0) {
                runOnUiThread(() -> Toast.makeText(this,
                        "You have already applied for this gig", Toast.LENGTH_SHORT).show());
                return;
            }

            // Get artist name from profile
            ArtistProfile profile = db.artistProfileDao().getProfileByUserId(artistUserId);
            String artistName = profile != null ? profile.stageName : session.getUsername();

            Booking booking = new Booking();
            booking.gigId         = currentGig.id;
            booking.artistUserId  = artistUserId;
            booking.venueUserId   = currentGig.venueUserId;
            booking.artistName    = artistName;
            booking.venueName     = currentGig.venueName;
            booking.gigDate       = currentGig.gigDate;
            booking.gigTime       = currentGig.gigTime;
            booking.location      = currentGig.location;
            booking.payAmount     = currentGig.payAmount;
            booking.status        = "pending";
            booking.createdAt     = System.currentTimeMillis();

            db.bookingDao().insert(booking);

            runOnUiThread(() -> {
                Toast.makeText(this, "Application sent!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private void populateViews(GigListing gig) {
        setText(R.id.tvVenueName,   gig.venueName);
        setText(R.id.tvLocation,    gig.location);
        setText(R.id.tvDate,        gig.gigDate + " at " + gig.gigTime);
        setText(R.id.tvDuration,    gig.duration != null && !gig.duration.isEmpty() ? gig.duration : "—");
        setText(R.id.tvGenre,       gig.genreWanted);
        setText(R.id.tvDescription, gig.description != null && !gig.description.isEmpty() ? gig.description : "No details provided.");
        setText(R.id.tvPay,         gig.payAmount > 0 ? "€" + String.format("%.2f", gig.payAmount) : "Pay not specified");
        setText(R.id.tvStatus,      gig.status);
    }

    private void setText(int id, String value) {
        TextView tv = findViewById(id);
        if (tv != null) tv.setText(value != null ? value : "—");
    }
}