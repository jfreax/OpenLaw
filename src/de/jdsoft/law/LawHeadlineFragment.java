package de.jdsoft.law;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.PropertyValuesHolder;
import de.jdsoft.law.LawListFragment.Callbacks;
import de.jdsoft.law.data.helper.Law;
import de.jdsoft.law.data.helper.LawHeadline;
import de.jdsoft.law.database.LawNamesDb;

/**
 * A fragment representing a single Book detail screen. This fragment is either
 * contained in a {@link LawListActivity} in two-pane mode (on tablets) or a
 * {@link LawHeadlineActivity} on handsets.
 */
public class LawHeadlineFragment extends SherlockListFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	private String slug = "";
	private HeadlineComposerAdapter adapter;
	
    public static final int ANIM_DURATION =100;
    private static final Interpolator interpolator = new DecelerateInterpolator();

    boolean isCollapsed = true;
    private ViewGroup panel1, panel2, panel3;

    // For save state
    public static final String STATE_SLUG = "STATE_SLUG";
    public static final String STATE_LAW = "STATE_LAW";

	
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
	 * A dummy implementation of the interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		public void onItemSelected(String id) {
		}
	};
	
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;


	private Law law = null;
    private long selectedID = -1L;
    private LinearLayout loading;

    /**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LawHeadlineFragment() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//        if( savedInstanceState != null ) {
//            this.slug = savedInstanceState.getString(STATE_SLUG);
//            this.law = (Law)savedInstanceState.getSerializable(STATE_LAW);
//            LawNamesDb dbHandler = new LawNamesDb(this.getActivity().getApplicationContext());
//        } else {
            if ( getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
                LawNamesDb dbHandler = new LawNamesDb(this.getActivity().getApplicationContext());
                law = dbHandler.getLaw(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));


                if (law != null) {
                    // Change title
                    if( getSherlockActivity() instanceof LawHeadlineActivity) {
                        getSherlockActivity().getSupportActionBar().setTitle(law.getShortName());
                    } else {
                        getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.title_law));
                    }

                    // Save slug for later
                    this.slug = law.getSlug();
                }
            }
//        }
		
        panel1 = (ViewGroup) getSherlockActivity().findViewById(R.id.law_list);
        panel2 = (ViewGroup) getSherlockActivity().findViewById(R.id.law_headline_container);
        panel3 = (ViewGroup) getSherlockActivity().findViewById(R.id.law_text_container);

        loading = (LinearLayout)getSherlockActivity().findViewById(R.id.loading);
        if( loading != null )
            loading.setVisibility(View.VISIBLE);
	}

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new HeadlineComposerAdapterWithView(getSherlockActivity(), slug);
        setListAdapter(adapter);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            getListView().setSelection(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
//            setActivatedPosition(savedInstanceState
//                    .getInt(STATE_ACTIVATED_POSITION));
        }

        final ListView listView = getListView();

        // Enable fast scroll
        listView.setFastScrollEnabled(true);
        listView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);

        setActivateOnItemClick(true);
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
        // Refresh law text only at new clicked id
        if( selectedID == id ) {
//            getListView().setItemChecked((int)selectedID, true);
            return;
        }

        selectedID = id;

        // In two pane mode
		if ( getActivity() instanceof LawListActivity && ((LawListActivity)getActivity()).isTwoPane() ) {
			Bundle arguments = new Bundle();
			arguments.putLong(LawTextFragment.ARG_ITEM_ID, id);
			arguments.putString(LawTextFragment.ARG_ITEM_SLUG, law.getSlug());
            arguments.putString(LawTextFragment.ARG_ITEM_SHORT, law.getShortName());
			LawTextFragment text_fragment = new LawTextFragment();
			text_fragment.setArguments(arguments);

            // Replace fragment
			FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out);
			ft.replace(R.id.law_text_container, text_fragment);
			ft.commit();
			
			((LawListActivity)getActivity()).headlineFragment = this;

            // Fade in
			if ( isCollapsed )
				fadeIn();
		} else {
			// In single-pane mode, simply start the text activity
			// for the selected item ID.
			Intent detailIntent = new Intent(getActivity(), LawTextActivity.class);
			detailIntent.putExtra(LawTextFragment.ARG_ITEM_ID, id);
			detailIntent.putExtra(LawTextFragment.ARG_ITEM_SLUG, law.getSlug());
            detailIntent.putExtra(LawTextFragment.ARG_ITEM_SHORT, law.getShortName());
			startActivity(detailIntent);
            getSherlockActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        }
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
//            outState.putString(STATE_SLUG, this.slug);
//            outState.putSerializable(STATE_LAW, this.law);
		}
	}
	
    public void fadeIn() {
        isCollapsed = false;
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Most of the magic here can be attributed to: http://android.amberfog.com/?p=758
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[3];
        arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat("Panel1Weight", 1.0f, 0.0f);
        arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat("Panel2Weight", 2.0f, 1.0f);
        arrayOfPropertyValuesHolder[2] = PropertyValuesHolder.ofFloat("Panel3Weight", 0.0f, 2.0f);
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, arrayOfPropertyValuesHolder).setDuration(ANIM_DURATION);
        localObjectAnimator.setInterpolator(interpolator);
        localObjectAnimator.start();

    }
    
    public void fadeOut() {
    	isCollapsed = true;

        // Disable up button
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Reset title
        getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.title_law));

        // Deselect
        getListView().setItemChecked((int)selectedID, false);
        selectedID = -1;

        // Animation
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[3];
        arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat("Panel1Weight", 0.0f, 1.0f);
        arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat("Panel2Weight", 1.0f, 2.0f);
        arrayOfPropertyValuesHolder[2] = PropertyValuesHolder.ofFloat("Panel3Weight", 2.0f, 0.0f);
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, arrayOfPropertyValuesHolder).setDuration(ANIM_DURATION);
        localObjectAnimator.setInterpolator(interpolator);
        localObjectAnimator.start();
    }
    

    /*
     * Our magic getters/setters below!
     */
    
    public float getPanel1Weight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)     panel1.getLayoutParams();
        return params.weight;
    }

    public void setPanel1Weight(float newWeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panel1.getLayoutParams();
        params.weight = newWeight;
        panel1.setLayoutParams(params);
    }

    public float getPanel2Weight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panel2.getLayoutParams();
        return params.weight;
    }

    public void setPanel2Weight(float newWeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panel2.getLayoutParams();
        params.weight = newWeight;
        panel2.setLayoutParams(params);
    }

    public float getPanel3Weight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panel3.getLayoutParams();
        return params.weight;
    }

    public void setPanel3Weight(float newWeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panel3.getLayoutParams();
        params.weight = newWeight;
        panel3.setLayoutParams(params);
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


    public class HeadlineComposerAdapterWithView extends HeadlineComposerAdapter {

        public HeadlineComposerAdapterWithView(Activity activity, String slug) {
            super(activity, slug);
        }

        protected void makeHeadlines(String raw) {
            SherlockFragmentActivity activity = getSherlockActivity();
            if( activity == null )
                return;

            if( loading != null )
                loading.setVisibility(View.GONE);

            super.makeHeadlines(raw);
        }

        // TODO performance!
        public View getView(int position, View convertView, ViewGroup parent) {
            View res = convertView;

            LawHeadline lineObj = getItem(position);
            if( Math.abs(lineObj.depth) == 1) {
                res = getActivity().getLayoutInflater().inflate(R.layout.item_headline_biggest, parent, false);
                TextView headline = (TextView) res.findViewById(R.id.headline);
                headline.setText(lineObj.headline);

            } else if( Math.abs(lineObj.depth) == 2) {
                res = getActivity().getLayoutInflater().inflate(R.layout.item_headline_big, parent, false);
                TextView headline = (TextView) res.findViewById(R.id.headline);
                headline.setText(lineObj.headline);

            } else {
                res = getActivity().getLayoutInflater().inflate(R.layout.item_headline, parent, false);
                TextView headline = (TextView) res.findViewById(R.id.headline);

                // Add margin for first element
                if( position == 0 ) {
                    getListView().setPadding(0, 8, 0, 0);
                }

                // Add padding for last or for last not big/biggest headline
                if( position == getCount()-1 || getItem(position).depth < lineObj.depth ) {
                    headline.setPadding(0,0,0,8);
                }

                switch (Math.abs(lineObj.depth)) {
                    case 3:
                        headline.setTextAppearance(getContext(), R.style.Headline3);
                        break;
                    case 4:
                        headline.setTextAppearance(getContext(), R.style.Headline4);
                        break;
                    case 5:
                        headline.setTextAppearance(getContext(), R.style.Headline5);
                        break;
                    case 6:
                        headline.setTextAppearance(getContext(), R.style.Headline6);
                        break;
                    default:
                        break;
                }
                // Set text
                headline.setText(lineObj.headline);
            }

            return res;
        }
    }

}
