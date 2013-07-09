package de.jdsoft.law;

import java.io.IOException;
import java.util.ArrayList;

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
import com.actionbarsherlock.app.SherlockListFragment;
import com.foound.widget.AmazingAdapter;
import com.foound.widget.AmazingListView;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;
import com.loopj.android.http.AsyncHttpResponseHandler;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import de.jdsoft.law.data.Cache;
import de.jdsoft.law.helper.CallbackInterface;
import de.jdsoft.law.helper.CallerInterface;
import de.jdsoft.law.helper.TweakedWebView;
import de.jdsoft.law.network.RestClient;

public class LawTextFragment extends SherlockFragment implements CallerInterface {
	public static final String ARG_ITEM_ID = "text_id";
	public static final String ARG_ITEM_SLUG = "law";

	private Cache cache = null;
	
	private long current_id = 0;
	private String slug = "";
	
	private TweakedWebView webview = null;

    private TextsAdapter adapter;
    private AmazingListView listview;


    /**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LawTextFragment() {
	}

//    public static Fragment newInstance(long id, String slug) {
//        LawTextFragment fragment = new LawTextFragment();
//
//        Bundle args = new Bundle();
//        args.putLong(ARG_ITEM_ID, id);
//        args.putString(ARG_ITEM_SLUG, slug);
//        fragment.setArguments(args);
//
//        return fragment;
//    }

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);

        if ( getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)
				&& getArguments().containsKey(ARG_ITEM_SLUG)) {

            current_id = getArguments().getLong(ARG_ITEM_ID);
            slug = getArguments().getString(ARG_ITEM_SLUG);
        }
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_law_text,
				container, false);

        // Law text cache
        cache = new Cache();

        // Adapter
        listview = (AmazingListView) rootView.findViewById(R.id.list);
        adapter = new TextsAdapter();
        adapter.setInitialPage((int) current_id);
        listview.setLoadingView(inflater.inflate(R.layout.text_loading_view, null));
        listview.setAdapter(adapter);

//        adapter.pag
//        adapter.resetPage();
//        adapter.
//        adapter.notifyDataSetChanged();
//        listview.notify();
//        adapter.notifyMayHaveMorePages();
//        adapter.nextPage();
//        adapter.notifyDataSetChanged();
//        listview.setSelection((int)current_id);
//        listview.setItemChecked((int)current_id, true);
        listview.setFastScrollEnabled(true);
        LoadOrCache(adapter, 1L);

//        mAdapter = new HeadlinePagerAdapter(this.getFragmentManager(), this.getSherlockActivity(), slug);
//        mPager = (ViewPager)rootView.findViewById(R.id.pager);
//        mPager.setAdapter(mAdapter);
//        mIndicator = (TabPageIndicator)rootView.findViewById(R.id.indicator);
//        mIndicator.setViewPager(mPager);

		webview = (TweakedWebView) rootView.findViewById(R.id.text_webview);
//		LoadOrCache();

		return rootView;
	}
	
	private void LoadOrCache(final CallbackInterface callback, final long id) {
        final LawTextFragment self = this;

    	// Try to read from cache
    	try {
    		if ( cache == null || cache.isClosed() ) {
    			cache.openCache();
    		}
			Snapshot snapshot = cache.get(slug+"_"+id);
			if ( snapshot != null ) {
//				lawText = snapshot.getString(0);
//				reloadData(false);
                callback.onFinish(this, snapshot.getString(0));
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

//				lawText = response;
//				reloadData(false);
                callback.onFinish(self, response);
            }
            
            public void onFailure(Throwable error, String content) {
            	// TODO handle error
//            	reloadData(true);
                callback.onFinish(self, "");
            }
        });
		
		return;
	}
	
//	private void reloadData(boolean isError) {
//        if( isError || getSherlockActivity() == null ) {
//            return;
//        }
//
//        // Only valid text data
//        if( lawText.length() <= 5 || lawText.substring(0, 4).contains("%") ) {
//            // if not valid, try next
//            id++;
//            LoadOrCache();
//            return;
//        }
//
//        // Select correct item in listview to visualize current selected
//        if( getActivity() instanceof LawListActivity) { // Only in two pane mode
//            ((LawListActivity)getActivity()).headlineFragment.getListView().setItemChecked((int)id-1, true);
//        } else {
////            PageIndicator mIndicator = ((LawTextActivity)getSherlockActivity()).mIndicator;
////            mIndicator.setCurrentItem((int)id-1);
//        }
//
//        // Disable progress bar
//        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
//
//		if ( webview != null ) {
//			webview.loadData("<html><body bgcolor=\"#eee\">" + lawText + "</body></html>", "text/html", "UTF-8");
//		}
//	}
	
	private Context getContext() {
		return getSherlockActivity().getApplicationContext();
	}

    class TextsAdapter extends AmazingAdapter implements CallbackInterface {

        private ArrayList<String> list;

        public TextsAdapter() {
            super();
            list = new ArrayList<String>();
        }

        @Override
        protected void onNextPageRequested(int page) {
            Log.d(TAG, "Got onNextPageRequested page=" + page);

            LoadOrCache(this, page);
        }

        @Override
        protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
        }

        @Override
        public View getAmazingView(int position, View convertView, ViewGroup parent) {
            View res = convertView;
            if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.item_text, null);

            // we don't have headers, so hide it
//            res.findViewById(R.id.header).setVisibility(View.GONE);

            WebView lName = (WebView) res.findViewById(R.id.text_webview);

//            Composer composer = getItem(position);
            lName.loadData(getItem(position), "text/html", "UTF-8");

            return res;

        }

        @Override
        public void configurePinnedHeader(View header, int position, int alpha) {
        }

        @Override
        public int getPositionForSection(int section) {
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public void onFinish(CallerInterface caller) {
        }

        @Override
        public void onFinish(CallerInterface caller, String result) {
            list.add(result);
            nextPage();
            notifyDataSetChanged();
            if( result.isEmpty() ) {
                notifyNoMorePages();
            } else {
                notifyMayHaveMorePages();
            }
        }
    }
}
