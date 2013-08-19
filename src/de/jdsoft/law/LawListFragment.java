package de.jdsoft.law;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.foound.widget.AmazingAdapter;

import de.jdsoft.law.data.LawSectionList;
import de.jdsoft.law.data.UpdateLawList;
import de.jdsoft.law.data.helper.Law;
import de.jdsoft.law.helper.CallbackInterface;
import de.jdsoft.law.helper.CallerInterface;

/**
 * A list fragment representing a list of Laws. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link LawHeadlineFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LawListFragment extends SherlockListFragment {

	final SectionComposerAdapter adapter = new SectionComposerAdapter();
    SectionComposerAdapter adapterFavs = new SectionComposerAdapter();

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
        /**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LawListFragment() {
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load actual list
        final LawSectionList sectionDB = new LawSectionList(LawSectionList.TYPE_ALL);
        if( !adapter.isFinish ) {
            sectionDB.execute(adapter);

            // And parallel update the list from network
            UpdateLawList updater = new UpdateLawList();
            updater.execute(adapter);
        }
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
//			setActivatedPosition(savedInstanceState
//					.getInt(STATE_ACTIVATED_POSITION));
            getListView().setSelection(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
		}

		final ListView listView = getListView();
//        listView.setBackgroundColor(Color.TRANSPARENT);
        TypedArray a = getActivity().getTheme().obtainStyledAttributes(R.style.AppThemeDark, new int[]{R.attr.background});
        int attributeResourceId = a.getResourceId(0, 0);
        listView.setBackgroundColor(getResources().getColor(attributeResourceId));
        a.recycle();

        // Enable fast scroll
		listView.setFastScrollEnabled(true);
		listView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);
	}


	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;

	}

	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.	
		int dbid = ((SectionComposerAdapter)listView.getAdapter()).getItem(position).getID();
		mCallbacks.onItemSelected(String.valueOf(dbid)); // TODO is this really correct?
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
		
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	/**
	 * Section Composer... 
	 * @author Jens Dieskau
	 *
	 */
	public class SectionComposerAdapter extends AmazingAdapter implements CallbackInterface, OnScrollListener, Filterable {
		List<Pair<String, List<Law>>> unfiltered = null;
        List<Pair<String, List<Law>>> all = null;

        public boolean isFinish = false;

		public SectionComposerAdapter() {
		}

		public void onFinish(CallerInterface caller) {
            isFinish = true;
			this.all = ((LawSectionList)caller).getResult();

            if(this.all == null ) {
                return; // TODO show info that no laws available
            }

            this.unfiltered = new ArrayList<Pair<String, List<Law>>>(this.all); // copy

			if ( this.getCount() > 0 ) {
				setListAdapter(this);
			}
		}

		public Context getContext() {
			return getActivity().getApplicationContext();
		}

		public int getCount() {
			int res = 0;
			for (int i = 0; i < all.size(); i++) {
				res += all.get(i).second.size();
			}
			return res;
		}

		public Law getItem(int position) {
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (position >= c && position < c + all.get(i).second.size()) {
					return all.get(i).second.get(position - c);
				}
				c += all.get(i).second.size();
			}
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		protected void onNextPageRequested(int page) {
		}

		protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
			if (displaySectionHeader) {
				view.findViewById(R.id.card_header).setVisibility(View.VISIBLE);
				TextView lSectionTitle = (TextView) view.findViewById(R.id.header);
				lSectionTitle.setText(getSections()[getSectionForPosition(position)]);
			} else {
				view.findViewById(R.id.card_header).setVisibility(View.GONE);
			}
		}

		public View getAmazingView(int position, View convertView, ViewGroup parent) {
			View res = convertView;
			if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.card_composer, null);

			TextView shortName = (TextView) res.findViewById(R.id.shortName);
			TextView fullName = (TextView) res.findViewById(R.id.fullName);

			Law law = getItem(position);
			shortName.setText(law.getShortName());
			fullName.setText(law.getLongName());

			return res;
		}

		public void configurePinnedHeader(View header, int position, int alpha) {
			TextView lSectionHeader = (TextView)header;
//			lSectionHeader.setText(getSections()[getSectionForPosition(position)]);
//			lSectionHeader.setBackgroundColor(alpha << 24 | (0xbbffbb));
//			lSectionHeader.setTextColor(alpha << 24 | (0x000000));
		}

		public int getPositionForSection(int section) {
			if (section < 0) section = 0;
			if (section >= all.size()) section = all.size() - 1;
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (section == i) { 
					return c;
				}
				c += all.get(i).second.size();
			}
			return 0;
		}

		public int getSectionForPosition(int position) {
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (position >= c && position < c + all.get(i).second.size()) {
					return i;
				}
				c += all.get(i).second.size();
			}
			return -1;
		}

		public String[] getSections() {
			String[] res = new String[all.size()];
			for (int i = 0; i < all.size(); i++) {
				res[i] = all.get(i).first;
			}
			return res;
		}

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    ArrayList<Pair<String, List<Law>>> filteredArrayItems = new ArrayList<Pair<String, List<Law>>>();
                    FilterResults results = new FilterResults();
                    constraint = constraint.toString().toLowerCase();


                    for (int i = 0; i < unfiltered.size(); i++) {
                        Pair<String, List<Law>> pair = unfiltered.get(i);

                        List<Law> filteredLaw = new ArrayList<Law>();

                        for(Law law : pair.second) {
                            if( law.getShortName().toLowerCase().contains(constraint) ||
                                law.getLongName().toLowerCase().contains(constraint)  ||
                                law.getSlug().toLowerCase().contains(constraint)) {

                                filteredLaw.add(law);
                            }
                        }
                        if( !filteredLaw.isEmpty() ) {
                            filteredArrayItems.add(new Pair<String, List<Law>>(pair.first, filteredLaw));
                        }

                    }
                    results.count = filteredArrayItems.size();
                    results.values = filteredArrayItems;

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    all = (ArrayList<Pair<String, List<Law>>>) results.values;
                    notifyDataSetChanged();
                }
            };

            return filter;
        }
    }
}
