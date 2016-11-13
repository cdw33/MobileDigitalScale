package com.turtleshell.mds;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;

import java.util.Locale;

public class MainActivity extends Activity {

	double weight = 0.0; 	// weight
	boolean bounce = false; // indicates scale status for bounce up/down
	static int TIME = 350;	// bounce time in milliseconds
	final static double g2ozFactor = 0.035274; // conversion factor for grams to ounces

	enum Units{
		grams,
		ounces
	}

	Units unit = Units.grams; // Default to grams

	// Link URLs
	final String YOUTUBE_TUTORIAL_URL    = "http://www.youtube.com/watch?v=9JaCmMhlQoQ";
	final String MDS_MARKET_URL 	     = "market://details?id=com.turtleshell.mds";
	final String MDS_MARKET_URL_FALLBACK = "http://play.google.com/store/apps/details?id=com.turtleshell.mds";

	Activity activity;

	// Shared Preferences
	SharedPreferences sharedPreferences;
	final static String RUN_COUNT_KEY = "runcount";
	final static String RATING_DIALOG_KEY = "showRating";
	int runCount;
	boolean showRatingDialog;

	// LCD TextView
	TextView textViewWeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		activity = this;

		initTextViews();

		initSharedPreferences();

		processRunCountChecks();

		zeroScaleLCD();
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

