// C21361681 – Michael Traynor
// Status/navigation bar colour helper

package com.fyp.giggy.utils;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

public class ThemeUtils {
    public static void darkSystemBars(Activity a, int color) {
        Window w = a.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(color);
            w.setNavigationBarColor(color);
        }
    }
}
