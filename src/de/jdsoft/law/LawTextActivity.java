package de.jdsoft.law;

import android.os.Bundle;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class LawTextActivity extends SherlockFragmentActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_law_text);

        // Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putLong(LawTextFragment.ARG_ITEM_ID, getIntent().getLongExtra(LawTextFragment.ARG_ITEM_ID, 0L));
            arguments.putString(LawTextFragment.ARG_ITEM_SLUG, getIntent().getStringExtra(LawTextFragment.ARG_ITEM_SLUG));
			LawTextFragment fragment = new LawTextFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.law_text_container, fragment).commit();
		}
	}

    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

       return false;
    }

}
