// C21361681 – Michael Traynor
// SearchArtistsActivity.java – Venue searches/browses artist profiles
// Fix: uses correct layout IDs (spGenreFilter, etLocationFilter, listArtists)
// Fix: tapping an artist opens THEIR profile via viewUserId extra

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.ArtistProfile;

import java.util.List;

public class SearchArtistsActivity extends AppCompatActivity {

    private List<ArtistProfile> profiles;
    private Spinner spGenreFilter;
    private EditText etLocationFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artists);

        spGenreFilter    = findViewById(R.id.spGenreFilter);
        etLocationFilter = findViewById(R.id.etLocationFilter);
        ListView listView = findViewById(R.id.listArtists);
        Button btnBack   = findViewById(R.id.btnBack);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnBack.setOnClickListener(v -> finish());

        // Genre spinner
        ArrayAdapter<CharSequence> genreAdapter = ArrayAdapter.createFromResource(
                this, R.array.genres_with_any, android.R.layout.simple_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenreFilter.setAdapter(genreAdapter);

        btnSearch.setOnClickListener(v -> searchArtists(listView));

        // Load all on open
        searchArtists(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (profiles != null && position < profiles.size()) {
                ArtistProfile p = profiles.get(position);
                Intent i = new Intent(this, ViewArtistProfileActivity.class);
                i.putExtra("viewUserId", p.userId);
                startActivity(i);
            }
        });
    }

    private void searchArtists(ListView listView) {
        String genre    = spGenreFilter.getSelectedItem() != null
                ? spGenreFilter.getSelectedItem().toString() : "Any";
        String location = etLocationFilter.getText().toString().trim();

        boolean anyGenre    = genre.equals("Any");
        boolean anyLocation = location.isEmpty();

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