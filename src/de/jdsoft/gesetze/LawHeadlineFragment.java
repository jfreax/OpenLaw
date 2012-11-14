package de.jdsoft.gesetze;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.PropertyValuesHolder;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Snapshot;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.jdsoft.gesetze.LawListFragment.Callbacks;
import de.jdsoft.gesetze.data.helper.Law;
import de.jdsoft.gesetze.data.helper.LawHeadline;
import de.jdsoft.gesetze.database.LawNamesDb;
import de.jdsoft.gesetze.network.RestClient;

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
	
    public static final int ANIM_DURATION = 500;
    private static final Interpolator interpolator = new DecelerateInterpolator();

    boolean isCollapsed = false;
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
	 * A dummy implementation of the {@link HeadlineCallbacks} interface that does
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
        panel2 = (ViewGroup) getActivity().findViewById(R.id.law_detail_container);
        panel3 = (ViewGroup) getActivity().findViewById(R.id.law_text_container);
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
		
		Log.e("onListItemClick", "fdzrd");

		
		if ( getActivity() instanceof LawListActivity && ((LawListActivity)getActivity()).isTwoPane() ) {
			
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			
			//ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			ft.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_in_left);
			//ft.replace(R.id.law_list, new LawListFragment());
			//ft.replace(R.id.law_list, new LawHeadlineFragment());
			
			// Create new fragment to show law text
			LinearLayout text_container = (LinearLayout) getActivity().findViewById(R.id.law_text_container);
			//text_container.setL;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
			//	params.weight = 2.0f;
			//text_container.setLayoutParams(params);
			
			Bundle arguments = new Bundle();
			arguments.putString(LawHeadlineFragment.ARG_ITEM_ID, "10"); // TODO
			LawHeadlineFragment text_fragment = new LawHeadlineFragment(); // TODO
			text_fragment.setArguments(arguments);
			ft.replace(R.id.law_text_container, text_fragment);
			
			
						
			// Hide list of laws
			//ft.hide(getFragmentManager().findFragmentById(R.id.law_list));
			//ft.addToBackStack(null);
	
			//ft.setCustomAnimations(android.R.animator.fade_in,   android.R.animator.fade_out);
			//ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//ft.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,android.R.anim.fade_in,android.R.anim.fade_out);
	
			//ft.hide(getFragmentManager().findFragmentById(R.id.law_list)); 
			ft.commit(); 
			
			toggleCollapseState();

	
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.	
			//int dbid = ((HeadlineComposerAdapter)listView.getAdapter()).getItem(position).getID();
			//mCallbacks.onItemSelected("1");
		}

	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}
	
    private void toggleCollapseState() {
        //Most of the magic here can be attributed to: http://android.amberfog.com/?p=758

        if (isCollapsed) {
            PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[3];
            arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat("Panel1Weight", 0.0f, 1.0f);
            arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat("Panel2Weight", 1.0f, 2.0f);
            arrayOfPropertyValuesHolder[2] = PropertyValuesHolder.ofFloat("Panel3Weight", 2.0f, 0.0f);
            ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, arrayOfPropertyValuesHolder).setDuration(ANIM_DURATION);
            localObjectAnimator.setInterpolator(interpolator);
            localObjectAnimator.start();
        } else {
            PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[3];
            arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat("Panel1Weight", 1.0f, 0.0f);
            arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat("Panel2Weight", 2.0f, 1.0f);
            arrayOfPropertyValuesHolder[2] = PropertyValuesHolder.ofFloat("Panel3Weight", 0.0f, 2.0f);
            ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, arrayOfPropertyValuesHolder).setDuration(ANIM_DURATION);
            localObjectAnimator.setInterpolator(interpolator);
            localObjectAnimator.start();
        }
        isCollapsed = !isCollapsed;
    }
    
    
