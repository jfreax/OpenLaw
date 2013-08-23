package de.jdsoft.law;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import de.jdsoft.law.data.LawSectionList;
import de.jdsoft.law.database.Connector;

import java.util.LinkedList;
import java.util.List;

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

    private static final int OPTION_SEARCH = 3;


    public static Connector db;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private LinearLayout mDrawerView;


    /**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
    protected boolean mTwoPane;
	private LawHeadlineFragment headlineFragment = null;
    private LawListFragment lawListFragment;


    public LawListActivity() {
        super();


    }

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        // Select theme
        SharedPreferences pref =  getSharedPreferences("openlaw", Context.MODE_PRIVATE);
        if( pref.getBoolean("dark_theme", false) ) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }

        // Setup database
        if( db == null ) {
            db = new Connector(getApplicationContext());
        }

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_law_list);

        lawListFragment =
                ((LawListFragment) getSupportFragmentManager().findFragmentById(R.id.law_list));

        // Two pane mode
		if (findViewById(R.id.law_headline_container ) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			lawListFragment.setActivateOnItemClick(true);
			
			ListView listview = lawListFragment.getListView();

            // Disable standard orange background selection
            listview.setCacheColorHint(android.R.color.transparent);
            listview.setSelector(android.R.color.transparent);
            listview.setScrollingCacheEnabled(false);

			listview.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_INSET);

            // Do not show loading animation on start
            LinearLayout loading = (LinearLayout)findViewById(R.id.loading);
            loading.setVisibility(View.GONE);
        }
		
		final com.actionbarsherlock.app.ActionBar actionbar = getSupportActionBar();

        // Locate ListView in drawer_main.xml
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerView = (LinearLayout) findViewById(R.id.left_drawer_layout);

        // Set drawer adapter
        final DrawerAdapter adapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Back to main list
                if( headlineFragment != null && !headlineFragment.isCollapsed ) {
                    headlineFragment.fadeOut();
                }

                switch (adapter.getItem(position).type) {
                    case DrawerAdapter.ID_GERMANY:
                        if( !lawListFragment.adapter.isFinish ) {
                            LawSectionList sectionDB = new LawSectionList(LawSectionList.TYPE_ALL);
                            sectionDB.execute(lawListFragment.adapter);
                        } else {
                            lawListFragment.setListAdapter(lawListFragment.adapter);
                        }
                        break;
                    case DrawerAdapter.ID_EU:
                        Toast.makeText(LawListActivity.this, "Coming soon.", Toast.LENGTH_SHORT).show();

                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawer(mDrawerView);
            }
        });

        // Locate drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Show the Up button in the action bar.
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);

        // Show title
		actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setTitle(getResources().getString(R.string.title_law));
	}


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

	
    @SuppressLint("AlwaysShowAction")
	public boolean onCreateOptionsMenu(final Menu menu) {
        //Used to put dark icons on light action bar
        //boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;
    	boolean isLight = true;

    	// Search button
        menu.add(0, OPTION_SEARCH, 3, R.string.search)
            .setIcon(isLight ? R.drawable.ic_search_inverse : R.drawable.ic_search)
            .setActionView(R.layout.collapsible_search)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        final EditText search = (EditText)menu.getItem(0).getActionView();
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                final InputMethodManager imm =
                        (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if(!queryTextFocused) {
                    // Reset search
                    search.setText("");
                    // Hide keyboard
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                } else {
                    // show keyboard
                    imm.showSoftInput(view, 0);
                }
            }
        });

        return true;
    }

	/**
	 * Callback method from {@link LawListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	public void onItemSelected(final String id) {
		if (mTwoPane) {
            if( headlineFragment != null ) {
                // Fade in and fade out animation
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                        headlineFragment.getListView(),
                        "alpha", 0.f
                ).setDuration(50);

                fadeOut.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        headlineFragment.update(Integer.parseInt(id));
                        ObjectAnimator.ofFloat(
                                headlineFragment.getListView(),
                                "alpha", 1.f
                        ).setDuration(80).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                fadeOut.start();
            } else {

                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                Bundle arguments = new Bundle();
                arguments.putString(LawHeadlineFragment.ARG_ITEM_ID, id);
                headlineFragment = new LawHeadlineFragment();
                headlineFragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.law_headline_container, headlineFragment).commit();
            }

		} else {

            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, LawHeadlineActivity.class);
            detailIntent.putExtra(LawHeadlineFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
	}


	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return true;
	}


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawerView)) {
                    mDrawerLayout.closeDrawer(mDrawerView);
                } else {
                    mDrawerLayout.openDrawer(mDrawerView);
                }
                return true;
            case OPTION_SEARCH:
                // Go back to law list in two pane mode if necessary
                if( headlineFragment != null && !headlineFragment.isCollapsed ) {
                    headlineFragment.fadeOut();
                }

                EditText search = (EditText) item.getActionView();
                search.addTextChangedListener(searchTextWatcher);
                return true;
        }
        return false;
    }


    // Handle button clicks
    public void clickDrawerSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void clickDrawerFavorites(View view) {
        if( headlineFragment != null && !headlineFragment.isCollapsed ) {
            headlineFragment.fadeOut();
        }

        LawSectionList sectionDB = new LawSectionList(LawSectionList.TYPE_FAV);
        sectionDB.execute(lawListFragment.adapterFavs);

        if( lawListFragment.adapterFavs.isFinish ) {
            lawListFragment.setListAdapter(lawListFragment.adapterFavs);
        }

        mDrawerLayout.closeDrawer(mDrawerView);
    }


    private TextWatcher searchTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            int x = 5;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LawListFragment fragment = ((LawListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.law_list));
            ((LawListFragment.SectionComposerAdapter)fragment.getListAdapter()).getFilter().filter(s);
        }
    };


    static private class DrawerAdapter extends BaseAdapter {
        public static final int ID_EU = 1;
        public static final int ID_GERMANY = 2;

        static private class Entry {
            public Entry(Drawable icon, int count, String text, int type) {
                this.icon = icon;
                this.count = count;
                this.text = text;
                this.type = type;
            }

            public Drawable icon;
            public int count;
            public String text;
            public final int type;
        }

        static class ViewHolder {
            public TextView text;
            public TextView count;
            public ImageView icon;
        }

        private final LayoutInflater inflater;
        private final List<Entry> entries = new LinkedList<Entry>();

        public DrawerAdapter(Activity activity) {
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            entries.add( new Entry(
                    activity.getResources().getDrawable(R.drawable.flag_eu),
                    10,
                    activity.getString(R.string.eu),
                    ID_EU));

            entries.add( new Entry(
                    activity.getResources().getDrawable(R.drawable.flag_germany),
                    10,
                    activity.getString(R.string.germany),
                    ID_GERMANY));
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Entry getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_drawer, null);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            Entry item = getItem(position);
            holder.text.setText(item.text);
            holder.icon.setImageDrawable(item.icon);


            return convertView;
        }
    }
}
