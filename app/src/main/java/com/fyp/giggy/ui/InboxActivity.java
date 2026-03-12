// C21361681 – Michael Traynor
// InboxActivity.java – Shows all conversations (one per booking) for the logged-in user
// Sprint 5: Messaging

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.Booking;
import com.fyp.giggy.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    private List<Booking> bookings = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private TextView tvEmpty;
    private long myUserId;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        SessionManager session = new SessionManager(this);
        myUserId = session.getUserId();
        role     = session.getRole();

        Button btnBack = findViewById(R.id.btnBack);
        listView = findViewById(R.id.listConversations);
        tvEmpty  = findViewById(R.id.tvEmpty);

        btnBack.setOnClickListener(v -> finish());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position < bookings.size()) {
                Intent i = new Intent(this, ConversationActivity.class);
                i.putExtra("bookingId", bookings.get(position).id);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversations(); // Reload data without recreating the Activity
    }

    private void loadConversations() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            List<Booking> result;
            if ("venue".equals(role)) {
                result = db.bookingDao().getBookingsForVenue(myUserId);
            } else {
                result = db.bookingDao().getBookingsForArtist(myUserId);
            }

            String[] items = new String[result.size()];
            for (int i = 0; i < result.size(); i++) {
                Booking b = result.get(i);
                String otherName = "venue".equals(role)
                        ? (b.artistName != null ? b.artistName : "Artist #" + b.artistUserId)
                        : (b.venueName  != null ? b.venueName  : "Venue #"  + b.venueUserId);
                items[i] = otherName + "\n" + b.gigDate + "  ·  " + b.status.toUpperCase();
            }

            bookings = result;

            runOnUiThread(() -> {
                if (tvEmpty != null)
                    tvEmpty.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);

                if (!result.isEmpty()) {
                    if (adapter == null) {
                        adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_2,
                                android.R.id.text1,
                                items);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.clear();
                        adapter.addAll(items);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }).start();
    }
}