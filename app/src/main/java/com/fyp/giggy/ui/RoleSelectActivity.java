// C21361681 – Michael Traynor
// ]Choose Artist or Venue and passes role to Signup

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.giggy.R;

public class RoleSelectActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);

        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        View artist = findViewById(R.id.cardArtist);
        View venue  = findViewById(R.id.cardVenue);

        artist.setOnClickListener(v -> openSignup("artist"));
        venue.setOnClickListener(v -> openSignup("venue"));
    }

    private void openSignup(String role) {
        Intent i = new Intent(this, SignupActivity.class);
        i.putExtra("role", role);
        startActivity(i);
    }
}
