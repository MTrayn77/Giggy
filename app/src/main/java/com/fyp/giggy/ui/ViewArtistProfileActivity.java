// C21361681 – Michael Traynor
// ViewArtistProfileActivity.java – View an artist's profile
// Sprint 5: added "Send Message" button when viewing another user's profile

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.ArtistProfile;
import com.fyp.giggy.data.Booking;
import com.fyp.giggy.utils.SessionManager;

import java.util.List;

public class ViewArtistProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_artist_profile);

        SessionManager session = new SessionManager(this);
        Button btnBack    = findViewById(R.id.btnBack);
        Button btnEdit    = findViewById(R.id.btnEditProfile);
        Button btnMessage = findViewById(R.id.btnMessageProfile);

        btnBack.setOnClickListener(v -> finish());

        long viewUserId    = getIntent().getLongExtra("viewUserId", -1);
        long sessionUserId = session.getUserId();
        boolean isOwnProfile = (viewUserId == -1 || viewUserId == sessionUserId);
        long targetUserId    = isOwnProfile ? sessionUserId : viewUserId;

        if (btnEdit != null)
            btnEdit.setVisibility(isOwnProfile ? View.VISIBLE : View.GONE);
        if (btnEdit != null)
            btnEdit.setOnClickListener(v ->
                    startActivity(new Intent(this, EditArtistProfileActivity.class)));

        // Message button: only show when viewing someone else's profile
        if (btnMessage != null)
            btnMessage.setVisibility(isOwnProfile ? View.GONE : View.VISIBLE);

        if (btnMessage != null && !isOwnProfile) {
            final long otherUserId = targetUserId;
            btnMessage.setOnClickListener(v -> {
                // Find a shared booking to tie the conversation to
                new Thread(() -> {
                    List<Booking> bookings = AppDatabase.getInstance(this)
                            .bookingDao().getBookingsForArtist(sessionUserId);

                    Booking shared = null;
                    for (Booking b : bookings) {
                        if (b.artistUserId == otherUserId || b.venueUserId == otherUserId) {
                            shared = b;
                            break;
                        }
                    }

                    final Booking booking = shared;
                    runOnUiThread(() -> {
                        if (booking == null) {
                            Toast.makeText(this,
                                    "No booking found with this artist. Apply to a gig first.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Intent i = new Intent(this, ConversationActivity.class);
                            i.putExtra("bookingId", booking.id);
                            startActivity(i);
                        }
                    });
                }).start();
            });
        }

        new Thread(() -> {
            ArtistProfile profile = AppDatabase.getInstance(this)
                    .artistProfileDao().getProfileByUserId(targetUserId);
            runOnUiThread(() -> {
                if (profile == null) {
                    if (isOwnProfile) {
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
        setText(R.id.tvStageName,  p.stageName);
        setText(R.id.tvGenre,      p.genre);
        setText(R.id.tvActType,    p.actType);
        setText(R.id.tvLocation,   p.location);
        setText(R.id.tvBio,        p.bio);
        setText(R.id.tvSpotify,    p.spotifyUrl);
        setText(R.id.tvInstagram,  p.instagramUrl);
        setText(R.id.tvFacebook,   p.facebookUrl);
        setText(R.id.tvYoutube,    p.youtubeUrl);
        setText(R.id.tvWebsite,    p.websiteUrl);

        TextView tvRating = findViewById(R.id.tvRating);
        if (tvRating != null) {
            // Rating display handled by existing layout
        }
    }

    private void setText(int id, String value) {
        TextView tv = findViewById(id);
        if (tv != null) tv.setText(value != null && !value.isEmpty() ? value : "—");
    }
}