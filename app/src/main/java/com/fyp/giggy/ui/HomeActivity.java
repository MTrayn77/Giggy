// C21361681 – Michael Traynor
// HomeActivity.java – Role-aware dashboard
// Sprint 4 update: booking buttons added, layout IDs updated to match activity_home.xml

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

        TextView tvUsername     = findViewById(R.id.tvUsername);
        TextView tvRole         = findViewById(R.id.tvRole);
        Button btnMyProfile     = findViewById(R.id.btnMyProfile);
        Button btnLogout        = findViewById(R.id.btnLogout);

        // Artist buttons
        Button btnBrowseGigs    = findViewById(R.id.btnBrowseGigs);
        Button btnSearchArtists = findViewById(R.id.btnSearchArtists);
        Button btnBookings      = findViewById(R.id.btnBookings);

        // Venue buttons
        Button btnPostGig       = findViewById(R.id.btnPostGig);
        Button btnFindArtists   = findViewById(R.id.btnFindArtists);
        Button btnVenueBookings = findViewById(R.id.btnVenueBookings);

        tvUsername.setText(name);

        if ("venue".equals(role)) {
            tvRole.setText("Venue Account");
            btnPostGig.setVisibility(View.VISIBLE);
            btnFindArtists.setVisibility(View.VISIBLE);
            btnVenueBookings.setVisibility(View.VISIBLE);

            btnMyProfile.setOnClickListener(v ->
                    startActivity(new Intent(this, ViewVenueProfileActivity.class)));
            btnPostGig.setOnClickListener(v ->
                    startActivity(new Intent(this, PostGigActivity.class)));
            btnFindArtists.setOnClickListener(v ->
                    startActivity(new Intent(this, SearchArtistsActivity.class)));
            btnVenueBookings.setOnClickListener(v ->
                    startActivity(new Intent(this, VenueBookingsActivity.class)));

        } else {
            tvRole.setText("Artist Account");
            btnBrowseGigs.setVisibility(View.VISIBLE);
            btnSearchArtists.setVisibility(View.VISIBLE);
            btnBookings.setVisibility(View.VISIBLE);

            btnMyProfile.setOnClickListener(v ->
                    startActivity(new Intent(this, ViewArtistProfileActivity.class)));
            btnBrowseGigs.setOnClickListener(v ->
                    startActivity(new Intent(this, BrowseGigsActivity.class)));
            btnSearchArtists.setOnClickListener(v ->
                    startActivity(new Intent(this, SearchArtistsActivity.class)));
            btnBookings.setOnClickListener(v ->
                    startActivity(new Intent(this, MyBookingsActivity.class)));
        }

        btnLogout.setOnClickListener(v -> {
            session.clearSession();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}