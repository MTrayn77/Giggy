// C21361681 – Michael Traynor
// EditArtistProfileActivity.java – Create or update artist profile
// Fix: after saving, navigate to ViewArtistProfileActivity (not finish() which crashes on first setup)

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.ArtistProfile;
import com.fyp.giggy.utils.SessionManager;

public class EditArtistProfileActivity extends AppCompatActivity {

    private EditText etStageName, etLocation, etBio, etSpotify, etInstagram, etFacebook, etYoutube, etWebsite;
    private Spinner spinnerGenre, spinnerActType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_artist_profile);

        SessionManager session = new SessionManager(this);

        etStageName  = findViewById(R.id.etStageName);
        etLocation   = findViewById(R.id.etLocation);
        etBio        = findViewById(R.id.etBio);
        etSpotify    = findViewById(R.id.etSpotify);
        etInstagram  = findViewById(R.id.etInstagram);
        etFacebook   = findViewById(R.id.etFacebook);
        etYoutube    = findViewById(R.id.etYoutube);
        etWebsite    = findViewById(R.id.etWebsite);
        spinnerGenre   = findViewById(R.id.spinnerGenre);
        spinnerActType = findViewById(R.id.spinnerActType);

        Button btnSave = findViewById(R.id.btnSaveProfile);

        // Genre spinner
        ArrayAdapter<CharSequence> genreAdapter = ArrayAdapter.createFromResource(
                this, R.array.genres, android.R.layout.simple_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        // Act type spinner
        String[] actTypes = {"Solo", "Duo", "Band", "DJ", "Other"};
        ArrayAdapter<String> actAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, actTypes);
        actAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActType.setAdapter(actAdapter);

        // Pre-fill if profile already exists
        new Thread(() -> {
            ArtistProfile existing = AppDatabase.getInstance(this)
                    .artistProfileDao().getProfileByUserId(session.getUserId());
            if (existing != null) {
                runOnUiThread(() -> prefillForm(existing, genreAdapter, actAdapter));
            }
        }).start();

        btnSave.setOnClickListener(v -> saveProfile(session));
    }

    private void prefillForm(ArtistProfile p,
                             ArrayAdapter<CharSequence> genreAdapter,
                             ArrayAdapter<String> actAdapter) {
        if (p.stageName  != null) etStageName.setText(p.stageName);
        if (p.location   != null) etLocation.setText(p.location);
        if (p.bio        != null) etBio.setText(p.bio);
        if (p.spotifyUrl != null) etSpotify.setText(p.spotifyUrl);
        if (p.instagramUrl != null) etInstagram.setText(p.instagramUrl);
        if (p.facebookUrl  != null) etFacebook.setText(p.facebookUrl);
        if (p.youtubeUrl   != null) etYoutube.setText(p.youtubeUrl);
        if (p.websiteUrl   != null) etWebsite.setText(p.websiteUrl);

        if (p.genre != null) {
            int pos = genreAdapter.getPosition(p.genre);
            if (pos >= 0) spinnerGenre.setSelection(pos);
        }
        if (p.actType != null) {
            int pos = actAdapter.getPosition(p.actType);
            if (pos >= 0) spinnerActType.setSelection(pos);
        }
    }

    private void saveProfile(SessionManager session) {
        String stageName = etStageName.getText().toString().trim();
        if (stageName.isEmpty()) {
            Toast.makeText(this, "Please enter a stage name", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            long userId = session.getUserId();

            ArtistProfile existing = db.artistProfileDao().getProfileByUserId(userId);

            String genre    = spinnerGenre.getSelectedItem()   != null ? spinnerGenre.getSelectedItem().toString()   : "Rock";
            String actType  = spinnerActType.getSelectedItem() != null ? spinnerActType.getSelectedItem().toString() : "Solo";
            String location = etLocation.getText().toString().trim();
            if (location.isEmpty()) location = "Ireland";

            if (existing == null) {
                // Use the required constructor: (userId, stageName, genre, actType, location)
                existing = new ArtistProfile(userId, stageName, genre, actType, location);
            } else {
                existing.stageName = stageName;
                existing.genre     = genre;
                existing.actType   = actType;
                existing.location  = location;
            }

            existing.bio          = etBio.getText().toString().trim();
            existing.spotifyUrl   = etSpotify.getText().toString().trim();
            existing.instagramUrl = etInstagram.getText().toString().trim();
            existing.facebookUrl  = etFacebook.getText().toString().trim();
            existing.youtubeUrl   = etYoutube.getText().toString().trim();
            existing.websiteUrl   = etWebsite.getText().toString().trim();

            if (existing.id == 0) {
                db.artistProfileDao().insertProfile(existing);
            } else {
                db.artistProfileDao().updateProfile(existing);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                // Navigate to profile view, clearing the back stack so pressing back
                // from profile goes to Home, not back to edit
                Intent i = new Intent(this, ViewArtistProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            });
        }).start();
    }
}