// C21361681 – Michael Traynor
// Inserts user if unique and goes straight to Home
// Sprint 2 update: stores session via SessionManager,
// then redirects new users to profile setup screen

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.User;
import com.fyp.giggy.data.UserDao;
import com.fyp.giggy.utils.SessionManager;
import com.fyp.giggy.utils.ValidationUtils;

public class SignupActivity extends AppCompatActivity {

    private String role = "artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        role = getIntent().getStringExtra("role");
        if (role == null) role = "artist";

        EditText etName     = findViewById(R.id.etName);
        EditText etEmail    = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button   btnCreate  = findViewById(R.id.btnCreate);
        TextView tvChange   = findViewById(R.id.tvChangeRole);

        if ("venue".equals(role)) etName.setHint("Venue Name");

        btnCreate.setOnClickListener(v -> {
            String name  = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString();

            if (name.isEmpty()) {
                etName.setError("Username is required");
                return;
            }
            if (!ValidationUtils.isValidEmail(email)) {
                etEmail.setError("Enter a valid email");
                return;
            }
            if (!ValidationUtils.isValidPassword(pass)) {
                etPassword.setError("Password must be 6+ chars");
                return;
            }

            // Run DB operations off the main thread
            final String finalRole = role;
            new Thread(() -> {
                UserDao dao = AppDatabase.getInstance(this).userDao();

                // Pre-check duplicates for friendly errors
                if (dao.findByName(name) != null) {
                    runOnUiThread(() -> {
                        etName.setError("This username is already taken");
                        etName.requestFocus();
                    });
                    return;
                }
                if (dao.findByEmail(email) != null) {
                    runOnUiThread(() -> {
                        etEmail.setError("An account with this email already exists");
                        etEmail.requestFocus();
                    });
                    return;
                }

                try {
                    long newUserId = dao.insert(new User(name, email, pass, finalRole));

                    // ✅ Sprint 2: save session immediately after account creation
                    SessionManager session = new SessionManager(this);
                    session.createSession(newUserId, name, email, finalRole);

                    runOnUiThread(() -> {
                        // Redirect new user to profile setup instead of Home
                        Intent profileSetup;
                        if ("artist".equals(finalRole)) {
                            profileSetup = new Intent(this, EditArtistProfileActivity.class);
                        } else {
                            profileSetup = new Intent(this, EditVenueProfileActivity.class);
                        }
                        // Clear back stack so user can't go back to signup
                        profileSetup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(profileSetup);
                        finish();
                    });

                } catch (Exception ex) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Account already exists (email or username).",
                                    Toast.LENGTH_LONG).show()
                    );
                }
            }).start();
        });

        tvChange.setOnClickListener(v -> {
            startActivity(new Intent(this, RoleSelectActivity.class));
            finish();
        });
    }
}