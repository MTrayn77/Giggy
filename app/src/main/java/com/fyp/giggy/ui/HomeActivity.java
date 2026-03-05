// C21361681 – Michael Traynor
// Role-aware dashboard
// Sprint 2 update: Edit Profile button now launches correct profile screen

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
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

        // Sprint 2: read role from SessionManager instead of Intent extras
        SessionManager session = new SessionManager(this);
        String role = session.getRole();
        String name = session.getUsername();
        if (name == null) name = getIntent().getStringExtra("name");
        if (role == null) role = getIntent().getStringExtra("role");
        if (name == null) name = "Giggy User";
        if (role == null) role = "artist";

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvRole    = findViewById(R.id.tvRole);
        Button btnAction      = findViewById(R.id.btnPrimaryAction);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        Button btnLogout      = findViewById(R.id.btnLogout);

        // Just the name, no "Welcome"
        tvWelcome.setText(name);
        tvRole.setText(role.equals("venue") ? "Venue Account" : "Artist Account");

        if ("venue".equals(role)) {
            btnAction.setText("Create a New Gig");
        } else {
            btnAction.setText("Browse Gigs");
        }

        btnAction.setOnClickListener(v -> {
            // TODO (Sprint 3): navigate to role-specific features
        });

        // Sprint 2: launch the correct profile screen based on role
        final String finalRole = role;
        btnEditProfile.setOnClickListener(v -> {
            Intent i;
            if ("venue".equals(finalRole)) {
                i = new Intent(this, EditVenueProfileActivity.class);
            } else {
                i = new Intent(this, EditArtistProfileActivity.class);
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