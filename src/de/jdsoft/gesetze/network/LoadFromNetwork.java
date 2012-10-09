package de.jdsoft.gesetze.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

public class LoadFromNetwork {
    public void getLawNames() throws JSONException {
        RestClient.get("laws", null, new JsonHttpResponseHandler() {
            public void onSuccess(JSONArray response) {
                JSONObject firstEvent;
				try {
					firstEvent = (JSONObject) response.get(0);
	                String bla = firstEvent.getString("text");
	                
	                // Do something with the response
	                Log.w(LoadFromNetwork.class.getName(), bla);

				} catch (JSONException e) {
					// TODO Catch it!
					e.printStackTrace();
				}


            }
        });
    }
}
