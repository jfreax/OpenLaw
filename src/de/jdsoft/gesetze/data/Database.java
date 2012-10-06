package de.jdsoft.gesetze.data;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Pair;
import de.jdsoft.gesetze.LawListFragment.SectionComposerAdapter;
import de.jdsoft.gesetze.data.helper.Law;

public class Database extends AsyncTask<SectionComposerAdapter, Integer, List<Pair<String, List<Law>>>> {
	public static final String TAG = Database.class.getSimpleName();
	private static SectionComposerAdapter mCallback = null;

	protected List<Pair<String, List<Law>>> doInBackground(SectionComposerAdapter... params) {
		mCallback = params[0];

		DatabaseHandler dbHandler = new DatabaseHandler(mCallback.getContext());
		Cursor lawCursor = dbHandler.getAllLaws();
		
		String sectionName = null;
		List<Law> currentList = new ArrayList<Law>();
		List<Pair<String, List<Law>>> res = new ArrayList<Pair<String, List<Law>>>();
		
		if (lawCursor.moveToFirst()) {
			do {
				Law law = new Law();
				law.setID(Integer.parseInt(lawCursor.getString(0)));
				law.setShortName(lawCursor.getString(1));
				law.setLongName(lawCursor.getString(2));
				law.setText(lawCursor.getString(2));

				String firstCharacter = lawCursor.getString(1).substring(0, 1).toUpperCase();
				if ( sectionName == null ) {
					sectionName = firstCharacter;
				}
				if ( sectionName.equals(firstCharacter) ) {
					currentList.add(law);
				} else {
					res.add(new Pair<String, List<Law>>(sectionName, currentList));
					sectionName = firstCharacter;
					currentList = new ArrayList<Law>();
				}

			} while (lawCursor.moveToNext());
		}

		// Do not forget to add the last section!
		res.add(new Pair<String, List<Law>>(sectionName, currentList));
		return res;
	}

	protected void onPostExecute(List<Pair<String, List<Law>>> result) {
		mCallback.onReady(result);
	}

}
