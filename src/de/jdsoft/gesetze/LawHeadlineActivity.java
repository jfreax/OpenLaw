package de.jdsoft.gesetze;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import de.jdsoft.gesetze.LawListFragment.Callbacks;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;

/**
 * An activity representing a single Book detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link LawListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link LawHeadlineFragment}.
 */
public class LawHeadlineActivity extends SherlockFragmentActivity implements Callbacks, ActionBar.OnNavigationListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_law_headline);

		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
			LawHeadlineFragment fragment = new LawHeadlineFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.law_headline_container, fragment).commit();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this,
					new Intent(this, LawListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	public void onItemSelected(String id) {
//		Log.e("onItemSelected", id);
//
//		
//		FragmentTransaction ft = getFragmentManager().beginTransaction();  
//		ft.hide(getFragmentManager().getFragment(null, "test")); 
//		ft.commit(); 
	}
	
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return true;
	}
}
