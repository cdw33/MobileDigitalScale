package appinventor.ai_cwilson.Mobile_Digital_Scale;

import appinventor.ai_cwilson.Mobile_Digital_Scale.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Calibrate extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibrate);
    }
    
    
    public void onClickSave(View view){	//need to return weight
    	CheckBox Checkbox_g = (CheckBox) findViewById(R.id.checkBox_g);
    	CheckBox Checkbox_o = (CheckBox) findViewById(R.id.checkBox_o);
    	EditText editTextInputNew = (EditText) findViewById(R.id.editTextInputNew);
    	
    	if(!Checkbox_g.isChecked() && !Checkbox_o.isChecked()){
    		Toast.makeText(this, "Please select units.", Toast.LENGTH_SHORT).show();  		//no units selected pop up notification
    	}
    	
    	else if(editTextInputNew.getText().toString().equals("")){
    		Toast.makeText(this, "Please input weight.", Toast.LENGTH_SHORT).show();  		//no weight pop up notification
    	}
    	
    	else if(Checkbox_g.isChecked() && Double.valueOf(editTextInputNew.getText().toString()) > 999.99){
    		Toast.makeText(this, "Too much weight for grams.", Toast.LENGTH_SHORT).show();  		//no weight pop up notification
    	}
    	
    	else if(Checkbox_o.isChecked() && Double.valueOf(editTextInputNew.getText().toString()) > 35.27){
    		Toast.makeText(this, "Too much weight for ounces.", Toast.LENGTH_SHORT).show();  		//no weight pop up notification
    	}
    	
    	else{
    		
    		Toast.makeText(this, "Weight Set.", Toast.LENGTH_SHORT).show();  		//weight set pop up notification
    		
    		Intent intent = new Intent(this, MainActivity.class);
    		Bundle extras = new Bundle();
    		extras.putDouble("newWeight",Double.valueOf(editTextInputNew.getText().toString()));
    		
    		if(Checkbox_g.isChecked()){
    			extras.putString("UNIT","g");
    		}
    		
    		else{
    			extras.putString("UNIT","oz");
    		}  		
    		
    		intent.putExtras(extras);
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(intent);
    		
    	}
    }
    
    public void onClickCancel(View view){
    	finish();
    }
    
    
    public void onClickClear(View view){
    	EditText editTextInputNew = (EditText) findViewById(R.id.editTextInputNew);
    	editTextInputNew.setText(null);
    	
    }
    
    public void onClickCheckboxG(View view){
    	TextView textViewInputUnit = (TextView) findViewById(R.id.textViewInputUnit);
    	
    	CheckBox Checkbox_g = (CheckBox) findViewById(R.id.checkBox_g);
    	CheckBox Checkbox_o = (CheckBox) findViewById(R.id.checkBox_o);
  
    	Checkbox_g.setChecked(false);
    	Checkbox_o.setChecked(false);
    	
    	Checkbox_g.setChecked(true);
    	textViewInputUnit.setText("g  ");
    	

    }
    
    public void onClickCheckboxO(View view){
    	TextView textViewInputUnit = (TextView) findViewById(R.id.textViewInputUnit);
    	
    	CheckBox Checkbox_g = (CheckBox) findViewById(R.id.checkBox_g);
    	CheckBox Checkbox_o = (CheckBox) findViewById(R.id.checkBox_o);
    	
    	Checkbox_g.setChecked(false);
		Checkbox_o.setChecked(false);
		
		Checkbox_o.setChecked(true);
		
		textViewInputUnit.setText("oz");
    }
    
    
    
    
}
