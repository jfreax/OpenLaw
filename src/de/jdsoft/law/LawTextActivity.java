package de.jdsoft.law;

import android.app.Activity;
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

    protected void onCreate(Bundle savedInstanceState) {
        // Select theme
        if ("dark".equalsIgnoreCase( getIntent().getStringExtra( "theme" ))) {
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
        int selectID = (int)getIntent().getLongExtra(LawTextFragment.ARG_ITEM_ID, 0L);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(selectID);

        mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager, selectID);

        mIndicator.setCurrentItem(selectID);


        // Show the Up button in the action bar.
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    class HeadlinePagerAdapter extends FragmentPagerAdapter {
        private HeadlineComposerAdapter mAdapter;

        public HeadlinePagerAdapter(FragmentManager fm, Activity activity, String slug) {
            super(fm);

            mAdapter = new HeadlineComposerAdapter(activity, slug) {
                @Override
                protected void makeHeadlines(String raw) {
                    super.makeHeadlines(raw);

                    // Set position after loading all headlines
                    if( mIndicator != null ) {
                        int selectID = (int)getIntent().getLongExtra(LawTextFragment.ARG_ITEM_ID, 0L);
                        mIndicator.setCurrentItem(selectID);
                    }
                }
            };

        }

        @Override
        public Fragment getItem(int position) {
            return LawTextFragment.newInstance(position, mAdapter.getSlug(), lawShortName);
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
