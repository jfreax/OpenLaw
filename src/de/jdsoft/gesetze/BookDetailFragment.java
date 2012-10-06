package de.jdsoft.gesetze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import de.jdsoft.gesetze.data.DatabaseHandler;
import de.jdsoft.gesetze.data.helper.Law;

/**
 * A fragment representing a single Book detail screen. This fragment is either
 * contained in a {@link BookListActivity} in two-pane mode (on tablets) or a
 * {@link BookDetailActivity} on handsets.
 */
public class BookDetailFragment extends SherlockFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * This fragment is presenting.
	 */
	private Law law = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public BookDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			DatabaseHandler dbHandler = new DatabaseHandler(this.getActivity().getApplicationContext());
			law = dbHandler.getLaw(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_book_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (law != null) {
			((TextView) rootView.findViewById(R.id.book_detail)).setText(law.getText());
		}

		return rootView;
	}
}
