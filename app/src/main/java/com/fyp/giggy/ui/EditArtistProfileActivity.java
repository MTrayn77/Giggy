// C21361681 – Michael Traynor
// Activity for Artists to create or edit their EPK-style profile

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.ArtistProfile;
import com.fyp.giggy.data.ArtistProfileDao;
import com.fyp.giggy.utils.SessionManager;

public class EditArtistProfileActivity extends AppCompatActivity {

    private EditText etStageName, etLocation, etBio,
            etSpotify, etInstagram, etFacebook,
            etYoutube, etWebsite;
    private Spinner  spinnerGenre, spinnerActType;
    private Button   btnSave;

    private ArtistProfileDao artistProfileDao;
    private long userId;
    private ArtistProfile existingProfile;

    // Genre options
    private static final String[] GENRES = {
            "Select Genre", "Rock", "Pop", "Traditional/Folk", "Jazz", "Blues",
            "Classical", "Country", "Electronic", "Hip-Hop/R&B", "Soul/Funk",
            "Comedy/Other"
    };

    // Act type options
    private static final String[] ACT_TYPES = {
            "Select Act Type", "Solo", "Duo", "Trio", "Band", "DJ", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_artist_profile);

        // Get logged-in user ID from session
        SessionManager session = new SessionManager(this);
        userId = session.getUserId();

        artistProfileDao = AppDatabase.getInstance(this).artistProfileDao();

        bindViews();
        setupSpinners();
        loadExistingProfile();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void bindViews() {
        etStageName   = findViewById(R.id.etStageName);
        etLocation    = findViewById(R.id.etLocation);
        etBio         = findViewById(R.id.etBio);
        etSpotify     = findViewById(R.id.etSpotify);
        etInstagram   = findViewById(R.id.etInstagram);
        etFacebook    = findViewById(R.id.etFacebook);
        etYoutube     = findViewById(R.id.etYoutube);
        etWebsite     = findViewById(R.id.etWebsite);
        spinnerGenre  = findViewById(R.id.spinnerGenre);
        spinnerActType = findViewById(R.id.spinnerActType);
        btnSave       = findViewById(R.id.btnSaveProfile);
    }

    private void setupSpinners() {
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, GENRES);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        ArrayAdapter<String> actAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ACT_TYPES);
        actAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActType.setAdapter(actAdapter);
    }

    // Load profile data if it already exists (edit mode)
    private void loadExistingProfile() {
        new Thread(() -> {
            existingProfile = artistProfileDao.getProfileByUserId(userId);
            if (existingProfile != null) {
                runOnUiThread(() -> populateFields(existingProfile));
            }
        }).start();
    }

    private void populateFields(ArtistProfile profile) {
        etStageName.setText(profile.stageName);
        etLocation.setText(profile.location);
        etBio.setText(profile.bio != null ? profile.bio : "");
        etSpotify.setText(profile.spotifyUrl != null ? profile.spotifyUrl : "");
        etInstagram.setText(profile.instagramUrl != null ? profile.instagramUrl : "");
        etFacebook.setText(profile.facebookUrl != null ? profile.facebookUrl : "");
        etYoutube.setText(profile.youtubeUrl != null ? profile.youtubeUrl : "");
        etWebsite.setText(profile.websiteUrl != null ? profile.websiteUrl : "");

        // Set spinner selections
        setSpinnerSelection(spinnerGenre, GENRES, profile.genre);
        setSpinnerSelection(spinnerActType, ACT_TYPES, profile.actType);
    }

    private void setSpinnerSelection(Spinner spinner, String[] options, String value) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void saveProfile() {
        String stageName = etStageName.getText().toString().trim();
        String location  = etLocation.getText().toString().trim();
        String genre     = spinnerGenre.getSelectedItem().toString();
        String actType   = spinnerActType.getSelectedItem().toString();

        // Validate required fields
        if (TextUtils.isEmpty(stageName)) {
            etStageName.setError("Stage name is required");
            etStageName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }
        if (genre.equals("Select Genre")) {
            Toast.makeText(this, "Please select a genre", Toast.LENGTH_SHORT).show();
            return;
        }
        if (actType.equals("Select Act Type")) {
            Toast.makeText(this, "Please select an act type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build or update the profile object
        if (existingProfile == null) {
            existingProfile = new ArtistProfile(userId, stageName, genre, actType, location);
        } else {
            existingProfile.stageName = stageName;
            existingProfile.genre     = genre;
            existingProfile.actType   = actType;
            existingProfile.location  = location;
        }

        existingProfile.bio          = nullIfEmpty(etBio.getText().toString().trim());
        existingProfile.spotifyUrl   = nullIfEmpty(etSpotify.getText().toString().trim());
        existingProfile.instagramUrl = nullIfEmpty(etInstagram.getText().toString().trim());
        existingProfile.facebookUrl  = nullIfEmpty(etFacebook.getText().toString().trim());
        existingProfile.youtubeUrl   = nullIfEmpty(etYoutube.getText().toString().trim());
        existingProfile.websiteUrl   = nullIfEmpty(etWebsite.getText().toString().trim());

        final ArtistProfile profileToSave = existingProfile;

        new Thread(() -> {
            try {
                artistProfileDao.insertProfile(profileToSave); // REPLACE handles both insert + update
                runOnUiThread(() -> {
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the artist dashboard
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Could not save profile. Please try again.",
                                Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private String nullIfEmpty(String value) {
        return TextUtils.isEmpty(value) ? null : value;
    }
}