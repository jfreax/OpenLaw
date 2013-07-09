package de.jdsoft.law;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;
import com.loopj.android.http.AsyncHttpResponseHandler;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import de.jdsoft.law.data.Cache;
import de.jdsoft.law.helper.TweakedWebView;
import de.jdsoft.law.network.RestClient;

public class LawTextFragment extends SherlockFragment {
    public static final String ARG_ITEM_ID = "text_id";
    public static final String ARG_ITEM_SLUG = "law";

    private Cache cache = null;

    private long id = 0;
    private String slug = "";

    private TweakedWebView webview = null;
    private String lawText = "";


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LawTextFragment() {
    }

    public static Fragment newInstance(long id, String slug) {
        LawTextFragment fragment = new LawTextFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, id);
        args.putString(ARG_ITEM_SLUG, slug);
        fragment.setArguments(args);

        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);

        if ( getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)
                && getArguments().containsKey(ARG_ITEM_SLUG)) {

            id = getArguments().getLong(ARG_ITEM_ID);
            slug = getArguments().getString(ARG_ITEM_SLUG);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_law_text,
                container, false);

//        mAdapter = new HeadlinePagerAdapter(this.getFragmentManager(), this.getSherlockActivity(), slug);
//        mPager = (ViewPager)rootView.findViewById(R.id.pager);
//        mPager.setAdapter(mAdapter);
//        mIndicator = (TabPageIndicator)rootView.findViewById(R.id.indicator);
//        mIndicator.setViewPager(mPager);


        cache = new Cache();
        webview = (TweakedWebView) rootView.findViewById(R.id.text_webview);
        LoadOrCache();

        return rootView;
    }

    private void LoadOrCache() {
        // Try to read from cache
        try {
            if ( cache == null || cache.isClosed() ) {
                cache.openCache();
            }
            Snapshot snapshot = cache.get(slug+"_"+id);
            if ( snapshot != null ) {
                lawText = snapshot.getString(0);
                reloadData(false);
                return;
            }
        } catch (IOException e) {
            Log.e(LawTextFragment.class.getName(), e.getCause().getMessage());
        }

        // Not in cache, try to read from network
        RestClient.get(getContext(), slug+"/"+id, null, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                Log.i("GetLawText", "onSuccess() Response size: "+response.length());
                if ( response.length() == 0 ) {
                    Log.e(LawTextFragment.class.getName(), "Can't download law " + slug + " " + id);
                    return;
                }

                // Save to cache
                try {
                    if ( cache == null || cache.isClosed() ) {
                        cache.openCache();
                    }
                    Editor creator = cache.edit(slug+"_"+id);
                    creator.set(0, response);
                    creator.commit();
                    cache.flush();
                } catch (IOException e) {
                    Log.e(LawTextFragment.class.getName(), "Error while reading cache!");
                }

                lawText = response;
                reloadData(false);
            }

            public void onFailure(Throwable error, String content) {
                // TODO handle error
                reloadData(true);
            }
        });

        return;
    }

    private void reloadData(boolean isError) {
        if( isError || getSherlockActivity() == null ) {
            return;
        }

        // Only valid text data
        if( lawText.length() <= 5 || lawText.substring(0, 4).contains("%") ) {
            // if not valid, try next
            id++;
            LoadOrCache();
            return;
        }

        // Select correct item in listview to visualize current selected
        if( getActivity() instanceof LawListActivity) { // Only in two pane mode
            ((LawListActivity)getActivity()).headlineFragment.getListView().setItemChecked((int)id-1, true);
        } else {
//            PageIndicator mIndicator = ((LawTextActivity)getSherlockActivity()).mIndicator;
//            mIndicator.setCurrentItem((int)id-1);
        }

        // Disable progress bar
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);

        if ( webview != null ) {
            webview.loadData("<html><body bgcolor=\"#eee\">" + lawText + "</body></html>", "text/html", "UTF-8");
        }
    }

    private Context getContext() {
        return getSherlockActivity().getApplicationContext();
    }

//    class HeadlinePagerAdapter extends FragmentPagerAdapter {
//        private HeadlineComposerAdapter mAdapter;
//
//        public HeadlinePagerAdapter(FragmentManager fm, Activity activity, String slug) {
//            super(fm);
//
//            mAdapter = new HeadlineComposerAdapter(activity, slug);
//
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return LawTextFragment.newInstance(position, mAdapter.getSlug());
//        }
//
//        @Override
//        public int getCount() {
//            return mAdapter.getCount();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mAdapter.getItem(position).headline;
//        }
//    }
}
