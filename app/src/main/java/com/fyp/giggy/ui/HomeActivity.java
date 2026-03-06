// C21361681 – Michael Traynor
// HomeActivity.java – Role-aware dashboard
// Sprint 4 update: added My Bookings (artist) and Booking Requests (venue) buttons

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.giggy.R;
import com.fyp.giggy.utils.SessionManager;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SessionManager session = new SessionManager(this);
        String role = session.getRole();
        String name = session.getUsername();
        if (name == null) name = getIntent().getStringExtra("name");
        if (role == null) role = getIntent().getStringExtra("role");
        if (name == null) name = "Giggy User";
        if (role == null) role = "artist";

        TextView tvWelcome       = findViewById(R.id.tvWelcome);
        TextView tvRole          = findViewById(R.id.tvRole);
        Button btnAction         = findViewById(R.id.btnPrimaryAction);
        Button btnSecondary      = findViewById(R.id.btnSecondaryAction);
        Button btnMyProfile      = findViewById(R.id.btnMyProfile);
        Button btnBookings       = findViewById(R.id.btnBookings);
        Button btnLogout         = findViewById(R.id.btnLogout);

        tvWelcome.setText(name);
        tvRole.setText(role.equals("venue") ? "Venue Account" : "Artist Account");

        final String finalRole = role;

        if ("venue".equals(role)) {
            btnAction.setText("Post a Gig");
            btnSecondary.setText("Find Artists");
            btnSecondary.setVisibility(View.VISIBLE);
            btnBookings.setText("Booking Requests");
        } else {
            btnAction.setText("Browse Gigs");
            btnSecondary.setVisibility(View.GONE);
            btnBookings.setText("My Applications");
        }

        btnAction.setOnClickListener(v -> {
            if ("venue".equals(finalRole)) {
                startActivity(new Intent(this, PostGigActivity.class));
            } else {
                startActivity(new Intent(this, BrowseGigsActivity.class));
            }
        });

        btnSecondary.setOnClickListener(v ->
                startActivity(new Intent(this, SearchArtistsActivity.class))
        );

        btnBookings.setOnClickListener(v -> {
            if ("venue".equals(finalRole)) {
                startActivity(new Intent(this, VenueBookingsActivity.class));
            } else {
                startActivity(new Intent(this, MyBookingsActivity.class));
            }
        });

        btnMyProfile.setOnClickListener(v -> {
            Intent i;
            if ("venue".equals(finalRole)) {
                i = new Intent(this, ViewVenueProfileActivity.class);
            } else {
                i = new Intent(this, ViewArtistProfileActivity.class);
            }
            startActivity(i);
        });

        btnLogout.setOnClickListener(v -> {
            session.clearSession();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}