// C21361681 – Michael Traynor
// Activity for Venues to create or edit their profile

package com.fyp.giggy.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.VenueProfile;
import com.fyp.giggy.data.VenueProfileDao;
import com.fyp.giggy.utils.SessionManager;

public class EditVenueProfileActivity extends AppCompatActivity {

    private EditText etVenueName, etLocation, etDescription,
            etPhone, etWebsite, etInstagram,
            etFacebook, etCapacity;
    private Spinner  spinnerVenueType;
    private Button   btnSave;

    private VenueProfileDao venueProfileDao;
    private long userId;
    private VenueProfile existingProfile;

    private static final String[] VENUE_TYPES = {
            "Select Venue Type", "Pub", "Late Bar", "Restaurant",
            "Event Space", "Hotel", "Club", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_venue_profile);

        SessionManager session = new SessionManager(this);
        userId = session.getUserId();

        venueProfileDao = AppDatabase.getInstance(this).venueProfileDao();

        bindViews();
        setupSpinner();
        loadExistingProfile();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void bindViews() {
        etVenueName      = findViewById(R.id.etVenueName);
        etLocation       = findViewById(R.id.etLocation);
        etDescription    = findViewById(R.id.etDescription);
        etPhone          = findViewById(R.id.etPhone);
        etWebsite        = findViewById(R.id.etWebsite);
        etInstagram      = findViewById(R.id.etInstagram);
        etFacebook       = findViewById(R.id.etFacebook);
        etCapacity       = findViewById(R.id.etCapacity);
        spinnerVenueType = findViewById(R.id.spinnerVenueType);
        btnSave          = findViewById(R.id.btnSaveProfile);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, VENUE_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVenueType.setAdapter(adapter);
    }

    private void loadExistingProfile() {
        new Thread(() -> {
            existingProfile = venueProfileDao.getProfileByUserId(userId);
            if (existingProfile != null) {
                runOnUiThread(() -> populateFields(existingProfile));
            }
        }).start();
    }

    private void populateFields(VenueProfile profile) {
        etVenueName.setText(profile.venueName);
        etLocation.setText(profile.location);
        etDescription.setText(profile.description != null ? profile.description : "");
        etPhone.setText(profile.phoneNumber != null ? profile.phoneNumber : "");
        etWebsite.setText(profile.websiteUrl != null ? profile.websiteUrl : "");
        etInstagram.setText(profile.instagramUrl != null ? profile.instagramUrl : "");
        etFacebook.setText(profile.facebookUrl != null ? profile.facebookUrl : "");
        etCapacity.setText(profile.capacity > 0 ? String.valueOf(profile.capacity) : "");

        for (int i = 0; i < VENUE_TYPES.length; i++) {
            if (VENUE_TYPES[i].equals(profile.venueType)) {
                spinnerVenueType.setSelection(i);
                break;
            }
        }
    }

    private void saveProfile() {
        String venueName  = etVenueName.getText().toString().trim();
        String location   = etLocation.getText().toString().trim();
        String venueType  = spinnerVenueType.getSelectedItem().toString();
        String capacityStr = etCapacity.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(venueName)) {
            etVenueName.setError("Venue name is required");
            etVenueName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }
        if (venueType.equals("Select Venue Type")) {
            Toast.makeText(this, "Please select a venue type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (existingProfile == null) {
            existingProfile = new VenueProfile(userId, venueName, location, venueType);
        } else {
            existingProfile.venueName  = venueName;
            existingProfile.location   = location;
            existingProfile.venueType  = venueType;
        }

        existingProfile.description  = nullIfEmpty(etDescription.getText().toString().trim());
        existingProfile.phoneNumber  = nullIfEmpty(etPhone.getText().toString().trim());
        existingProfile.websiteUrl   = nullIfEmpty(etWebsite.getText().toString().trim());
        existingProfile.instagramUrl = nullIfEmpty(etInstagram.getText().toString().trim());
        existingProfile.facebookUrl  = nullIfEmpty(etFacebook.getText().toString().trim());

        if (!TextUtils.isEmpty(capacityStr)) {
            try {
                existingProfile.capacity = Integer.parseInt(capacityStr);
            } catch (NumberFormatException e) {
                etCapacity.setError("Enter a valid number");
                etCapacity.requestFocus();
                return;
            }
        }

        final VenueProfile profileToSave = existingProfile;

        new Thread(() -> {
            try {
                venueProfileDao.insertProfile(profileToSave);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    finish();
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