package de.jdsoft.gesetze.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.AsyncTask;
import android.util.Log;
import de.jdsoft.gesetze.CallerInterface;
import de.jdsoft.gesetze.LawListFragment.SectionComposerAdapter;
import de.jdsoft.gesetze.data.helper.Law;
import de.jdsoft.gesetze.database.LawDb;
import de.jdsoft.gesetze.database.LawSectionList;
import de.jdsoft.gesetze.network.RestClient;

public class UpdateLawList extends AsyncTask<SectionComposerAdapter, Integer, Boolean> implements CallerInterface {
	private static SectionComposerAdapter mCallback = null;

	protected Boolean doInBackground(SectionComposerAdapter... params) {
		mCallback = params[0];
		
		this.getLawNames();

		
		return true;
	}
	
	
    public void getLawNames() {
    	Log.e("law", "Get log names");
        RestClient.get("laws", null, new JsonHttpResponseHandler() {   	
            public void onSuccess(JSONArray response) {
				try {
					// Build list of all laws
					List<Law> laws = new ArrayList<Law>();
					for( int i = 0; i < response.length(); ++i) {
						JSONObject jsonLaw = (JSONObject) response.get(i);
						
						Log.w("Testausgabe", jsonLaw.optString("n"));
						Law law = new Law(jsonLaw.optString("n"), jsonLaw.optString("l"), "empty");
						laws.add(law);
					}
					
					// Clear db
					LawDb db = new LawDb(mCallback.getContext());
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
            public void onFailure(Throwable e, JSONObject errorResponse) {
            }
            
            public void onFailure(Throwable e, JSONArray errorResponse) {
            }
        });
    }
    
	protected void onPostExecute(Boolean result) {
	}

}
