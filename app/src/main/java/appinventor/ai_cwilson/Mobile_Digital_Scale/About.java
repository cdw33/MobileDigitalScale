package appinventor.ai_cwilson.Mobile_Digital_Scale;

import appinventor.ai_cwilson.Mobile_Digital_Scale.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;


public class About extends Activity{
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.about);
	    }
	  
	    public void onClickBack(View view) {
	        finish();
	    }
	  
	    public void onClickBuy(View view) {
	    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=appinventor.ai_cwilson.Mobile_Digital_Scale"));
	    	startActivity(browserIntent);
	    }
	    
	    public void onClickVideo(View view){
	    	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=9JaCmMhlQoQ"));
	    	startActivity(i); 
	    } 
	  
}
