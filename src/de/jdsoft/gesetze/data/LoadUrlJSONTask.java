package de.jdsoft.gesetze.data;

import android.os.AsyncTask;
import us.feras.mdv.util.HttpHelper;

public class LoadUrlJSONTask extends AsyncTask<String, Integer, String> {

	protected String doInBackground(String... urls) {
		try {
			String url = urls[0];
			String data = HttpHelper.get(url).getResponseMessage();

			return data;
		} catch (Exception ex) {
			// TODO This will happen! What would we do? 
		}
		return null;
	}

}
