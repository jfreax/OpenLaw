package de.jdsoft.law;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.jakewharton.DiskLruCache;
import com.loopj.android.http.AsyncHttpResponseHandler;
import de.jdsoft.law.data.Cache;
import de.jdsoft.law.data.helper.LawHeadline;
import de.jdsoft.law.network.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeadlineComposerAdapter extends BaseAdapter {
    private final Activity activity;
    private final String slug;
    private List<Pair<Integer,String>> headlines = null;
    Cache cache = null;


    public HeadlineComposerAdapter(Activity activity, String slug) {
        this.cache = new Cache();
        this.activity = activity;
        this.slug = slug;
        getHeadlinesRaw();
    }

    protected void finalize() throws Throwable {
        super.finalize();

        if ( cache != null && !cache.isClosed() ) {
            cache.close();
            cache = null;
        }
    }

    private void getHeadlinesRaw() {
        // Try to read from cache
        try {
            if ( cache == null || cache.isClosed() ) {
                cache.openCache();
            }
            DiskLruCache.Snapshot snapshot = cache.get(slug);
            if ( snapshot != null ) {
                makeHeadlines(snapshot.getString(0));
                return;
            }
        } catch (IOException e) {
            Log.e(HeadlineComposerAdapter.class.getName(), "Error while reading cache!");
            Log.e(HeadlineComposerAdapter.class.getName(), e.getCause().getMessage());
        }

        // Not in cache, try to read from network
        RestClient.get(getContext(), slug + "/heads", null, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                Log.d("GetLawHeadlines", "onSuccess() Response size: " + response.length());
                if (response.length() == 0) {
                    Log.e(HeadlineComposerAdapter.class.getName(), "Cannot download law " + slug);
                    return;
                }

                // Save to cache
                try {
                    if (cache == null || cache.isClosed()) {
                        cache.openCache();
                    }
                    DiskLruCache.Editor creator = cache.edit(slug);
                    creator.set(0, response);
                    creator.commit();
                    cache.flush();
                } catch (IOException e) {
                    Log.e(HeadlineComposerAdapter.class.getName(), "Error while reading cache!");
                }

                makeHeadlines(response);
            }

            public void onFailure(Throwable error, String content) {
                makeHeadlines("");
            }
        });
    }

    protected void makeHeadlines(String raw) {
        headlines = new ArrayList<Pair<Integer,String>>();

        if( raw.equals("") ) { // Error while downloading
            headlines.add(new Pair<Integer, String>(1, activity.getString(R.string.error_downloading)));
        } else {
            for ( String line : raw.split("\\r?\\n")) {
                if ( line.contains(":") ) {
                    String[] depthAndText = line.split(":");
                    headlines.add(new Pair<Integer, String>(Integer.parseInt(depthAndText[0]), depthAndText[1]));
                }
            }
        }

        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity.getApplicationContext();
    }

    public int getCount() {
        if ( headlines == null ) {
            return 0;
        }
        return headlines.size();
    }

    public LawHeadline getItem(int position) {
        try {
            return new LawHeadline(headlines.get(position).first, headlines.get(position).second);
        } catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public String getSlug() {
        return slug;
    }
}