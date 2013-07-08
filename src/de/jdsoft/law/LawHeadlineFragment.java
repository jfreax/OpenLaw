package de.jdsoft.law;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.PropertyValuesHolder;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Snapshot;
import com.loopj.android.http.AsyncHttpResponseHandler;
import de.jdsoft.law.LawListFragment.Callbacks;
import de.jdsoft.law.data.Cache;
import de.jdsoft.law.data.helper.Law;
import de.jdsoft.law.data.helper.LawHeadline;
import de.jdsoft.law.database.LawNamesDb;
import de.jdsoft.law.network.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * This fragment is presenting.
	 */
	private Law law = null;
    private long selectedID = 0L;

    /**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LawHeadlineFragment() {
	}

	public void onCreate(Bundle savedInstanceState) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
		
		super.onCreate(savedInstanceState);
		
		if ( getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
			LawNamesDb dbHandler = new LawNamesDb(this.getActivity().getApplicationContext());
			law = dbHandler.getLaw(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));
			
			if (law != null) {
				this.slug = law.getSlug();
			}
		}
		
        panel1 = (ViewGroup) getActivity().findViewById(R.id.law_list);
        panel2 = (ViewGroup) getActivity().findViewById(R.id.law_headline_container);
        panel3 = (ViewGroup) getActivity().findViewById(R.id.law_text_container);
	}

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
//            setActivatedPosition(savedInstanceState
//                    .getInt(STATE_ACTIVATED_POSITION));
        }

        final ListView listView = getListView();

        // Enable fast scroll
        listView.setFastScrollEnabled(true);
        listView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);

        setActivateOnItemClick(true);
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_law_headline,
				container, false);
		
		adapter = new HeadlineComposerAdapter();
		setListAdapter(adapter);

		return rootView;
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
        if( selectedID == id+1 ) {
//            getListView().setItemChecked((int)selectedID, true);
            return;
        }


        //
        id++;
        selectedID = id;

        // In two pane mode
		if ( getActivity() instanceof LawListActivity && ((LawListActivity)getActivity()).isTwoPane() ) {
			Bundle arguments = new Bundle();
			arguments.putLong(LawTextFragment.ARG_ITEM_ID, id);
			arguments.putString(LawTextFragment.ARG_ITEM_SLUG, law.getSlug());
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
			startActivity(detailIntent);
        }
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
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
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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


    /**
	 * Section Composer... 
	 * @author Jens Dieskau
	 *
	 */
	public class HeadlineComposerAdapter extends BaseAdapter {
		private List<Pair<Integer,String>> headlines = null;
		Cache cache = null;

		
		public HeadlineComposerAdapter() {		
			cache = new Cache();
			getHeadlinesRaw();
		}
		
		protected void finalize() throws Throwable {
			super.finalize();

			if ( cache != null && !cache.isClosed() ) {
				cache.close();
				cache = null;
			}
		}
		
	    private void getHeadlinesRaw() {
	    	// Try to read from cache
	    	try {
	    		if ( cache == null || cache.isClosed() ) {
	    			cache.openCache();
	    		}
				Snapshot snapshot = cache.get(slug);
				if ( snapshot != null ) {
					makeHeadlines(snapshot.getString(0));
					return;
				}
			} catch (IOException e) {
				Log.e(HeadlineComposerAdapter.class.getName(), "Error while reading cache!");
				Log.e(HeadlineComposerAdapter.class.getName(), e.getCause().getMessage());
			}

	    	// Not in cache, try to read from network
	        RestClient.get(getContext(), slug+"/heads", null, new AsyncHttpResponseHandler() {   	
	            public void onSuccess(String response) {
	            	Log.d("GetLawHeadlines", "onSuccess() Response size: "+response.length());
					if ( response.length() == 0 ) {
						Log.e(HeadlineComposerAdapter.class.getName(), "Cannot download law " + slug);
						return;
					}
					
					// Save to cache
			    	try {
			    		if ( cache == null || cache.isClosed() ) {
			    			cache.openCache();
			    		}
						DiskLruCache.Editor creator = cache.edit(slug);
						creator.set(0, response);
						creator.commit();
						cache.flush();
					} catch (IOException e) {
						Log.e(HeadlineComposerAdapter.class.getName(), "Error while reading cache!");
					}
					
					makeHeadlines(response);
	            }
	            
	            public void onFailure(Throwable error, String content) {
	            	makeHeadlines("");
	            }
	        });
	    }
	    
	    private void makeHeadlines(String raw) {
	    	SherlockFragmentActivity activity = getSherlockActivity();
	    	if( activity == null )
	    		return;
	    	
	    	activity.setSupportProgressBarIndeterminateVisibility(false);
	    	
	    	headlines = new ArrayList<Pair<Integer,String>>();
	    	
	    	if( raw.equals("") ) { // Error while downloading
	    		headlines.add(new Pair<Integer, String>(1, getString(R.string.error_downloading)));
	    	} else {
				for ( String line : raw.split("\\r?\\n")) {
					if ( line.contains(":") ) {
						String[] depthAndText = line.split(":");
						headlines.add(new Pair<Integer, String>(Integer.parseInt(depthAndText[0]), depthAndText[1]));
					}
				}
	    	}
	    	
			notifyDataSetChanged();
	    }

		public Context getContext() {
			return getActivity().getApplicationContext();
		}

		public int getCount() {
			if ( headlines == null ) {
				return 0;
			}
			return headlines.size();
		}

		public LawHeadline getItem(int position) {
			try {
				return new LawHeadline(headlines.get(position).first, headlines.get(position).second);
			} catch(IndexOutOfBoundsException e){
				return null;
			}
		}

		public long getItemId(int position) {
			return position;
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
                    View stroke = res.findViewById(R.id.stroke);
                    stroke.setVisibility(View.VISIBLE);
                }

                // Add padding for last or for last not big/biggest headline
                if( position == getCount()-1 || getItem(position+1).depth < lineObj.depth ) {
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
		
		public String getSlug() {
			return slug;
		}
	}
}
