// C21361681 – Michael Traynor
// BrowseGigsActivity.java – Artists browse and search open gig listings
// Sprint 3: Search & Discovery

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.GigListing;

import java.util.List;

public class BrowseGigsActivity extends AppCompatActivity {

    private ListView listView;
    private EditText etLocationFilter;
    private Spinner spGenreFilter;
    private List<GigListing> currentGigs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_gigs);

        listView        = findViewById(R.id.listGigs);
        etLocationFilter = findViewById(R.id.etLocationFilter);
        spGenreFilter   = findViewById(R.id.spGenreFilter);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnBack   = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Genre spinner — "Any" + all genres
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.genres_with_any, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenreFilter.setAdapter(adapter);

        // Load all open gigs on start
        loadGigs("", "Any");

        btnSearch.setOnClickListener(v -> {
            String location = etLocationFilter.getText().toString().trim();
            String genre    = spGenreFilter.getSelectedItem().toString();
            loadGigs(location, genre);
        });

        // Tap a gig to view its details
        listView.setOnItemClickListener((parent, view, position, id) -> {
            GigListing gig = currentGigs.get(position);
            Intent i = new Intent(this, GigDetailActivity.class);
            i.putExtra("gigId", gig.id);
            startActivity(i);
        });
    }

    private void loadGigs(String location, String genre) {
        new Thread(() -> {
            List<GigListing> gigs;
            boolean hasLocation = !location.isEmpty();
            boolean hasGenre    = !genre.equals("Any");

            if (hasGenre && hasLocation) {
                gigs = AppDatabase.getInstance(this).gigListingDao()
                        .getOpenGigsByGenreAndLocation(genre, location);
            } else if (hasGenre) {
                gigs = AppDatabase.getInstance(this).gigListingDao()
                        .getOpenGigsByGenre(genre);
            } else if (hasLocation) {
                gigs = AppDatabase.getInstance(this).gigListingDao()
                        .getOpenGigsByLocation(location);
            } else {
                gigs = AppDatabase.getInstance(this).gigListingDao()
                        .getAllOpenGigs();
            }

            currentGigs = gigs;

            // Build display strings for the list
            String[] items = new String[gigs.size()];
            for (int i = 0; i < gigs.size(); i++) {
                GigListing g = gigs.get(i);
                items[i] = g.venueName + " — " + g.gigDate + " at " + g.gigTime
                        + "\n" + g.genreWanted + "  ·  " + g.location
                        + (g.payAmount > 0 ? "  ·  €" + String.format("%.0f", g.payAmount) : "");
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_2,
                        android.R.id.text1, items);
                listView.setAdapter(listAdapter);

                if (gigs.isEmpty()) {
                    Toast.makeText(this, "No gigs found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}