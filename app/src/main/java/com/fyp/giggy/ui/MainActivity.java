// C21361681 – Michael Traynor
// Launcher that routes to Login

package com.fyp.giggy.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.giggy.R;
import com.fyp.giggy.utils.ThemeUtils;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeUtils.darkSystemBars(this, getColor(R.color.giggy_black));

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
