package appinventor.ai_cwilson.Mobile_Digital_Scale;

import com.google.ads.AdView;
import com.google.ads.AdRequest;
import appinventor.ai_cwilson.Mobile_Digital_Scale.R;

import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {

	double weight = 0.0; 	// weight
	boolean bounce = false; // indicates scale status for bounce up/down
	static int TIME = 350;	//bounce time in milliseconds
	String unit = "g";		//string for units 'g' or 'oz'
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//no ads on full version
		/*AdView adView = (AdView) findViewById(R.id.adView); 
		adView.loadAd(new AdRequest());*/
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			weight = extras.getDouble("newWeight");
			unit = extras.getString("UNIT");

			updateUnit();
			
		}
		
		//Shared Preferences
		
		//SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    
		
		//instead of bool flag, should do version/timestamp
		boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
	    int runCount = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("runCount", 0); 
	    int rateCount = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("rateCount", 3);
	    String version = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("version", getString(R.string.cur_version));	//getString(R.string.cur_version)
	    
	    if (firstrun){ 
	    	
	    	//update showFirstRun function
	    	showChangeLog(this, "First Run");
	    	
	    	// Update firstrun to false
	    	getSharedPreferences("PREFERENCE", MODE_PRIVATE)
	    		.edit()
	    		.putBoolean("firstrun", false)
	    		.commit();
	    }
	     
	    String newVersion = getString(R.string.cur_version);
	    
	    //if current version is different than prev saved version
	    
	    /*
	    
	    No clue why this isnt working
	    
	    
	    */
	    
	    /*
	    
	    Toast toast = Toast.makeText(this, "Shared = " + version, Toast.LENGTH_SHORT);
		toast.show();
		
		Toast toast2 = Toast.makeText(this, "String = " + getString(R.string.cur_version), Toast.LENGTH_SHORT);
		toast2.show();
		
		if (!version.equals(newVersion)){	
			Toast toast3 = Toast.makeText(this, "Not Equal" , Toast.LENGTH_SHORT);
			toast3.show();
		}
		
		if (version.equals(newVersion)){	
			Toast toast4 = Toast.makeText(this, "Equal", Toast.LENGTH_SHORT);
			toast4.show();
		}
	    */

	    
	    
	    if (!version.equals(newVersion)){	
	    	
	    	//SharedPreferences.Editor editor = preferences.edit();
	    	
			showChangeLog(this, "Change Log");	//comment this to deactivate change log
			
			//editor.putString("version", getString(R.string.cur_version)).commit();
			
			/*Toast toast = Toast.makeText(this, getString(R.string.cur_version), Toast.LENGTH_SHORT);
			toast.show();*/
    	}
		
		runCount++;
		getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        .edit()
        .putInt("runCount", runCount)
        .commit();
		
		if(runCount == rateCount){
			showRatingDialog(this);	
	    }
	    

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// @Override
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.zero:
			onClickZero();
			return true;
		case R.id.calibrate:
			Intent myIntent = new Intent(MainActivity.this, Calibrate.class);
			myIntent.putExtra("newWeight", weight);
			MainActivity.this.startActivity(myIntent);
			return true;
		case R.id.about:
			Intent i = new Intent(MainActivity.this, About.class);
			startActivity(i);
			return true;
		case R.id.units:
			switchUnits();
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	
	public void showChangeLog(Activity activity, String title) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    CharSequence message = "If this is your first use, be sure to watch the video tutorial.\n\n" + 
	    		               "New in this release:\n\n" +
	    		               "-Rewritten from the ground up to improve speed and memory utilization.\n\n" +
	    		               "-Controls are now reached via the Menu button.";
	    
	    builder.setTitle(title);
	    builder.setMessage(message);
	    
	    builder.setPositiveButton("Watch Tutorial", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int whichButton)
	        {	        	
	        	//http://youtube.com/insidetheturtleshell
	        	//http://www.youtube.com/watch?v=9JaCmMhlQoQ
	        	
	        	startActivity(new Intent(Intent.ACTION_VIEW, 
	        			          Uri.parse("http://www.youtube.com/watch?v=9JaCmMhlQoQ")));
	        	}
	    });
	    
	    builder.setNegativeButton("OK", null);
	    builder.show();
	}
	
	public void showRatingDialog(Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		String title = "Rate Now";
		CharSequence message = "If you like this app, please support the Developers by raiting us on Google Play!";
	        
		if (title != null)
	        builder.setTitle(title);
	    builder.setMessage(message);
		
	    builder.setNegativeButton("Rate Now", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int whichButton)
	        {	        	
	        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=appinventor.ai_cwilson.Mobile_Digital_Scale"));
		    	startActivity(browserIntent);
	        }
	    });
	    
	    builder.setNeutralButton("Later", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int whichButton)
	        {	        	
	        	int rateCount = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("rateCount", 3);
	        	 
	        	getSharedPreferences("PREFERENCE", MODE_PRIVATE)
	    		.edit()
	    		.putInt("rateCount", rateCount+(rateCount/2)+2)	//shows 'rate me' dialog on runs 3, 6, 11, 19 etc.
	    		.commit();
	        }
	    });
	    
	    builder.setPositiveButton("Rate Never", null);

	    builder.show();
	}

	public void onClickScale(View view) {

		Handler handler = new Handler();

		if (weight == 0) {
			//Not yet calibrated!
			Toast toast = Toast.makeText(this, "Use Menu > Calibrate to initialize the scale.", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0 , 110);
			toast.show();
		}

		else {

			updateWeight(weight * (Math.random() % 100));

			// ugly as FUCK but works for now
			// need to work on more elegant solution

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					updateWeight(weight * (Math.random() % 100));
				}
			}, TIME / 2);

			// bounce up
			if (bounce == false) {
				bounce = true;
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						updateWeight(weight);
					}
				}, TIME);// was 800
			}

			// bounce down
			else {
				bounce = false;
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						updateWeight(0.0);
					}
				}, TIME);
			}
		}

	}

	public void onClickZero() // zero out scale
	{
		updateWeight(0); // set weight to 0;
		bounce = false; // reset bounce bool
	}

	public void updateWeight(double newWeight) { // update weight output textbox
		TextView textViewWeight = (TextView) findViewById(R.id.textViewWeight);

		if (newWeight == 0) { // if object is removed
			textViewWeight.setText("0.0" + " " + unit);
		}

		else { // if object is placed
			textViewWeight.setText(String.format("%.2f", newWeight) + " "
					+ unit); // String.format to set decimal precision
		}

	}

	public void switchUnits() {
		TextView textViewWeight = (TextView) findViewById(R.id.textViewWeight);

		if (unit.contains("g")) {
			unit = "oz";
			weight = weight * 0.035274; // ounces = grams * 0.035274
		}

		else {
			unit = "g";
			weight = weight / 0.035274; // grams = ounces / 0.035274
		}

		if (!textViewWeight.getText().toString().contains("0.0")) {
			updateWeight(weight);
		}

		else {
			textViewWeight.setText("0.0 " + unit);
		}

	}

	public void updateUnit() {
		TextView textViewWeight = (TextView) findViewById(R.id.textViewWeight);

		textViewWeight.setText("0.0 " + unit);

	}

}
