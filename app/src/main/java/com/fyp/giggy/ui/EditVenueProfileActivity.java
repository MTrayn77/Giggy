// C21361681 – Michael Traynor
// EditVenueProfileActivity.java – Create or update venue profile
// Fix: after saving, navigate to ViewVenueProfileActivity instead of crashing

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.VenueProfile;
import com.fyp.giggy.utils.SessionManager;

public class EditVenueProfileActivity extends AppCompatActivity {

    private EditText etVenueName, etLocation, etDescription, etPhone, etWebsite, etInstagram, etFacebook, etCapacity;
    private Spinner spinnerVenueType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_venue_profile);

        SessionManager session = new SessionManager(this);

        etVenueName    = findViewById(R.id.etVenueName);
        etLocation     = findViewById(R.id.etLocation);
        etDescription  = findViewById(R.id.etDescription);
        etPhone        = findViewById(R.id.etPhone);
        etWebsite      = findViewById(R.id.etWebsite);
        etInstagram    = findViewById(R.id.etInstagram);
        etFacebook     = findViewById(R.id.etFacebook);
        etCapacity     = findViewById(R.id.etCapacity);
        spinnerVenueType = findViewById(R.id.spinnerVenueType);

        Button btnSave = findViewById(R.id.btnSaveProfile);

        // Venue type spinner
        String[] venueTypes = {"Bar", "Restaurant", "Club", "Concert Hall", "Outdoor", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, venueTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVenueType.setAdapter(typeAdapter);

        // Pre-fill if profile exists
        new Thread(() -> {
            VenueProfile existing = AppDatabase.getInstance(this)
                    .venueProfileDao().getProfileByUserId(session.getUserId());
            if (existing != null) {
                runOnUiThread(() -> prefillForm(existing, typeAdapter));
            }
        }).start();

        btnSave.setOnClickListener(v -> saveProfile(session));
    }

    private void prefillForm(VenueProfile p, ArrayAdapter<String> typeAdapter) {
        if (p.venueName    != null) etVenueName.setText(p.venueName);
        if (p.location     != null) etLocation.setText(p.location);
        if (p.description  != null) etDescription.setText(p.description);
        if (p.phoneNumber  != null) etPhone.setText(p.phoneNumber);
        if (p.websiteUrl   != null) etWebsite.setText(p.websiteUrl);
        if (p.instagramUrl != null) etInstagram.setText(p.instagramUrl);
        if (p.facebookUrl  != null) etFacebook.setText(p.facebookUrl);
        if (p.capacity > 0) etCapacity.setText(String.valueOf(p.capacity));
        if (p.venueType != null) {
            int pos = typeAdapter.getPosition(p.venueType);
            if (pos >= 0) spinnerVenueType.setSelection(pos);
        }
    }

    private void saveProfile(SessionManager session) {
        String venueName = etVenueName.getText().toString().trim();
        if (venueName.isEmpty()) {
            Toast.makeText(this, "Please enter a venue name", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            long userId = session.getUserId();

            VenueProfile existing = db.venueProfileDao().getProfileByUserId(userId);

            String venueType = spinnerVenueType.getSelectedItem() != null
                    ? spinnerVenueType.getSelectedItem().toString() : "Bar";
            String location  = etLocation.getText().toString().trim();
            if (location.isEmpty()) location = "Ireland";

            if (existing == null) {
                // Use the required constructor: (userId, venueName, location, venueType)
                existing = new VenueProfile(userId, venueName, location, venueType);
            } else {
                existing.venueName  = venueName;
                existing.location   = location;
                existing.venueType  = venueType;
            }

            existing.description  = etDescription.getText().toString().trim();
            existing.phoneNumber  = etPhone.getText().toString().trim();
            existing.websiteUrl   = etWebsite.getText().toString().trim();
            existing.instagramUrl = etInstagram.getText().toString().trim();
            existing.facebookUrl  = etFacebook.getText().toString().trim();

            String capStr = etCapacity.getText().toString().trim();
            try { existing.capacity = capStr.isEmpty() ? 0 : Integer.parseInt(capStr); }
            catch (NumberFormatException e) { existing.capacity = 0; }

            if (existing.id == 0) {
                db.venueProfileDao().insertProfile(existing);
            } else {
                db.venueProfileDao().updateProfile(existing);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                // Navigate to profile view, not back to edit
                Intent i = new Intent(this, ViewVenueProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            });
        }).start();
    }
}