package de.jdsoft.gesetze;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;

public class LawTextFragment extends SherlockFragment {
	public static final String ARG_ITEM_ID = "text_id";

	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LawTextFragment() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_law_text,
				container, false);

		WebView webview = (WebView) rootView.findViewById(R.id.text_webview);
		
		webview.loadUrl("http://www.google.de");
		Log.e("Test", "ok"+webview.getOriginalUrl());
		
		return rootView;
	}
}
