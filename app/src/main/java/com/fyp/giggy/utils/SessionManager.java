// C21361681 – Michael Traynor
// Utility: SessionManager – stores logged-in user data in SharedPreferences

package com.fyp.giggy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME    = "GiggySession";
    private static final String KEY_USER_ID  = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL    = "email";
    private static final String KEY_ROLE     = "role";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Call this after successful login or signup
    public void createSession(long userId, String username, String email, String role) {
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    // Returns "artist" or "venue"
    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public boolean isArtist() {
        return "artist".equalsIgnoreCase(getRole());
    }

    public boolean isVenue() {
        return "venue".equalsIgnoreCase(getRole());
    }

    // Call on logout
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}