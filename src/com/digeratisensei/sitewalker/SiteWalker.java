package com.digeratisensei.sitewalker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SiteWalker extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button)findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	    		/** Context context = getApplicationContext(); */
	        	//Intent projectIntent = new Intent(v.getContext(), ImageUpload.class);
	        	Intent projectIntent = new Intent(v.getContext(), Projects.class);
	        	startActivityForResult(projectIntent, 0);
	        }
        });
    }
}