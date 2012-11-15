package de.jdsoft.gesetze;

import java.io.File;
import java.io.IOException;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;
import com.jakewharton.DiskLruCache;

import de.jdsoft.gesetze.data.Cache;

public class LawTextFragment extends SherlockFragment {
	public static final String ARG_ITEM_ID = "text_id";
	public static final String ARG_ITEM_SLUG = "law";

	private Cache cache = null;
	
	private long id = 0;
	private String slug = "";

	
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
		
		WebView webview = (WebView) rootView.findViewById(R.id.text_webview);
		
		Log.e("Webview", "http://gesetze.jdsoft.de/static/"+slug+"/"+id+".html");
		//webview.loadUrl("http://gesetze.jdsoft.de/static/"+slug+"/"+id);
		//webview.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
		//webview.loadData("<div id=\"paddingLR12\"><div><div class=\"jnhtml\"><div><div class=\"jurAbsatz\">Die Deutsche Bundesbank wird erm&#228;chtigt, zum Gedenken an die Deutsche Mark im eigenen Namen im Jahre 2001 eine M&#252;nze in Gold &#252;ber 1 Deutsche Mark (1-DM-Goldm&#252;nze) mit einer Auflage von bis zu einer Million St&#252;ck herauszugeben.</div></div></div></div></div>", "text/html", "UTF-8");
		Log.e("Test", "ok"+webview.getOriginalUrl());
		
		return rootView;
	}
	
	private String LoadOrCache() {
		
		
		return null;
		
	}
}
