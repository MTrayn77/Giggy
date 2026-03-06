// C21361681 – Michael Traynor
// ViewArtistProfileActivity.java – View an artist's profile
// Fix: if viewUserId extra is passed, show THAT user's profile (for venue searching artists)
//      otherwise fall back to the logged-in user's own profile

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.ArtistProfile;
import com.fyp.giggy.utils.SessionManager;

public class ViewArtistProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_artist_profile);

        SessionManager session = new SessionManager(this);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnEdit = findViewById(R.id.btnEditProfile);

        btnBack.setOnClickListener(v -> finish());

        // If a viewUserId was passed (e.g. from SearchArtistsActivity), use it.
        // Otherwise use the logged-in user's own id.
        long viewUserId = getIntent().getLongExtra("viewUserId", -1);
        long sessionUserId = session.getUserId();

        boolean isOwnProfile = (viewUserId == -1 || viewUserId == sessionUserId);
        long targetUserId = isOwnProfile ? sessionUserId : viewUserId;

        // Only show edit button when viewing own profile
        if (btnEdit != null) {
            btnEdit.setVisibility(isOwnProfile ? View.VISIBLE : View.GONE);
        }

        if (btnEdit != null) {
            btnEdit.setOnClickListener(v ->
                    startActivity(new Intent(this, EditArtistProfileActivity.class))
            );
        }

        new Thread(() -> {
            ArtistProfile profile = AppDatabase.getInstance(this)
                    .artistProfileDao().getProfileByUserId(targetUserId);

            runOnUiThread(() -> {
                if (profile == null) {
                    if (isOwnProfile) {
                        // Own profile not set up yet — go to edit
                        startActivity(new Intent(this, EditArtistProfileActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;
                }
                populateViews(profile);
            });
        }).start();
    }

    private void populateViews(ArtistProfile p) {
        setText(R.id.tvStageName,   p.stageName);
        setText(R.id.tvGenre,       p.genre);
        setText(R.id.tvActType,     p.actType);
        setText(R.id.tvLocation,    p.location);
        setText(R.id.tvBio,         p.bio);
        setText(R.id.tvSpotify,     p.spotifyUrl);
        setText(R.id.tvInstagram,   p.instagramUrl);
        setText(R.id.tvFacebook,    p.facebookUrl);
        setText(R.id.tvYoutube,     p.youtubeUrl);
        setText(R.id.tvWebsite,     p.websiteUrl);
    }

    private void setText(int id, String value) {
        TextView tv = findViewById(id);
        if (tv != null) tv.setText(value != null && !value.isEmpty() ? value : "—");
    }
}