package de.jdsoft.law;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import de.jdsoft.law.LawListFragment.Callbacks;
import de.jdsoft.law.data.helper.Law;
import de.jdsoft.law.data.helper.LawHeadline;
import de.jdsoft.law.database.Favorites;
import de.jdsoft.law.database.Laws;

import java.util.Random;

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
    public static final int OPTION_FAV = 4;

    private String slug = "";
	private HeadlineComposerAdapter adapter;

    public static final int ANIM_DURATION =100;
    private static final Interpolator interpolator = new DecelerateInterpolator();

    boolean isCollapsed = true;
    private ViewGroup panel1, panel2, panel3;

    // For save state
    public static final String STATE_SLUG = "STATE_SLUG";
    public static final String STATE_LAW = "STATE_LAW";

    // Dark color for light themes
    static int[] COLOR = ColorList.DARK_COLOR;


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

    // Animations
    private AnimatorSet fadeInAnimation;
    private AnimatorSet fadeOutAnimation;
    private MenuItem favMenu;


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
//            Connector dbHandler = new Connector(this.getActivity().getApplicationContext());
//        } else {
//        }

        // Initialize the three panels in tablet mode
        if ( getActivity() instanceof LawListActivity && ((LawListActivity)getActivity()).isTwoPane() ) {
            panel1 = (ViewGroup) getSherlockActivity().findViewById(R.id.law_list_container);
            panel2 = (ViewGroup) getSherlockActivity().findViewById(R.id.law_headline_container);
            panel3 = (ViewGroup) getSherlockActivity().findViewById(R.id.law_text_container);

            // Animation
            initializeAnimation();
        }

        // Referenc to loading indicator view
        loading = (LinearLayout)getSherlockActivity().findViewById(R.id.loading);

        // Set color collection
        SharedPreferences pref = getSherlockActivity().getSharedPreferences("openlaw", Context.MODE_PRIVATE);
        if( pref.getBoolean("dark_theme", false) ) {
            LawHeadlineFragment.COLOR = ColorList.LIGHT_COLOR;
        } else {
            LawHeadlineFragment.COLOR = ColorList.DARK_COLOR;
        }
	}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        if ( getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            updateAdapter(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));
        }

        setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Remember listview
        final ListView listView = getListView();

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            listView.setSelection(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }

        // Disable standard orange background selection
