// C21361681 – Michael Traynor
// SearchVenuesActivity.java – Artist browses venue profiles
// Sprint 4: new screen for artists to find venues

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.VenueProfile;

import java.util.List;

public class SearchVenuesActivity extends AppCompatActivity {

    private List<VenueProfile> profiles;
    private Spinner spVenueTypeFilter;
    private EditText etLocationFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_venues);

        spVenueTypeFilter = findViewById(R.id.spVenueTypeFilter);
        etLocationFilter  = findViewById(R.id.etLocationFilter);
        ListView listView = findViewById(R.id.listVenues);
        Button btnBack    = findViewById(R.id.btnBack);
        Button btnSearch  = findViewById(R.id.btnSearch);

        btnBack.setOnClickListener(v -> finish());

        // Venue type spinner
        String[] venueTypes = {"Any", "Bar", "Restaurant", "Club", "Concert Hall", "Outdoor", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, venueTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVenueTypeFilter.setAdapter(typeAdapter);

        btnSearch.setOnClickListener(v -> searchVenues(listView));

        // Load all on open
        searchVenues(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (profiles != null && position < profiles.size()) {
                VenueProfile p = profiles.get(position);
                Intent i = new Intent(this, ViewVenueProfileActivity.class);
                i.putExtra("viewUserId", p.userId);
                startActivity(i);
            }
        });
    }

    private void searchVenues(ListView listView) {
        String venueType = spVenueTypeFilter.getSelectedItem() != null
                ? spVenueTypeFilter.getSelectedItem().toString() : "Any";
        String location  = etLocationFilter.getText().toString().trim();

        boolean anyType     = venueType.equals("Any");
        boolean anyLocation = location.isEmpty();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            if (anyType && anyLocation) {
                profiles = db.venueProfileDao().getAllProfiles();
            } else if (!anyType && anyLocation) {
                profiles = db.venueProfileDao().searchByVenueType(venueType);
            } else if (anyType) {
                profiles = db.venueProfileDao().searchByLocation(location);
            } else {
                // Filter both in memory if no combined query exists
                List<VenueProfile> byType = db.venueProfileDao().searchByVenueType(venueType);
                profiles = new java.util.ArrayList<>();
                for (VenueProfile p : byType) {
                    if (p.location != null && p.location.toLowerCase()
                            .contains(location.toLowerCase())) {
                        profiles.add(p);
                    }
                }
            }

            String[] items = new String[profiles.size()];
            for (int i = 0; i < profiles.size(); i++) {
                VenueProfile p = profiles.get(i);
                items[i] = (p.venueName != null ? p.venueName : "Venue")
                        + "\n" + (p.venueType != null ? p.venueType : "")
                        + (p.location != null && !p.location.isEmpty() ? "  ·  " + p.location : "");
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_2,
                        android.R.id.text1, items);
                listView.setAdapter(adapter);
                if (profiles.isEmpty()) {
                    Toast.makeText(this, "No venues found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}