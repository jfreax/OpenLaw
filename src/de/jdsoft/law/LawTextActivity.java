package de.jdsoft.law;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;

public class LawTextActivity extends SherlockFragmentActivity {
    ViewPager mPager;
    public PageIndicator mIndicator;
    private HeadlinePagerAdapter mAdapter;

    private String lawShortName = "";
    private String lawLongName = "";

    protected void onCreate(Bundle savedInstanceState) {
        // Select theme
        SharedPreferences pref = getSharedPreferences("openlaw", Context.MODE_PRIVATE);
        if (pref.getBoolean("dark_theme", false)) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_law_text);

        mAdapter = new HeadlinePagerAdapter(this.getSupportFragmentManager(),
                this,
                getIntent().getStringExtra(LawTextFragment.ARG_ITEM_SLUG));

        lawShortName = getIntent().getStringExtra(LawTextFragment.ARG_ITEM_SHORT);
        lawLongName = getIntent().getStringExtra(LawTextFragment.ARG_ITEM_LONG);
        int selectID = (int) getIntent().getLongExtra(LawTextFragment.ARG_ITEM_ID, 0L);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(selectID);

        mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager, selectID);

        mIndicator.setCurrentItem(selectID);


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
//			Bundle arguments = new Bundle();
//			arguments.putLong(LawTextFragment.ARG_ITEM_ID, getIntent().getLongExtra(LawTextFragment.ARG_ITEM_ID, 0L));
//            arguments.putString(LawTextFragment.ARG_ITEM_SLUG, getIntent().getStringExtra(LawTextFragment.ARG_ITEM_SLUG));
//			LawTextFragment fragment = new LawTextFragment();
//			fragment.setArguments(arguments);
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.law_text_container, fragment).commit();
        }
    }

    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

        return false;
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    class HeadlinePagerAdapter extends FragmentPagerAdapter {
        private HeadlineComposerAdapter mAdapter;

        public HeadlinePagerAdapter(FragmentManager fm, Activity activity, String slug) {
            super(fm);

            mAdapter = new HeadlineComposerAdapter(activity) {
                @Override
                protected void makeHeadlines(String raw) {
                    super.makeHeadlines(raw);

                    // Set position after loading all headlines
                    if (mIndicator != null) {
                        int selectID = (int) getIntent().getLongExtra(LawTextFragment.ARG_ITEM_ID, 0L);
                        mIndicator.setCurrentItem(selectID);
                    }
                }
            };
            mAdapter.initialize(slug);

        }

        @Override
        public Fragment getItem(int position) {
            return LawTextFragment.newInstance(position, mAdapter.getSlug(), lawShortName, lawLongName);
        }

        @Override
        public int getCount() {
            return mAdapter.getCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mAdapter.getItem(position).headline;
        }
    }

}
