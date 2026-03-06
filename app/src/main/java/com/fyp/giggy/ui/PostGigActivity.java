// C21361681 – Michael Traynor
// PostGigActivity.java – Venue posts a new gig
// Sprint 4 fix: DatePickerDialog + TimePickerDialog instead of manual text entry

package com.fyp.giggy.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.*;
import com.fyp.giggy.utils.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class PostGigActivity extends AppCompatActivity {

    private Button btnDate, btnTime;
    private EditText etDuration, etPay, etDescription;
    private Spinner spinnerGenre;
    private String selectedDate = "", selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_gig);

        SessionManager session = new SessionManager(this);

        btnDate        = findViewById(R.id.btnPickDate);
        btnTime        = findViewById(R.id.btnPickTime);
        etDuration     = findViewById(R.id.etDuration);
        etPay          = findViewById(R.id.etPay);
        etDescription  = findViewById(R.id.etDescription);
        spinnerGenre   = findViewById(R.id.spinnerGenre);

        Button btnBack   = findViewById(R.id.btnBack);
        Button btnSubmit = findViewById(R.id.btnPostGig);

        btnBack.setOnClickListener(v -> finish());

        // Date picker
        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
                btnDate.setText(selectedDate);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time picker
        btnTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                btnTime.setText(selectedTime);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        // Populate genre spinner
        ArrayAdapter<CharSequence> genreAdapter = ArrayAdapter.createFromResource(
                this, R.array.genres, android.R.layout.simple_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        btnSubmit.setOnClickListener(v -> submitGig(session));
    }

    private void submitGig(SessionManager session) {
        String duration    = etDuration.getText().toString().trim();
        String payStr      = etPay.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String genre       = spinnerGenre.getSelectedItem() != null
                ? spinnerGenre.getSelectedItem().toString() : "";

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show(); return;
        }
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show(); return;
        }

        double pay = 0;
        if (!payStr.isEmpty()) {
            try { pay = Double.parseDouble(payStr); }
            catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid pay amount", Toast.LENGTH_SHORT).show(); return;
            }
        }

        final double finalPay = pay;

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            long userId = session.getUserId();

            VenueProfile profile = db.venueProfileDao().getProfileByUserId(userId);
            String venueName = profile != null ? profile.venueName : session.getUsername();
            String location  = profile != null ? profile.location  : "";

            GigListing gig = new GigListing();
            gig.venueUserId  = userId;
            gig.venueName    = venueName;
            gig.location     = location;
            gig.gigDate      = selectedDate;
            gig.gigTime      = selectedTime;
            gig.duration     = duration;
            gig.genreWanted  = genre;
            gig.description  = description;
            gig.payAmount    = finalPay;
            gig.status       = "open";
            gig.createdAt    = System.currentTimeMillis();

            db.gigListingDao().insert(gig);

            runOnUiThread(() -> {
                Toast.makeText(this, "Gig posted!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}