// C21361681 – Michael Traynor
// DB-backed login that goes to Home on success
// Sprint 2 update: stores session via SessionManager

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
import com.fyp.giggy.utils.SessionManager;
import com.fyp.giggy.utils.ThemeUtils;
import com.fyp.giggy.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ThemeUtils.darkSystemBars(this, getColor(R.color.giggy_black));

        session = new SessionManager(this);

        // If user is already logged in, skip straight to Home
        if (session.isLoggedIn()) {
            goHome();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Update hint to "Username or Email"
        etEmail.setHint("Username or Email");

        Button btnLogin   = findViewById(R.id.btnLogin);
        Button btnGoSignup = findViewById(R.id.btnGoSignup);
        TextView tvForgot  = findViewById(R.id.tvForgot);

        btnLogin.setOnClickListener(v -> doLogin());
        btnGoSignup.setOnClickListener(v -> startActivity(new Intent(this, RoleSelectActivity.class)));
        tvForgot.setOnClickListener(v ->
                Toast.makeText(this, "Sprint 2: Forgot Password not implemented", Toast.LENGTH_SHORT).show()
        );
    }

    private void doLogin() {
        String identifier = etEmail.getText().toString().trim();
        String pass       = etPassword.getText().toString();

        // Allow either valid email OR non-empty string (username)
        if (identifier.isEmpty()) {
            etEmail.setError("Enter username or email");
            return;
        }
        // Removed strict email validation to allow username login
        if (!ValidationUtils.isValidPassword(pass)) {
            etPassword.setError("Password must be 6+ chars");
            return;
        }

        // Run DB query off the main thread
        new Thread(() -> {
            User u = AppDatabase.getInstance(this).userDao().login(identifier, pass);

            runOnUiThread(() -> {
                if (u == null) {
                    Toast.makeText(this, "Invalid credentials. Sign up first.", Toast.LENGTH_LONG).show();
                } else {
                    // ✅ Sprint 2: save session so profile screens know who is logged in
                    session.createSession(u.id, u.name, u.email, u.role);
                    goHome();
                }
            });
        }).start();
    }

    private void goHome() {
        Intent home = new Intent(this, HomeActivity.class);
        home.putExtra("name",  session.getUsername());
        home.putExtra("email", session.getEmail());
        home.putExtra("role",  session.getRole());
        startActivity(home);
        finish();
    }
}