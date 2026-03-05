// C21361681 – Michael Traynor
// Inserts user if unique and goes straight to Home

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
import com.fyp.giggy.utils.ValidationUtils;

public class SignupActivity extends AppCompatActivity {

    private String role = "artist";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        role = getIntent().getStringExtra("role");
        if (role == null) role = "artist";

        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnCreate = findViewById(R.id.btnCreate);
        TextView tvChange = findViewById(R.id.tvChangeRole);

        if ("venue".equals(role)) etName.setHint("Venue Name");

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString();

            if (name.isEmpty()) { etName.setError("Username is required"); return; }
            if (!ValidationUtils.isValidEmail(email)) { etEmail.setError("Enter a valid email"); return; }
            if (!ValidationUtils.isValidPassword(pass)) { etPassword.setError("Password must be 6+ chars"); return; }

            UserDao dao = AppDatabase.get(this).userDao();

            // Pre-check duplicates for friendly errors
            if (dao.findByName(name) != null) {
                etName.setError("This username is already taken");
                etName.requestFocus();
                return;
            }
            if (dao.findByEmail(email) != null) {
                etEmail.setError("An account with this email already exists");
                etEmail.requestFocus();
                return;
            }

            try {
                dao.insert(new User(name, email, pass, role));
            } catch (Exception ex) {
                Toast.makeText(this, "Account already exists (email or username).", Toast.LENGTH_LONG).show();
                return;
            }

            // Success go straight to Home
            Intent home = new Intent(this, HomeActivity.class);
            home.putExtra("name", name);
            home.putExtra("email", email);
            home.putExtra("role", role);
            startActivity(home);
            finish();
        });

        tvChange.setOnClickListener(v -> {
            startActivity(new Intent(this, RoleSelectActivity.class));
            finish();
        });
    }
}
