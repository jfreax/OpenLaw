package de.jdsoft.law;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flags);
		
		GridView gridview = (GridView) findViewById(R.id.flagview);
	    gridview.setAdapter(new FlagAdapter(this));
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	    
        gridview.setColumnWidth( getRowWidth() );
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	
    	// TODO save and restore position (if possible)
    	GridView gridview = (GridView) findViewById(R.id.flagview);
    	gridview.setAdapter(new FlagAdapter(this));
    	
    }
	
	private int getRowWidth() {
	  int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
      int iImageWidth = iDisplayWidth / 2; 
      
      if ( iImageWidth > 400 ) {
    	  iImageWidth = 400;
      }
      
      return iImageWidth;
	}

	public class FlagAdapter extends BaseAdapter {
	    private Context mContext;

	    public FlagAdapter(Context c) {
	        mContext = c;
	    }

	    public int getCount() {
	        return mFlags.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View view = convertView;
	        if (view == null) {
	        	view = getLayoutInflater().inflate(R.layout.item_flags, null);
	        	
	        	ImageView imageView = (ImageView) view.findViewById(R.id.flag_image);
	            imageView.setLayoutParams(new LinearLayout.LayoutParams(getRowWidth(), (getRowWidth()*3) / 5));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	            imageView.setPadding(8, 8, 8, 8);
	        }
	        
	        ImageView imageView = (ImageView) view.findViewById(R.id.flag_image);
			TextView text = (TextView) view.findViewById(R.id.flag_text);

	        imageView.setImageResource(mFlags[position]); 
	        text.setText(mFlagNames[position]);

	        return view;
	    }

	    // references to all flags
        private Integer[] mFlags = {};
//	    private Integer[] mFlags = {
//	            R.drawable.flag_of_baden, R.drawable.flag_of_bayern,
//	            R.drawable.flag_of_berlin, R.drawable.flag_of_brandenburg,
//	            R.drawable.flag_of_bremen, R.drawable.flag_of_hamburg,
//	            R.drawable.flag_of_hesse, R.drawable.flag_of_lower_saxony,
//	            R.drawable.flag_of_mecklenburg_pomerania, R.drawable.flag_of_north_rhine_westphalia,
//	            R.drawable.flag_of_rhineland_palatinate, R.drawable.flag_of_saarland,
//	            R.drawable.flag_of_saxony, R.drawable.flag_of_saxony_anhalt,
//	            R.drawable.flag_of_schleswig_holstein, R.drawable.flag_of_thuringia
//	    };
	    
	    private String[] mFlagNames = getResources().getStringArray(R.array.LanderNames);
	}
}
