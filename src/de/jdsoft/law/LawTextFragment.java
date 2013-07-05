package de.jdsoft.law;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.jdsoft.law.data.Cache;
import de.jdsoft.law.network.RestClient;

public class LawTextFragment extends SherlockFragment {
	public static final String ARG_ITEM_ID = "text_id";
	public static final String ARG_ITEM_SLUG = "law";

	private Cache cache = null;
	
	private long id = 0;
	private String slug = "";
	
	private WebView webview = null;
	private String lawText = "";

	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LawTextFragment() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
		
		cache = new Cache();
		webview = (WebView) rootView.findViewById(R.id.text_webview);		
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
				reloadWebview();
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
				reloadWebview();
            }
            
            public void onFailure(Throwable error, String content) {
            	// TODO
            	reloadWebview();
            }
        });
		
		return;
	}
	
	private void reloadWebview() {
		if ( webview != null ) {
			webview.loadData("<html><body bgcolor=\"#eee\">" + lawText + "</body></html>", "text/html", "UTF-8");
		}
	}
	
	private Context getContext() {
		return getActivity().getApplicationContext();
	}
}
