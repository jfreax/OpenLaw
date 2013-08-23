package de.jdsoft.law;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import de.jdsoft.law.LawListFragment.Callbacks;

/**
 * An activity representing a single Book detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link LawListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link LawHeadlineFragment}.
 */
public class LawHeadlineActivity extends SherlockFragmentActivity implements Callbacks, ActionBar.OnNavigationListener {

    private LawHeadlineFragment fragment;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
        // Select theme
        SharedPreferences pref =  getSharedPreferences("openlaw", Context.MODE_PRIVATE);
        if( pref.getBoolean("dark_theme", false) ) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }

		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_law_headline);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(LawHeadlineFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(LawHeadlineFragment.ARG_ITEM_ID));
			fragment = new LawHeadlineFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.law_headline_container, fragment).commit();
		}

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
//			NavUtils.navigateUpTo(this,
//					new Intent(this, LawListActivity.class));
//            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            onBackPressed();
            return true;
		}
		return false;
	}

	public void onItemSelected(String id) {
	}
	
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return true;
	}

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onResume() {
        // Fragments exists only after 2nd call, so we are here, because
        // someone pressed back on text view on a phone!
        // If there is no headline, we have to go back one more time.
        if( fragment != null && fragment.getListAdapter().getCount() <= 1 ) {
            onBackPressed();
        }
        super.onResume();
    }

}
