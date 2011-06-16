package com.digeratisensei.sitewalker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Projects extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projects);
        Button sdbutton = (Button)findViewById(R.id.projButton0);
        sdbutton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	    		/** Context context = getApplicationContext(); */
	        	Intent mapperIntent = new Intent(v.getContext(), Mapper.class);
	        	startActivityForResult(mapperIntent, 0);
	        }
        });
        Button tsbutton = (Button)findViewById(R.id.projButton1);
        tsbutton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	    		/** Context context = getApplicationContext(); */
	        	Intent mapperIntent = new Intent(v.getContext(), Mapper.class);
	        	startActivityForResult(mapperIntent, 0);
	        }
        });
        startService(new Intent(Projects.this,GatherReadings.class));
    }
}
