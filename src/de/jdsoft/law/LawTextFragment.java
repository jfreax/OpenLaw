package de.jdsoft.law;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private Cache cache = null;
    private long id = 0;
    private String slug = "";
    private TweakedWebView webview = null;
    private LinearLayout loading = null;
    private LinearLayout text_overlay = null;
    private String lawText = "";


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LawTextFragment() {
    }

    public static Fragment newInstance(long id, String slug, String shortName) {
        LawTextFragment fragment = new LawTextFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, id);
        args.putString(ARG_ITEM_SLUG, slug);
        args.putString(ARG_ITEM_SHORT, shortName);
        fragment.setArguments(args);

        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)
                && getArguments().containsKey(ARG_ITEM_SLUG)) {

            id = getArguments().getLong(ARG_ITEM_ID);
            slug = getArguments().getString(ARG_ITEM_SLUG);

            getSherlockActivity().getSupportActionBar().setTitle(getArguments().getString(ARG_ITEM_SHORT));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_law_text,
                container, false);

        cache = new Cache();

        // Initialize webview
        webview = (TweakedWebView) rootView.findViewById(R.id.text_webview);
        webview.setLongClickable(false);
        webview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        loading = (LinearLayout) rootView.findViewById(R.id.loading);
        text_overlay = (LinearLayout) rootView.findViewById(R.id.text_overlay);

        // Set touch listener to show button overlay
        rootView.setOnTouchListener(new View.OnTouchListener() {

            public AnimRunnable fadeOutRunner = null;
            public Handler mHandler = new Handler();
            public boolean isFaded = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (fadeOutRunner == null) {
                    if (!isFaded) {
                        final Animation animFadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
                        text_overlay.setAnimation(animFadeIn);
                        text_overlay.setVisibility(View.VISIBLE);
                        isFaded = true;
                    }

                    mHandler.postDelayed(fadeOutRunner = new AnimRunnable() {
                        @Override
                        public void run() {
                            if (isStop()) {
                                return;
                            }
                            final Animation animFadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
                            text_overlay.setAnimation(animFadeOut);
                            text_overlay.setVisibility(View.GONE);
                            fadeOutRunner = null;
                            isFaded = false;
                        }
                    }, 3000); // Todo variable
                } else {
                    mHandler.removeCallbacksAndMessages(fadeOutRunner);
                    fadeOutRunner.stop();
                    fadeOutRunner = null;
                    this.onTouch(v, event);
                }

                return false;
            }
        });

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

        // Select correct item in listview to visualize current selected
        if (getSherlockActivity() instanceof LawListActivity) { // Only in two pane mode
            if (((LawListActivity) getSherlockActivity()).headlineFragment == null) { // == activity cancelled
                return;
            }
            ((LawListActivity) getSherlockActivity()).headlineFragment.getListView().setItemChecked((int) id, true);
        }

        if (webview != null) {
            String html = "<html><body bgcolor=\"#eee\">" + lawText + "</body></html>";
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

    private class AnimRunnable implements Runnable {
        private boolean stop = false;

        @Override
        public void run() {
        }

        public void stop() {
            stop = true;
        }

        public boolean isStop() {
            return stop;
        }
    }
}