		sharedPreferences.edit()
				.putInt(RUN_COUNT_KEY, runCount)
				.apply();
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
		textViewWeight = (TextView) findViewById(R.id.textViewWeight);
	}

	public void showChangeLog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		CharSequence message = getString(R.string.changelog_msg);

		builder.setTitle(R.string.changelog);
		builder.setMessage(message);

		builder.setPositiveButton(R.string.watch_tutorial, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				//http://youtube.com/insidetheturtleshell
				//http://www.youtube.com/watch?v=9JaCmMhlQoQ

				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(YOUTUBE_TUTORIAL_URL)));
			}
		});

		builder.setNegativeButton(R.string.ok, null);
		builder.show();
	}

	public void showRatingDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		String title = getString(R.string.rate_now);
		CharSequence message =  getString(R.string.rate_message);

		builder.setTitle(title);
		builder.setMessage(message);

		builder.setPositiveButton(getString(R.string.rate_now), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MDS_MARKET_URL));

				try {
					startActivity(browserIntent);
				} catch (Exception e) {
					browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MDS_MARKET_URL_FALLBACK));
					startActivity(browserIntent);
				}

				sharedPreferences.edit()
						.putBoolean(RATING_DIALOG_KEY, false)
						.apply();
			}
		});

		builder.setNeutralButton(getString(R.string.rate_later), null);

		builder.setNegativeButton( getString(R.string.rate_never), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				sharedPreferences.edit()
						.putBoolean(RATING_DIALOG_KEY, false)
						.apply();
			}
		});

		builder.show();
	}

	public void onClickTogglePower(View view) {
		if(textViewWeight.getVisibility() == View.VISIBLE ){
			textViewWeight.setVisibility(View.INVISIBLE);
		}
		else {
			onClickZero();
			textViewWeight.setVisibility(View.VISIBLE);
		}
	}

	public void onClickScale(View view) {

		Handler handler = new Handler();

		if (weight == 0) {

			final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			String title = getString(R.string.not_calibrated_title);
			CharSequence message = getString(R.string.not_calibrated_msg);

			builder.setTitle(title);
			builder.setMessage(message);

			builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					showCalibrateDialog();
				}
			});

			builder.setNeutralButton(getString(R.string.no), null);

			builder.show();

		} else {

			displayWeight(weight * (Math.random() % 100));

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
				}, TIME);
			}

			// bounce down
			else {
				bounce = false;
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						zeroScaleLCD();
					}
				}, TIME);
			}
		}
	}

	public void onClickZero(){ //Reset scale to zero
		zeroScaleLCD();
		bounce = false; // reset bounce bool
	}

	public void displayWeight(double newWeight) { //Update LCD TextBox
		String unitAbbr = (unit == Units.grams ? getString(R.string.grams_abbr) : getString(R.string.ounces_abbr));

		if (newWeight == 0) { // if object is removed
			textViewWeight.setText("0.0" + " " + unitAbbr);
		}
		else { // if object is placed
			textViewWeight.setText(String.format(Locale.US, "%.2f", newWeight) + " " + unitAbbr);
		}
	}

	public void switchUnits() {
		if (unit == Units.grams) {
			unit = Units.ounces;
			weight = convertGramsToOunces(weight);
		}
		else {
			unit = Units.grams;
			weight = convertOuncesToGrams(weight);
		}

		if (textViewWeight.getText().toString().contains(getString(R.string.zero_weight))) {
			zeroScaleLCD();
		} else {
			displayWeight(weight);
		}
	}

	public double convertGramsToOunces(double grams){
		return grams * g2ozFactor; //ounces = grams * 0.035274
	}

	public double convertOuncesToGrams(double ounces){
		return ounces / g2ozFactor; //grams = ounces / 0.035274
	}

	public void zeroScaleLCD() {
		displayWeight(0.0);
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
				.setTitle(R.string.title_calibrate)
				.setPositiveButton(R.string.save, null)
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
							Toast.makeText(activity, R.string.cal_error_units_empty, Toast.LENGTH_SHORT).show();
						} else if (userInput.getText().toString().equals("")) { //no weight set
							Toast.makeText(activity, R.string.cal_error_weight_empty, Toast.LENGTH_SHORT).show();
						} else if (Checkbox_g.isChecked() && Double.valueOf(userInput.getText().toString()) > 999.99) { //weight about max for grams
							Toast.makeText(activity, R.string.cal_error_gram_max, Toast.LENGTH_SHORT).show();
						} else if (Checkbox_o.isChecked() && Double.valueOf(userInput.getText().toString()) > 35.27) {  //weight about max for ounces
							Toast.makeText(activity, R.string.cal_error_ounce_max, Toast.LENGTH_SHORT).show();
						} else {
							weight = Double.valueOf(userInput.getText().toString());

							Toast.makeText(activity, R.string.calibrate_success, Toast.LENGTH_SHORT).show(); //weight set successfully!

							if (Checkbox_g.isChecked()) { //Set units string
								unit = Units.grams;
							} else {
								unit = Units.ounces;
							}

							onClickZero();

							calibrateDialog.dismiss();
						}
					}
				});

				//Handle Checkboxes
				final CheckBox cbGrams = (CheckBox) calibrateDialog.findViewById(R.id.gramsCheckbox);
				final CheckBox cbOunces = (CheckBox) calibrateDialog.findViewById(R.id.ouncesCheckBox);

				//Check currently selected unit checkbox
				if(unit == Units.grams){
					cbGrams.setChecked(true);
				}
				else{
					cbOunces.setChecked(true);
				}

				//Handle checkbox clicks - only one should be selected at a time
				cbGrams.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (cbGrams.isChecked() && cbOunces.isChecked()) {
							cbOunces.setChecked(false);
						}
					}
				});

				cbOunces.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (cbOunces.isChecked() && cbGrams.isChecked()) {
							cbGrams.setChecked(false);
						}
					}
				});
			}
		});

		calibrateDialog.show();
	}

	public void showAboutDialog(){
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.about_dialog, null);

		final TextView playVideo = (TextView) promptsView.findViewById(R.id.video);

		final TextView appName = (TextView) promptsView.findViewById(R.id.textViewAppName);

		// Build title string in the form 'AppName v#.#'
		final String appNameVersion = appName.getText() + " " + getString(R.string.ab_version_abbr) + getString(R.string.cur_version);
		appName.setText(appNameVersion);

		final AlertDialog aboutDialog = new AlertDialog.Builder(this)
				.setView(promptsView)
				.setTitle(R.string.title_about)
				.setNegativeButton(R.string.close, null)
				.create();

		aboutDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				//Handle video link press
				playVideo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_TUTORIAL_URL));
						startActivity(i);
					}
				});
			}
		});

		aboutDialog.show();
	}
}
