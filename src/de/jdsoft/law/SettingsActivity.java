package de.jdsoft.law;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import net.saik0.android.unifiedpreference.UnifiedPreferenceFragment;
import net.saik0.android.unifiedpreference.UnifiedSherlockPreferenceActivity;


public class SettingsActivity extends UnifiedSherlockPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Select theme
        SharedPreferences pref = getSharedPreferences("openlaw", Context.MODE_PRIVATE);
        if( pref.getBoolean("dark_theme", false) ) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }

        setHeaderRes(R.xml.pref_headers);
        setSharedPreferencesName("openlaw");
        setSharedPreferencesMode(Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);

        pref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if( key.equals("dark_theme") ) {
                    Intent intent = getIntent();
                    intent.putExtra("themeChanged", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        // Recreate main activity when themes was changed
        if( getIntent().getBooleanExtra("themeChanged", false) ) {
            Intent intent = new Intent(this, LawListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            super.onBackPressed();
        }
    }

    public static class GeneralFragment extends UnifiedPreferenceFragment {
        public GeneralFragment() {}
    }

    public static class AdvancedFragment extends UnifiedPreferenceFragment {
        public AdvancedFragment() {}
    }

}