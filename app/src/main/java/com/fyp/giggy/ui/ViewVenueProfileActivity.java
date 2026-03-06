// C21361681 – Michael Traynor
// Displays the logged-in venue's saved profile
// Sprint 2: View Profile screen

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.VenueProfile;
import com.fyp.giggy.utils.SessionManager;

public class ViewVenueProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_venue_profile);

        SessionManager session = new SessionManager(this);
        long userId = session.getUserId();

        Button btnEdit = findViewById(R.id.btnEditProfile);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, EditVenueProfileActivity.class))
        );

        new Thread(() -> {
            VenueProfile profile = AppDatabase.getInstance(this)
                    .venueProfileDao()
                    .getProfileByUserId(userId);

            runOnUiThread(() -> {
                if (profile == null) {
                    startActivity(new Intent(this, EditVenueProfileActivity.class));
                    finish();
                } else {
                    populateViews(profile);
                }
            });
        }).start();
    }

    private void populateViews(VenueProfile p) {
        setText(R.id.tvVenueName,    p.venueName);
        setText(R.id.tvVenueType,    p.venueType);
        setText(R.id.tvLocation,     p.location);
        setText(R.id.tvDescription,  p.description);
        setText(R.id.tvPhone,        p.phoneNumber);
        setText(R.id.tvWebsite,      p.websiteUrl);
        setText(R.id.tvInstagram,    p.instagramUrl);
        setText(R.id.tvFacebook,     p.facebookUrl);

        TextView tvCapacity = findViewById(R.id.tvCapacity);
        if (tvCapacity != null) {
            tvCapacity.setText(p.capacity > 0 ? p.capacity + " people" : "—");
        }

        TextView tvRating = findViewById(R.id.tvRating);
        if (p.reviewCount > 0) {
            tvRating.setText(String.format("%.1f ★  (%d reviews)", p.averageRating, p.reviewCount));
        } else {
            tvRating.setText("No reviews yet");
        }

        setRowVisible(R.id.rowPhone,     p.phoneNumber);
        setRowVisible(R.id.rowWebsite,   p.websiteUrl);
        setRowVisible(R.id.rowInstagram, p.instagramUrl);
        setRowVisible(R.id.rowFacebook,  p.facebookUrl);
    }

    private void setText(int viewId, String value) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setText(value != null && !value.isEmpty() ? value : "—");
        }
    }

    private void setRowVisible(int rowId, String value) {
        View row = findViewById(rowId);
        if (row != null) {
            row.setVisibility(value != null && !value.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
