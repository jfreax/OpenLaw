package de.jdsoft.law;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockFragment;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;
import com.loopj.android.http.AsyncHttpResponseHandler;
import de.jdsoft.law.data.Cache;
import de.jdsoft.law.helper.TweakedWebView;
import de.jdsoft.law.network.RestClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LawTextFragment extends SherlockFragment {
    public static final String ARG_ITEM_ID = "text_id";
    public static final String ARG_ITEM_SLUG = "law";
    public static final String ARG_ITEM_SHORT = "shortname";
    public static final String ARG_ITEM_LONG = "longname";
    public static final String ARG_NO_HEADLINE = "no_headline";

    private Cache cache = null;
    private long id = 0;
    private String slug = "";

    private TweakedWebView webview = null;
    private LinearLayout loading = null;
    private LinearLayout text_overlay = null;
    private String lawText = "";

    private String background_color;
    private String font_color;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LawTextFragment() {
    }

    public static Fragment newInstance(long id, String slug, String shortName, String longName) {
        LawTextFragment fragment = new LawTextFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, id);
        args.putString(ARG_ITEM_SLUG, slug);
        args.putString(ARG_ITEM_SHORT, shortName);
        args.putString(ARG_ITEM_LONG, longName);
        fragment.setArguments(args);

        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)
                && getArguments().containsKey(ARG_ITEM_SLUG)) {

            id = getArguments().getLong(ARG_ITEM_ID);
            slug = getArguments().getString(ARG_ITEM_SLUG);

            // Set law name in actionbar title
            if (getSherlockActivity() instanceof LawTextActivity) { // short name on phone
                getSherlockActivity().getSupportActionBar().setTitle(getArguments().getString(ARG_ITEM_SHORT));
            } else { // long name on tablet
                getSherlockActivity().getSupportActionBar().setTitle(getArguments().getString(ARG_ITEM_LONG));
            }
        }

        // Get theme id
        int theme = R.style.AppTheme;
        SharedPreferences pref = getSherlockActivity().getSharedPreferences("openlaw", Context.MODE_PRIVATE);
        if (pref.getBoolean("dark_theme", false)) {
            theme = R.style.AppThemeDark;
        }

        if ("dark".equalsIgnoreCase(getSherlockActivity().getIntent().getStringExtra("theme"))) {
            theme = R.style.AppThemeDark;
        }

        // Get webview background color from theme
        TypedArray a = getActivity().getTheme().obtainStyledAttributes(theme, new int[]{android.R.attr.colorForeground});
        int attributeResourceId = a.getResourceId(0, 0);
        background_color = Integer.toHexString(getResources().getColor(attributeResourceId) & 0x00ffffff);
        a.recycle();

        // And font color
        a = getActivity().getTheme().obtainStyledAttributes(theme, new int[]{android.R.attr.textColorPrimary});
        attributeResourceId = a.getResourceId(0, 0);
        font_color = Integer.toHexString(getResources().getColor(attributeResourceId) & 0x00ffffff);
        a.recycle();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_law_text,
                container, false);

        cache = new Cache();

        // Initialize webview
        webview = (TweakedWebView) rootView.findViewById(R.id.text_webview);

        // Hide ugly white scrollbar on API level <= 7
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

//        webview.setLongClickable(false);
//        webview.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return true;
//            }
//        });

        loading = (LinearLayout) rootView.findViewById(R.id.loading);
        text_overlay = (LinearLayout) rootView.findViewById(R.id.text_overlay);


        LoadOrCache();

        return rootView;
    }

    public void StartSelectText() {
        try {
            KeyEvent shiftPressEvent =
                    new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
            shiftPressEvent.dispatch(webview);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private void LoadOrCache() {
        // Try to read from cache
        try {
            if (cache == null || cache.isClosed()) {
                cache.openCache();
            }
            Snapshot snapshot = cache.get(slug + "_" + id);
            if (snapshot != null) {
                lawText = snapshot.getString(0);
                reloadData(false);
                return;
            }
        } catch (IOException e) {
            Log.e(LawTextFragment.class.getName(), "Error while reading cache!");
        }

        // Not in cache, try to read from network
        RestClient.get(getContext(), slug + "/" + id, null, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                Log.i("GetLawText", "onSuccess() Response size: " + response.length());
                if (response.length() == 0) {
                    Log.e(LawTextFragment.class.getName(), "Can't download law " + slug + " " + id);
                    return;
                }

                // Save to cache
                try {
                    if (cache == null || cache.isClosed()) {
                        cache.openCache();
                    }
                    Editor creator = cache.edit(slug + "_" + id);
                    creator.set(0, response);
                    creator.commit();
                    cache.flush();
                } catch (IOException e) {
                    Log.e(LawTextFragment.class.getName(), "Error while writing cache!");
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
        if (isError || getSherlockActivity() == null) {
            return;
        }

        // Only valid text data
        if (lawText.length() <= 5 || lawText.substring(0, 4).contains("%")) {
            // if not valid, try next
            id++;
            LoadOrCache();
            return;
        }


        if (webview != null) {
            String html = "<html>" +
                    "<body bgcolor=\"#" + background_color + "\" text=\"" + font_color + "\">" +
                    lawText + "</body></html>";
            try {
                webview.loadData(URLEncoder.encode(html, "utf-8").replaceAll("\\+", " "), "text/html", "utf-8");
                // Show webview
                webview.setVisibility(View.VISIBLE);
                // Hide loading animation
                loading.setVisibility(View.GONE);
            } catch (UnsupportedEncodingException e) {
                // TODO
            }
        }
    }

    private Context getContext() {
        return getSherlockActivity().getApplicationContext();
    }
}
