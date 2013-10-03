package de.jdsoft.law;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
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
    private final Context context;
    private String slug;
    private List<LawHeadline> headlines = null;
    Cache cache = null;

    private ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();


    public HeadlineComposerAdapter(Context context) {
        this.cache = new Cache();
        this.context = context;
    }

    public void initialize(String slug) {
        this.slug = slug;
        getHeadlinesRaw();
    }

    protected void finalize() throws Throwable {
        super.finalize();

        if (cache != null && !cache.isClosed()) {
            cache.close();
            cache = null;
        }
    }

    private void getHeadlinesRaw() {
        // Try to read from cache
        try {
            if (cache == null || cache.isClosed()) {
                cache.openCache();
            }
            if (slug == "") { // Work around when database is blocked
                return;
            }

            DiskLruCache.Snapshot snapshot = cache.get(slug);
            if (snapshot != null) {
                makeHeadlines(snapshot.getString(0));
                return;
            }
        } catch (IOException e) {
            Log.e(HeadlineComposerAdapter.class.getName(), "Error while reading cache!");
            Log.e(HeadlineComposerAdapter.class.getName(), e.getCause().getMessage());
        }

        // Not in cache, try to read from network
        RestClient.get(getContext(), slug, null, new AsyncHttpResponseHandler() {
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
                    Log.e(HeadlineComposerAdapter.class.getName(), "Error while writing cache!");
                }

                makeHeadlines(response);
            }

            public void onFailure(Throwable error, String content) {
                makeHeadlines("");
            }
        });
    }

    protected void makeHeadlines(String raw) {
        headlines = new ArrayList<LawHeadline>();

        if (raw.equals("")) { // Error while downloading
            headlines.add(new LawHeadline(1, context.getString(R.string.error_downloading)));
        } else {
            String[] splitted = raw.split("\\r?\\n");

            for (String line : splitted) {
                if (line.contains(":")) {
                    String[] depthAndText = line.split(":");
                    headlines.add(new LawHeadline(Integer.parseInt(depthAndText[0]), depthAndText[1].trim()));
                }
            }
            if (splitted.length == 1) {
                notifyOnlyOneHeader();
                return;
            }
        }

        notifyDataSetChanged();
    }

    public void notifyOnlyOneHeader() {
        for (DataSetObserver d : observers) {
            d.onInvalidated();
        }
    }

    public Context getContext() {
        return context;
    }

    public int getCount() {
        if (headlines == null) {
            return 0;
        }
        return headlines.size();
    }

    public LawHeadline getItem(int position) {
        try {
            return headlines.get(position);
        } catch (IndexOutOfBoundsException e) {
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

    @Override
    public void registerDataSetObserver(DataSetObserver arg0) {
        observers.add(arg0);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver arg0) {
        observers.remove(arg0);
    }

    @Override
    public void notifyDataSetChanged() {
        for (DataSetObserver d : observers) {
            d.onChanged();
        }
    }
}