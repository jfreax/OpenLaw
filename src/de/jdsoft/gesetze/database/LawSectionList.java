package de.jdsoft.gesetze.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.AsyncTask;
import android.util.Pair;
import de.jdsoft.gesetze.CallerInterface;
import de.jdsoft.gesetze.LawListFragment.SectionComposerAdapter;
import de.jdsoft.gesetze.data.helper.Law;

public class LawSectionList extends AsyncTask<SectionComposerAdapter, Integer, List<Pair<String, List<Law>>>> implements CallerInterface {
	public static final String TAG = LawSectionList.class.getSimpleName();
	private static SectionComposerAdapter mCallback = null;
	private List<Pair<String, List<Law>>> result = null;

	protected List<Pair<String, List<Law>>> doInBackground(SectionComposerAdapter... params) {
		mCallback = params[0];

		LawDb dbHandler = new LawDb(mCallback.getContext());
		List<Law> allLaws = dbHandler.getAllLaws();

		String sectionName = null;
		List<Law> currentList = new ArrayList<Law>();
		List<Pair<String, List<Law>>> res = new ArrayList<Pair<String, List<Law>>>();
		
		for( Law law : allLaws ) {
			String firstCharacter = law.getShortName().substring(0, 1).toUpperCase(Locale.GERMAN);
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
		mCallback.onFinish(this);
	}

	public List<Pair<String, List<Law>>> getResult() {
		return this.result;
	}

}
