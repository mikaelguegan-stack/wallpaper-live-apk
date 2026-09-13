package com.mikael.wallpaper;

import android.app.Activity;
import android.os.Bundle;

public class WallpaperSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish(); // Ferme immédiatement pour valider le sélecteur sans bug
    }
}
