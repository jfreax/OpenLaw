package de.jdsoft.law.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.AsyncTask;
import android.util.Pair;
import de.jdsoft.law.LawListActivity;
import de.jdsoft.law.database.DbLaws;
import de.jdsoft.law.database.Handler;
import de.jdsoft.law.helper.CallerInterface;
import de.jdsoft.law.LawListFragment.SectionComposerAdapter;
import de.jdsoft.law.data.helper.Law;

public class LawSectionList extends AsyncTask<SectionComposerAdapter, Integer, List<Pair<String, List<Law>>>> implements CallerInterface {
	public static final String TAG = LawSectionList.class.getSimpleName();
	private static SectionComposerAdapter mCallback = null;
	private List<Pair<String, List<Law>>> result = null;
    public boolean isExecuted = false;

    protected List<Pair<String, List<Law>>> doInBackground(SectionComposerAdapter... params) {
		mCallback = params[0];

        if( isCancelled() ) {
            return null;
        }
		List<Law> allLaws = DbLaws.getAllLaws();

		String sectionName = null;
		List<Law> currentList = new ArrayList<Law>();
		List<Pair<String, List<Law>>> res = new ArrayList<Pair<String, List<Law>>>();
		
		for( Law law : allLaws ) {
            // Get first letter
			String firstCharacter = law.getShortName().substring(0, 1).toUpperCase(Locale.GERMAN);

            // Change umlauts
            if( firstCharacter.equals("Ä")) {
                firstCharacter = "A";
            }
            if( firstCharacter.equals("Ö")) {
                firstCharacter = "O";
            }
            if( firstCharacter.equals("Ü")) {
                firstCharacter = "U";
            }

			if ( sectionName == null ) {
				sectionName = firstCharacter;
			}
			if ( sectionName.equals(firstCharacter) ) {
				currentList.add(law);
			} else {
				res.add(new Pair<String, List<Law>>(sectionName, currentList));
				sectionName = firstCharacter;
				currentList = new ArrayList<Law>();
				currentList.add(law);
			}
		}

		// Do not forget to add the last section!
		res.add(new Pair<String, List<Law>>(sectionName, currentList));
		return res;
	}

	protected void onPostExecute(List<Pair<String, List<Law>>> result) {
		this.result = result;
        isExecuted = true;
        if( !isCancelled() ) {
		    mCallback.onFinish(this);
        }
	}

	public List<Pair<String, List<Law>>> getResult() {
		return this.result;
	}

}
