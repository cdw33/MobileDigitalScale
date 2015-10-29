package appinventor.ai_cwilson.Mobile_Digital_Scale;

import appinventor.ai_cwilson.Mobile_Digital_Scale.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

//Settings is fucking ghey

public class Settings extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		checkEnabled();

	}

	public void onClickSetDefault() {
		EditText editTextDefault = (EditText) findViewById(R.id.EditTextSetDefaultWeight);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		String defaultWeight = preferences.getString("defaultWeight", "");
		defaultWeight = editTextDefault.getText().toString();

		if (!editTextDefault.getText().equals(defaultWeight.toString())) {

			getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
					.putString("defaultWeight", defaultWeight.toString())
					.commit();

			getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
					.putBoolean("defaultEnabled", true).commit();
		}
	}

	public void onClickEnableDefault(View view) {
		CheckBox checkBoxEnable = (CheckBox) findViewById(R.id.checkBoxDefaultWeight);
		EditText editTextDefault = (EditText) findViewById(R.id.EditTextSetDefaultWeight);

		if (checkBoxEnable.isChecked()) {
			editTextDefault.setEnabled(true);

			setSavedWeight();
		}

		else {
			editTextDefault.setEnabled(false);
		}
	}

	public void setSavedWeight() {
		EditText editTextDefault = (EditText) findViewById(R.id.EditTextSetDefaultWeight);

		editTextDefault
				.setHint(getSharedPreferences("PREFERENCE", MODE_PRIVATE)
						.getString("defaultWeight", ""));
	}

	public void checkEnabled() {
		EditText editTextDefault = (EditText) findViewById(R.id.EditTextSetDefaultWeight);
		CheckBox checkBoxEnable = (CheckBox) findViewById(R.id.checkBoxDefaultWeight);

		boolean defaultEnabled = getSharedPreferences("PREFERENCE",
				MODE_PRIVATE).getBoolean("defaultEnabled", false);
		if (defaultEnabled) {
			editTextDefault.setEnabled(true);
			checkBoxEnable.setChecked(true);

			setSavedWeight();
		}

		else {
			checkBoxEnable.setChecked(false);
			editTextDefault.setEnabled(false);
		}
	}

	public void onClickSave(View view) {

		onClickSetDefault();

		CheckBox checkBoxEnable = (CheckBox) findViewById(R.id.checkBoxDefaultWeight);

		boolean enabled = checkBoxEnable.isChecked();

		if (enabled) {
			getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
					.putBoolean("defaultEnabled", true).commit();
		}

		else {
			getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
					.putBoolean("defaultEnabled", false).commit();
		}

		finish();
	}

	public void checkInput() { // need to add checkboxs for grams and ounces

		// check input on default weights

		/*
		 * if(editTextInputNew.getText().toString().equals("")){
		 * Toast.makeText(this, "Please input weight.",
		 * Toast.LENGTH_SHORT).show(); //no weight pop up notification }
		 * 
		 * else if(Checkbox_g.isChecked() &&
		 * Double.valueOf(editTextInputNew.getText().toString()) > 999.99){
		 * Toast.makeText(this, "Too much weight for grams.",
		 * Toast.LENGTH_SHORT).show(); //no weight pop up notification }
		 * 
		 * else if(Checkbox_o.isChecked() &&
		 * Double.valueOf(editTextInputNew.getText().toString()) > 35.27){
		 * Toast.makeText(this, "Too much weight for ounces.",
		 * Toast.LENGTH_SHORT).show(); //no weight pop up notification }
		 */

	}

	public void onClickInput(View view) {// will not work cuz its disabled,
											// retard
		CheckBox checkBoxEnable = (CheckBox) findViewById(R.id.checkBoxDefaultWeight);

		if (!checkBoxEnable.isChecked()) {
			Toast toast = Toast.makeText(this, "Please enable Default Weight.",
					Toast.LENGTH_LONG);
			toast.show();
		}

	}

}
