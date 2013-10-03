package de.jdsoft.law.network;

import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class RestClient {
    private static final String BASE_URL = "http://api.openlaw.jdsoft.de"; // TODO property!

    public static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d("RestClient", getAbsoluteUrl(url));
        client.get(context, getAbsoluteUrl(url), params, responseHandler);
    }

    public static void cancel(Context context) {
        client.cancelRequests(context, true);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + "/" + relativeUrl;
    }
}