//    public void onBackPressed() {
//        //TODO: Very basic stack handling. Would probably want to do something relating to fragments here..
//        if(isCollapsed) {
//            toggleCollapseState();
//        } else {
//            super.onBackPressed();
//        }
//    }

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
		
		private DiskLruCache cache = null;
		private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
																	 // TODO this should be a property!
		private static final String DISK_CACHE_SUBDIR = ".Gesetze";
		private static final int DISK_CACHE_VERSION = 2;
		
		
		public HeadlineComposerAdapter() {		
			try {
				this.openCache();
			} catch (IOException e) {
				// Okay, so we can't use it :(
				Log.e(HeadlineComposerAdapter.class.getName(), "Can't open cache " + DISK_CACHE_SUBDIR + "!");
				e.printStackTrace();
			}
			getHeadlinesRaw();
		}
		
		protected void finalize() throws Throwable {
			super.finalize();
			
			try {
				if ( cache != null && !cache.isClosed() ) {
					cache.close();
					cache = null;
				}
			} catch (IOException e) {
				// Thats bad
				e.printStackTrace();
			}
		}
		
	    private void getHeadlinesRaw() {
	    	// Try to read from cache
	    	try {
	    		if ( cache == null || cache.isClosed() ) {
	    			openCache();
	    		}
				Snapshot snapshot = cache.get(slug);
				if ( snapshot != null ) {
					makeHeadlines(snapshot.getString(0));
					return;
				}
			} catch (IOException e) {
				Log.e(HeadlineComposerAdapter.class.getName(), "Error while reading cache1 " + DISK_CACHE_SUBDIR + "!");
				Log.e(HeadlineComposerAdapter.class.getName(), e.getCause().getMessage());
			}

	    	// Not in cache, try to read from network
	        RestClient.get(getContext(), "law/"+slug, null, new AsyncHttpResponseHandler() {   	
	            public void onSuccess(String response) {
	            	Log.i("GetLawHeadlines", "onSuccess() Response size: "+response.length());
					if ( response.length() == 0 ) {
						Log.e(HeadlineComposerAdapter.class.getName(), "Cannot download law " + slug);
						return;
					}
					
					// Save to cache
			    	try {
			    		if ( cache == null || cache.isClosed() ) {
			    			openCache();
			    		}
						DiskLruCache.Editor creator = cache.edit(slug);
						creator.set(0, response);
						creator.commit();
						cache.flush();
					} catch (IOException e) {
						Log.e(HeadlineComposerAdapter.class.getName(), "Error while reading cache " + DISK_CACHE_SUBDIR + "!");
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
	    		headlines.add(new Pair<Integer, String>(0, getString(R.string.error_downloading)));
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

		public View getView(int position, View convertView, ViewGroup parent) {
			View res = convertView;

			LawHeadline lineObj = getItem(position);
			res = getActivity().getLayoutInflater().inflate(R.layout.item_headline, parent, false);
			TextView headline = (TextView) res.findViewById(R.id.headline);
			
			switch (lineObj.depth) {
			case 1:
				headline.setTextAppearance(getContext(), R.style.Headline1);
				headline.setPadding(6, 0, 0, 0);
				break;
			case 2:
				headline.setTextAppearance(getContext(), R.style.Headline2);
				headline.setPadding(12, 0, 0, 0);
				break;
			case 3:
				headline.setTextAppearance(getContext(), R.style.Headline3);
				headline.setPadding(16, 0, 0, 0);
				break;
			case 4:
				headline.setTextAppearance(getContext(), R.style.Headline4);
				headline.setPadding(20, 0, 0, 0);
				break;
			case 5:
				headline.setTextAppearance(getContext(), R.style.Headline5);
				headline.setPadding(24, 0, 0, 0);
				break;
			case 6:
				headline.setTextAppearance(getContext(), R.style.Headline6);
				headline.setPadding(28, 0, 0, 0);
				break;
			default:
				break;
			}
		
			headline.setText(lineObj.headline);

			return res;
		}
		
		public String getSlug() {
			return slug;
		}
		
		public void openCache() throws IOException {
	        String javaTmpDir = System.getProperty("java.io.tmpdir");
	        File cacheDir = new File(javaTmpDir, DISK_CACHE_SUBDIR);
	        cacheDir.mkdir();
	        
	        cache = DiskLruCache.open(cacheDir, DISK_CACHE_VERSION, 1, DISK_CACHE_SIZE);
		}
	}
}
