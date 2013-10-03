package de.jdsoft.law.data;

import android.os.AsyncTask;
import android.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import de.jdsoft.law.LawListFragment.SectionComposerAdapter;
import de.jdsoft.law.data.helper.Law;
import de.jdsoft.law.database.Laws;
import de.jdsoft.law.helper.CallerInterface;
import de.jdsoft.law.network.RestClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateLawList extends AsyncTask<SectionComposerAdapter, Integer, Boolean> implements CallerInterface {
    private static SectionComposerAdapter mCallback = null;

    protected Boolean doInBackground(SectionComposerAdapter... params) {
        mCallback = params[0];

        if (Laws.getLawsCount() == 0) {
            this.getLawNames();
        }

        return true;
    }


    public void getLawNames() {
        RestClient.get(mCallback.getContext(), "laws", null, new JsonHttpResponseHandler() {
            public void onSuccess(JSONArray response) {
                try {
                    // Build list of all laws
                    List<Law> laws = new ArrayList<Law>();
                    for (int i = 0; i < response.length(); ++i) {
                        JSONArray jsonLaw = (JSONArray) response.get(i);

                        Law law = new Law(jsonLaw.getString(0), jsonLaw.getString(1), jsonLaw.getString(2));
                        laws.add(law);
                    }

                    // Clear db
                    Laws.clear();

                    // Add new law list to db
                    Laws.addLaws(laws);

                    if (response.length() != 0) {
                        LawSectionList sectionListBuilder = new LawSectionList(LawSectionList.TYPE_ALL);
                        sectionListBuilder.execute(mCallback);
                    }

                } catch (JSONException e) {
                    // TODO Catch it!
                    e.printStackTrace();
                }
            }

            public void onFailure(Throwable e, JSONObject errorResponse) {
                Log.w("UpdateLawList", "1");
            }

            public void onFailure(Throwable e, JSONArray errorResponse) {
                Log.w("UpdateLawList", "2");
            }
        });
    }

    protected void onPostExecute(Boolean result) {
    }

}
