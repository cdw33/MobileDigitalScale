package appinventor.ai_cwilson.Mobile_Digital_Scale;

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

	Activity activity;

	//Shared Preferences
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	final static String RUN_COUNT_KEY = "runcount";
	final static String RATING_DIALOG_KEY = "showRating";
	int runCount;
	boolean showRatingDialog;

	AutoResizeTextView textViewWeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		activity = this;

		initTextViews();

		initSharedPreferences();

		processRunCountChecks();

		resetScaleLCD();

		//Toast.makeText(MainActivity.this, "RunCount: " + runCount + " - ShowRating: " + showRatingDialog, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.zero:
				onClickZero();
				return true;
			case R.id.calibrate:
				showCalibrateDialog();
				return true;
			case R.id.about:
				showAboutDialog();
				return true;
			case R.id.units:
				switchUnits();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void incrementRunCounter(){
		runCount++;

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		editor = sharedPreferences.edit();

		editor.putInt(RUN_COUNT_KEY, runCount).commit();
	}

	public void processRunCountChecks(){
		if(runCount == 1){
			showChangeLog();
		}
		else if(showRatingDialog && runCount % 3 == 0){ //Only show rating dialog every 3 runs while showRatingDialog sharedPref is true
			showRatingDialog();
		}
	}

	public void initSharedPreferences() {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		runCount = sharedPreferences.getInt(RUN_COUNT_KEY, 0);
		showRatingDialog = sharedPreferences.getBoolean(RATING_DIALOG_KEY, true);

		incrementRunCounter();
	}

	public void initTextViews(){
		textViewWeight = (AutoResizeTextView) findViewById(R.id.textViewWeight);
	}

	public void showChangeLog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		CharSequence message = "If this is your first use, be sure to watch the video tutorial.\n\n" +
				"New in this release:\n\n" +
				"•Rewritten from the ground up to improve speed and memory utilization.\n\n" +
				"•Fixed layout issues with certain high DPI devices\n\n" +
				"•Updated Calibration and About dialogs";

		builder.setTitle("Change Log");
		builder.setMessage(message);

		builder.setPositiveButton("Watch Tutorial", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				//http://youtube.com/insidetheturtleshell
				//http://www.youtube.com/watch?v=9JaCmMhlQoQ

				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://www.youtube.com/watch?v=9JaCmMhlQoQ")));
			}
		});

		builder.setNegativeButton("OK", null);
		builder.show();
	}

	public void showRatingDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		String title = "Rate Now";
		CharSequence message = "If you like this app, please support the Developers by rating us on Google Play!";

		builder.setTitle(title);
		builder.setMessage(message);

		builder.setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=appinventor.ai_cwilson.Mobile_Digital_Scale"));
				startActivity(browserIntent);

				editor.putBoolean(RATING_DIALOG_KEY, false).commit();
			}
		});

		builder.setNeutralButton("Later", null);

		builder.setNegativeButton("Rate Never", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				editor.putBoolean(RATING_DIALOG_KEY, false).commit();
			}
		});

		builder.show();
	}

	public void onClickScale(View view) {

		Handler handler = new Handler();

		if (weight == 0) {
			//Not yet calibrated!
			Toast toast = Toast.makeText(this, "Use Menu > Calibrate to initialize the scale.", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0 , 110);
			toast.show();
		} else {

			displayWeight(weight * (Math.random() % 100));

			// ugly as FUCK but works for now
			// need to work on more elegant solution

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					displayWeight(weight * (Math.random() % 100));
				}
			}, TIME / 2);

			// bounce up
			if (!bounce) {
				bounce = true;
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						displayWeight(weight);
					}
				}, TIME);// was 800
			}

			// bounce down
			else {
				bounce = false;
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						displayWeight(0.0);
					}
				}, TIME);
			}
		}

	}

	public void onClickZero(){ //Reset scale to zero
		displayWeight(0); // set weight to 0;
		bounce = false; // reset bounce bool
	}

	public void displayWeight(double newWeight) { //Update LCD TextBox
		if (newWeight == 0) { // if object is removed
			textViewWeight.setText("0.0" + " " + unit);
		}
		else { // if object is placed
			textViewWeight.setText(String.format("%.2f", newWeight) + " " + unit);
		}
	}

	public void switchUnits() {
		if (unit.contains("g")) {
			unit = "oz";
			weight = convertGramsToOunces(weight);
		}
		else {
			unit = "g";
			weight = convertOuncesToGrams(weight);
		}
		if (textViewWeight.getText().toString().contains("0.0")) {
			resetScaleLCD();
		} else {
			displayWeight(weight);
		}
	}

	public double convertGramsToOunces(double grams){
		return grams * 0.035274; //ounces = grams * 0.035274
	}

	public double convertOuncesToGrams(double ounces){
		return ounces / 0.035274; //grams = ounces / 0.035274
	}

	public void resetScaleLCD() {
		textViewWeight.setText("0.0 " + unit);
	}

	public void showCalibrateDialog(){
		LayoutInflater li = LayoutInflater.from(this);
		View calibrateView = li.inflate(R.layout.calibrate_dialog, null);

		final EditText userInput = (EditText) calibrateView.findViewById(R.id.weightInputTextBox);

		final CheckBox Checkbox_g = (CheckBox) calibrateView.findViewById(R.id.gramsCheckbox);
		final CheckBox Checkbox_o = (CheckBox) calibrateView.findViewById(R.id.ouncesCheckBox);

		final Button clearButton = (Button) calibrateView.findViewById(R.id.clearButton);

		final AlertDialog calibrateDialog = new AlertDialog.Builder(this)
				.setView(calibrateView)
				.setTitle("Calibrate")
				.setPositiveButton("Save", null) //Set to null. We override the onclick
				.setNegativeButton(android.R.string.cancel, null)
				.create();

		calibrateDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				//Handle "Clear" button press
				clearButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						userInput.setText("");
					}
				});

				//Handle "Save" button press
				Button b = calibrateDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						if (!Checkbox_g.isChecked() && !Checkbox_o.isChecked()) { //no units selected
							Toast.makeText(activity, "Please select units.", Toast.LENGTH_SHORT).show();
						} else if (userInput.getText().toString().equals("")) { //no weight set
							Toast.makeText(activity, "Please input weight.", Toast.LENGTH_SHORT).show();
						} else if (Checkbox_g.isChecked() && Double.valueOf(userInput.getText().toString()) > 999.99) { //weight about max for grams
							Toast.makeText(activity, "Too much weight for grams.", Toast.LENGTH_SHORT).show();
						} else if (Checkbox_o.isChecked() && Double.valueOf(userInput.getText().toString()) > 35.27) {  //weight about max for ounces
							Toast.makeText(activity, "Too much weight for ounces.", Toast.LENGTH_SHORT).show();
						} else {
							//Set weight
							weight = Double.valueOf(userInput.getText().toString());

							Toast.makeText(activity, "Weight Set.", Toast.LENGTH_SHORT).show(); //weight set successfully!

							if (Checkbox_g.isChecked()) { //Set units string
								unit = "g";
							} else {
								unit = "oz";
							}

							resetScaleLCD();

							calibrateDialog.dismiss(); //exit dialog
						}
					}
				});
			}
		});

		calibrateDialog.show(); //Show dialog
	}

	public void showAboutDialog(){
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.about_dialog, null);

		final TextView playVideo = (TextView) promptsView.findViewById(R.id.video);

		final TextView appName = (TextView) promptsView.findViewById(R.id.textViewAppName);

		appName.setText(appName.getText() + getString(R.string.cur_version));

		final AlertDialog aboutDialog = new AlertDialog.Builder(this)
				.setView(promptsView)
				.setTitle("About")
				.setPositiveButton("Ok", null) //Set to null. We override the onclick
				.setNegativeButton("Close", null)
				.create();

		aboutDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				//Handle video link press
				playVideo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=9JaCmMhlQoQ"));
						startActivity(i);
					}
				});

				//Handle "Buy" button press
				Button b = aboutDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						aboutDialog.dismiss(); //exit dialog
					}
				});
			}
		});

		aboutDialog.show(); //Show dialog
	}
}
