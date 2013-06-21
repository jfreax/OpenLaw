package de.jdsoft.law;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * An activity representing a list of Books. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link LawHeadlineActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link LawListFragment} and the item details (if present) is a
 * {@link LawHeadlineFragment}.
 * <p>
 * This activity also implements the required {@link LawListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class LawListActivity extends SherlockFragmentActivity implements
		LawListFragment.Callbacks, ActionBar.OnNavigationListener {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	
	public LawHeadlineFragment headlineFragment = null;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_law_list);

		if (findViewById(R.id.law_headline_container ) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			LawListFragment lawListFragment = ((LawListFragment) getSupportFragmentManager().findFragmentById(
					R.id.law_list));
			lawListFragment.setActivateOnItemClick(true);
			
			ListView listview = lawListFragment.getListView();
			
			listview.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
			listview.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_INSET);
			listview.setFastScrollAlwaysVisible(true);
		}
		
		com.actionbarsherlock.app.ActionBar actionbar = getSupportActionBar();
		
		// Hide title
		actionbar.setDisplayShowTitleEnabled(false);
		
		// Show list menu
        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionbar.setListNavigationCallbacks(list, this);
        
		// If exposing deep links into your app, handle intents here.
	}
	
	
    @SuppressLint("AlwaysShowAction")
	public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        //boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;
    	boolean isLight = true;

    	// Search button
        menu.add(R.string.search)
            .setIcon(isLight ? R.drawable.ic_search_inverse : R.drawable.ic_search)
            .setActionView(R.layout.collapsible_search)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        
        // Settings button
        menu.add(R.string.settings)
        	.setIcon(isLight ? R.drawable.ic_action_settings : R.drawable.ic_action_settings_inverse)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

	/**
	 * Callback method from {@link LawListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(LawHeadlineFragment.ARG_ITEM_ID, id);
			LawHeadlineFragment fragment = new LawHeadlineFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.law_headline_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, LawHeadlineActivity.class);
			detailIntent.putExtra(LawHeadlineFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	public boolean isTwoPane() {
		return mTwoPane;
	}
	
    public void onBackPressed() {
    	if ( isTwoPane() && headlineFragment != null && !headlineFragment.isCollapsed ) {
    		headlineFragment.fadeOut();
    	} else {
    		super.onBackPressed();
    	}
    }
}
