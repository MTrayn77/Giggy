// C21361681 – Michael Traynor
// Basic email/password checks

package com.fyp.giggy.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String s) {
        return !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    public static boolean isValidPassword(String s) {
        return s != null && s.length() >= 6;
    }
}