//        listView.setCacheColorHint(android.R.color.transparent);
        listView.setSelector(android.R.color.transparent);

        // Enable fast scroll
        listView.setFastScrollEnabled(true);
        listView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);

        setActivateOnItemClick(true);
    }


    void updateAdapter(int lawID) {
        // Show loading indicator
        if( loading != null )
            loading.setVisibility(View.VISIBLE);

        law = Laws.getLaw(lawID);
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

        adapter = new HeadlineComposerAdapterWithView(
                getSherlockActivity().getBaseContext(),
                slug,
                new DataSetObserver() { // Wait for data changed on
                    @Override
                    public void onChanged() {
                        super.onChanged();

                        // Then hide loading indicator
                        loading.setVisibility(View.GONE);
                    }
                }
        );
        setListAdapter(adapter);

        // Refresh fav icon in actionbar
        if( favMenu != null ) {
            if( Favorites.isFav(""+law.getID()) ) {
                favMenu.setIcon(R.drawable.btn_star_on_convo_holo_light);
            } else {
                favMenu.setIcon(R.drawable.btn_star_off_convo_holo_light);
            }
        }
    }


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        boolean isLight = true; // TODO

        // Favorite
        int favDrawable = 0;
        if( Favorites.isFav(""+law.getID()) ) {
            favDrawable = R.drawable.btn_star_on_convo_holo_light;
        } else {
            favDrawable = R.drawable.btn_star_off_convo_holo_light;
        }

        menu.add(0, OPTION_FAV, 2, R.string.favit)
                .setIcon(favDrawable)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        favMenu = menu.findItem(OPTION_FAV);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
        switch (item.getItemId()) {

            case LawHeadlineFragment.OPTION_FAV:
                String id = ""+law.getID();
                if( Favorites.isFav(id) ) {
                    Favorites.removeFav(id);
                    item.setIcon(R.drawable.btn_star_off_convo_holo_light);
                } else {
                    Favorites.addFav(id);
                    item.setIcon(R.drawable.btn_star_on_convo_holo_light);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
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
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
			ft.replace(R.id.law_text_container, text_fragment);
			ft.commit();

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
            getSherlockActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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


    public void initializeAnimation() {
        // Fade in //
        //---------//

        float listWidth = panel1.getWidth();

        // Scale list view and fade it a little bit
        AnimatorSet scaleDownAndFadeOut =
                (AnimatorSet) AnimatorInflater.loadAnimator(getSherlockActivity(),
                        R.anim.scale_down_and_fadeout);
        scaleDownAndFadeOut.setTarget(panel1);
        scaleDownAndFadeOut.setInterpolator(new AccelerateDecelerateInterpolator());

        // Move it to left (out of screen)
        ObjectAnimator leftOut = ObjectAnimator.ofFloat(panel1, "translationX", -listWidth);
        leftOut.setDuration(150);
        leftOut.setStartDelay(80);
        leftOut.setInterpolator(new AccelerateInterpolator());


        // Move and change size for second (headline) panel at once
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(
                leftOut,
                ObjectAnimator.ofFloat(this, "Panel1Weight", 2.0f, 0.0f).setDuration(200),
                ObjectAnimator.ofFloat(this, "Panel2Weight", 3.0f, 2.0f).setDuration(80)
        );
        animSet.setStartDelay(30);

        // Altgether, fade out, translate out together and after a small delay changes headline size
        // and move to left side
        fadeInAnimation = new AnimatorSet();
        fadeInAnimation.playTogether(
                scaleDownAndFadeOut,
                animSet
        );

        // Fade out //
        //----------//

        // Move law list to right (into the screen)
        ObjectAnimator leftIn = ObjectAnimator.ofFloat(panel1, "translationX", 0);
        leftIn.setDuration(150);
        leftIn.setStartDelay(80);
        leftIn.setInterpolator(new AccelerateInterpolator());

        // Scale law text down and fade it a little bit
        AnimatorSet scaleDownAndFadeOut2 =
                (AnimatorSet) AnimatorInflater.loadAnimator(getSherlockActivity(),
                        R.anim.scale_down_and_fadeout);
        scaleDownAndFadeOut2.setTarget(panel3);
        scaleDownAndFadeOut2.setInterpolator(new AccelerateDecelerateInterpolator());


        // Move and change size for second (headline) panel at once
        AnimatorSet animSet2 = new AnimatorSet();
        animSet2.playTogether(
                ObjectAnimator.ofFloat(this, "Panel1Weight", 0.0f, 2.0f).setDuration(200),
                ObjectAnimator.ofFloat(this, "Panel2Weight", 2.0f, 3.0f).setDuration(80)
        );
        animSet2.setStartDelay(100);

        // Altgether, fade out, translate out together and after a small delay changes headline size
        // and move to left side
        fadeOutAnimation = new AnimatorSet();
        fadeOutAnimation.playTogether(
                scaleDownAndFadeOut2,
                leftIn,
                animSet2
        );
    }


    public void fadeIn() {
        isCollapsed = false;
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewHelper.setScaleX(panel3, 1.0f);
        ViewHelper.setScaleY(panel3, 1.0f);
        ViewHelper.setAlpha(panel3, 1.0f);

        fadeInAnimation.start();
    }

    
    public void fadeOut() {
    	isCollapsed = true;

        // Fixme workaround
        if( getSherlockActivity() != null ) {
            // Disable up button
            getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            // Reset title
            getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.title_law));

            // Deselect
            getListView().setItemChecked((int)selectedID, false);
            selectedID = -1;
        }

        ViewHelper.setScaleX(panel1, 1.0f);
        ViewHelper.setScaleY(panel1, 1.0f);
        ViewHelper.setAlpha(panel1, 1.0f);

        fadeOutAnimation.start();
    }
    

    /*
     * Our magic getters/setters below!
     */
    public float getPanel1Weight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panel1.getLayoutParams();
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


    static public class HeadlineComposerAdapterWithView extends HeadlineComposerAdapter {

        private Random rand = new Random();

        private static final int TYPE_MAX_COUNT = 3;
        private static final int TYPE_BIGGEST = 0;
        private static final int TYPE_BIG = 1;
        private static final int TYPE_NORMAL = 2;
        private final LayoutInflater mInflater;

        private int[] colorForDepth = new int[128];

        static private class ViewHolder {

            final View container;
            final TextView headline;
            final View separator;

            ViewHolder(View container, TextView headline, View separator) {
                this.container = container;
                this.headline = headline;
                this.separator = separator;
            }

        }

        public HeadlineComposerAdapterWithView(Context baseContext, String slug, DataSetObserver dataSetObserver) {
            super(baseContext);
            registerDataSetObserver(dataSetObserver);
            mInflater = (LayoutInflater)baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            initialize(slug);
        }


        public HeadlineComposerAdapterWithView(Context baseContext, String slug) {
            super(baseContext);
            mInflater = (LayoutInflater)baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            initialize(slug);
        }

        protected void makeHeadlines(String raw) {
            super.makeHeadlines(raw);
        }

        @Override
        public int getItemViewType(int position) {
            return Math.abs(getItem(position).depth) - 1;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int type = getItemViewType(position);
            if (convertView == null) {
                switch (type) {
                    case TYPE_BIGGEST:
                        convertView = mInflater.inflate(R.layout.item_headline_biggest, null);
                        break;
                    case TYPE_BIG:
                        convertView = mInflater.inflate(R.layout.item_headline_big, null);
                        break;
                    case TYPE_NORMAL:
                    default:
                        convertView = mInflater.inflate(R.layout.item_headline, null);
                        break;
                }
                holder = new ViewHolder(
                        (LinearLayout) convertView.findViewById(R.id.headline_container),
                        (TextView) convertView.findViewById(R.id.headline),
                        convertView.findViewById(R.id.seperator)
                );
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            // Set text
            holder.headline.setText(getItem(position).headline);

            // Set color and intend
            // TODO clean up this mess
            LawHeadline item = getItem(position);
            int currentDepth = Math.abs(item.depth);

            if( item.color == -1 ) {
                if( position == 0) {
                    currentDepth = 1;
                    colorForDepth[currentDepth] = COLOR[rand.nextInt(COLOR.length)];
//                    setPseudoDepthOnPosition(position, 0);
                } else {
                    int lastDepth = getDepthOfPosition(position-1);

                    if ( getPseudoDepthOnPosition(position-1) != -1 ) {

                        if( currentDepth == lastDepth ) {
                            setPseudoDepthOnPosition(position, getPseudoDepthOnPosition(position-1));
                            currentDepth = getPseudoDepthOnPosition(position-1);
                        }
                        lastDepth = getPseudoDepthOnPosition(position-1);
                    }

                    // One depth deeper, so we need a new color
                    if( currentDepth > lastDepth ) {
                        if( currentDepth-lastDepth > 1 ) {
                            setPseudoDepthOnPosition(position, lastDepth+1);
                            currentDepth = lastDepth+1;
                        }

                        colorForDepth[currentDepth] = COLOR[rand.nextInt(COLOR.length)];

                    } else {
                        // We are now more then one depth higher, add some space
                        if( lastDepth-currentDepth > 1 ) {
                            item.padding = 24;
                        }

                        if( colorForDepth[currentDepth] == 0 ) {
                            colorForDepth[currentDepth] = COLOR[rand.nextInt(COLOR.length)];
                        }
                    }
                }
                item.color = colorForDepth[currentDepth];
                item.intend = currentDepth; // save new depth
            } else {
                currentDepth = item.intend; // load saved intend depth
            }

            holder.separator.setBackgroundColor(item.color);

            // Intend
            LinearLayout.LayoutParams separatorLayout = (LinearLayout.LayoutParams) holder.separator.getLayoutParams();
            separatorLayout.leftMargin = 10*(currentDepth-1);
            holder.separator.setLayoutParams(separatorLayout);

            // Set top padding
            if( type == TYPE_BIGGEST &&
                    item.padding > 0 &&
                    position != 0 ) {
                holder.container.setPadding(0, item.padding, 0, 0);
            }



            return convertView;
        }

        int getDepthOfPosition(int position) {
            return Math.abs(getItem(position).depth);
        }

        void setPseudoDepthOnPosition(int position, int pseudo) {
            getItem(position).pseudoDepth = pseudo;
        }

        int getPseudoDepthOnPosition(int position) {
            return getItem(position).pseudoDepth;
        }

    }

}
