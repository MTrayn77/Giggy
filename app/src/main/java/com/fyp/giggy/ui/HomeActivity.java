// C21361681 – Michael Traynor
// Role-aware dashboard

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.giggy.R;

public class HomeActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvRole    = findViewById(R.id.tvRole);
        Button btnAction   = findViewById(R.id.btnPrimaryAction);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        Button btnLogout   = findViewById(R.id.btnLogout);

        String name = getIntent().getStringExtra("name");
        String role = getIntent().getStringExtra("role");
        if (name == null) name = "Giggy User";
        if (role == null) role = "artist";

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

        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
