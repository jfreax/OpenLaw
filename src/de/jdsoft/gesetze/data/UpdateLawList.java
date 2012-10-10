package de.jdsoft.gesetze.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.AsyncTask;
import android.util.Log;
import de.jdsoft.gesetze.CallerInterface;
import de.jdsoft.gesetze.LawListFragment.SectionComposerAdapter;
import de.jdsoft.gesetze.data.helper.Law;
import de.jdsoft.gesetze.database.LawNamesDb;
import de.jdsoft.gesetze.network.RestClient;

public class UpdateLawList extends AsyncTask<SectionComposerAdapter, Integer, Boolean> implements CallerInterface {
	private static SectionComposerAdapter mCallback = null;

	protected Boolean doInBackground(SectionComposerAdapter... params) {
		mCallback = params[0];
		
		this.getLawNames();

		
		return true;
	}
	
	
    public void getLawNames() {
    	Log.e("LawDb", "update!!!");
        RestClient.get("laws", null, new JsonHttpResponseHandler() {   	
            public void onSuccess(JSONArray response) {
				try {
					// Build list of all laws
					List<Law> laws = new ArrayList<Law>();
					Log.e("LawDb", "okaaay "+response.length());
					for( int i = 0; i < response.length(); ++i) {
						JSONArray jsonLaw = (JSONArray) response.get(i);
						
						Law law = new Law(jsonLaw.getString(0), jsonLaw.getString(1) , jsonLaw.getString(2));
						laws.add(law);
					}
					
					// Clear db
					LawNamesDb db = new LawNamesDb(mCallback.getContext());
					db.clear();

					// Add new law list to db
					db.addLaws(laws);
					
					if ( response.length() != 0 ) {
						LawSectionList sectionListBuilder = new LawSectionList();
						sectionListBuilder.execute(mCallback);
					}

				} catch (JSONException e) {
					// TODO Catch it!
					e.printStackTrace();
				}
            }
        });
    }
    
	protected void onPostExecute(Boolean result) {
	}

}
