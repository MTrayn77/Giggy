// C21361681 – Michael Traynor
// SearchArtistsActivity.java – Venue searches/browses artist profiles
// Fix: tapping an artist now opens THEIR profile (passes viewUserId), not the logged-in user's

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.ArtistProfile;
import com.fyp.giggy.utils.SessionManager;

import java.util.List;

public class SearchArtistsActivity extends AppCompatActivity {

    private List<ArtistProfile> profiles;
    private Spinner spinnerGenre, spinnerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artists);

        spinnerGenre    = findViewById(R.id.spinnerGenre);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        ListView listView = findViewById(R.id.listArtists);
        Button btnBack   = findViewById(R.id.btnBack);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnBack.setOnClickListener(v -> finish());

        // Genre spinner
        ArrayAdapter<CharSequence> genreAdapter = ArrayAdapter.createFromResource(
                this, R.array.genres_with_any, android.R.layout.simple_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        // Location spinner - populated from existing profiles
        loadLocationSpinner();

        btnSearch.setOnClickListener(v -> searchArtists(listView));

        // Load all on open
        searchArtists(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (profiles != null && position < profiles.size()) {
                ArtistProfile p = profiles.get(position);
                Intent i = new Intent(this, ViewArtistProfileActivity.class);
                // Pass the artist's userId so ViewArtistProfileActivity shows THEIR profile
                i.putExtra("viewUserId", p.userId);
                startActivity(i);
            }
        });
    }

    private void loadLocationSpinner() {
        new Thread(() -> {
            List<ArtistProfile> all = AppDatabase.getInstance(this)
                    .artistProfileDao().getAllProfiles();

            // Collect unique locations
            java.util.LinkedHashSet<String> locations = new java.util.LinkedHashSet<>();
            locations.add("Any");
            for (ArtistProfile p : all) {
                if (p.location != null && !p.location.isEmpty()) {
                    locations.add(p.location);
                }
            }

            String[] locArray = locations.toArray(new String[0]);
            runOnUiThread(() -> {
                ArrayAdapter<String> locAdapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, locArray);
                locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocation.setAdapter(locAdapter);
            });
        }).start();
    }

    private void searchArtists(ListView listView) {
        String genre    = spinnerGenre.getSelectedItem() != null
                ? spinnerGenre.getSelectedItem().toString() : "Any";
        String location = spinnerLocation.getSelectedItem() != null
                ? spinnerLocation.getSelectedItem().toString() : "Any";

        boolean anyGenre    = genre.equals("Any");
        boolean anyLocation = location.equals("Any");

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            if (anyGenre && anyLocation) {
                profiles = db.artistProfileDao().getAllProfiles();
            } else if (!anyGenre && anyLocation) {
                profiles = db.artistProfileDao().searchByGenre(genre);
            } else if (anyGenre) {
                profiles = db.artistProfileDao().searchByLocation(location);
            } else {
                profiles = db.artistProfileDao().searchByGenreAndLocation(genre, location);
            }

            String[] items = new String[profiles.size()];
            for (int i = 0; i < profiles.size(); i++) {
                ArtistProfile p = profiles.get(i);
                items[i] = (p.stageName != null ? p.stageName : "Artist")
                        + "\n" + (p.genre != null ? p.genre : "")
                        + (p.location != null && !p.location.isEmpty() ? "  ·  " + p.location : "");
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_2,
                        android.R.id.text1, items);
                listView.setAdapter(adapter);

                if (profiles.isEmpty()) {
                    Toast.makeText(this, "No artists found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}