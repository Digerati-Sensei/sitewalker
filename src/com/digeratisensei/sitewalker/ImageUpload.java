package com.digeratisensei.sitewalker;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class ImageUpload extends Activity {
	final int CAMERA_PIC_REQUEST = 1337;
	private String LOG_NAME = "ImageUpload";
	private String tempPhotoUri = "/sdcard/tmp.jpg";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageupload);
        
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempPhotoUri)));
        startActivityForResult(cameraIntent, 0);
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	Log.d(LOG_NAME, "resultCode = "+resultCode);
    	if (resultCode == RESULT_OK){
    		Log.d(LOG_NAME, "good result.");
	    	if (requestCode == 0) {
	    		Log.d(LOG_NAME, "good request.");
	    		Uri imageURI = data.getData();
	    		Log.d(LOG_NAME, "Attempting to get URI");
	    		//Log.d(LOG_NAME, "URI="+imageURI.toString());
	    		//Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	    		//ImageView image = (ImageView) findViewById(R.id.photoResultView);  
	    		//image.setImageBitmap(thumbnail);
	    		String imagePath = getRealPathFromURI(imageURI);
	    		Log.d(LOG_NAME, "file is at: "+imagePath);
	    		File file = new File(imagePath);
	    		try {
	    		     HttpClient client = new DefaultHttpClient();
	    		     String postURL = "http://someposturl.com";
	    		     HttpPost post = new HttpPost(postURL);
	    		     FileBody bin = new FileBody(file);
	    		     MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	    		     reqEntity.addPart("myFile", bin);
	    		     //reqEntity.addPart("dataObject", data.getData());
	    		     post.setEntity(reqEntity);  
	    		     HttpResponse response = client.execute(post);  
	    		     HttpEntity resEntity = response.getEntity();  
	    		     if (resEntity != null) {    
	    		               Log.i("RESPONSE",EntityUtils.toString(resEntity));
	    		         }
	    		} catch (Exception e) {
	    		    e.printStackTrace();
	    		}
	    	}
    	}
    }
	
	public String getRealPathFromURI(Uri contentUri) {
		Log.d(LOG_NAME, "converting URI to path string");
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Log.d(LOG_NAME, "set proj string.");
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        Log.d(LOG_NAME, "set cursor");
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        Log.d(LOG_NAME, "set column_index");
        cursor.moveToFirst();
        Log.d(LOG_NAME, "Moved to first.");
        Log.d(LOG_NAME, "Returning path string as");
        return cursor.getString(column_index);
}
}
