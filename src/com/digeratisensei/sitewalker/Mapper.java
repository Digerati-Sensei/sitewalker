package com.digeratisensei.sitewalker;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class Mapper extends Activity {
	RelativeLayout layout;
	TouchImageView tiv1;
	String fpImage;
	Bitmap selectedImage;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapper);
        
        layout = (RelativeLayout) findViewById(R.id.relLayout1);
        
        fpImage = (String) getIntent().getStringExtra("floorplan");
        tiv1 = new TouchImageView(Mapper.this);
        
        selectedImage = (Bitmap) getImgFromFile(new File (fpImage));
        
        
	}
	
	public static Bitmap getImgFromFile(File file) {
        Bitmap pic = BitmapFactory.decodeFile(file.toString());
        if (pic == null) {
                //printDebug("    Tried to read image: " + file.toString());
                //printDebug("    Image from file is null");
        }
        return pic;
}
}
