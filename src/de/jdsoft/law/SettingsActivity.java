package de.jdsoft.law;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import net.saik0.android.unifiedpreference.UnifiedPreferenceFragment;
import net.saik0.android.unifiedpreference.UnifiedSherlockPreferenceActivity;


public class SettingsActivity extends UnifiedSherlockPreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Select theme
        SharedPreferences pref =  getSharedPreferences("openlaw", Context.MODE_PRIVATE);
        if( pref.getBoolean("dark_theme", false) ) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }


        setHeaderRes(R.xml.pref_headers);
        setSharedPreferencesName("openlaw");
        setSharedPreferencesMode(Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
    }

    public static class GeneralFragment extends UnifiedPreferenceFragment {}

    public static class AdvancedFragment extends UnifiedPreferenceFragment {}

}