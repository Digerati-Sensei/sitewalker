package com.digeratisensei.sitewalker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.digeratisensei.couchdb.Post;

public class ImageUpload extends Activity {
	final int CAMERA_PIC_REQUEST = 1337;
	private String LOG_NAME = "ImageUpload";
	private String tempPhotoUri = "/sdcard/tmp.jpg";
	private Uri tmpUri = Uri.fromFile(new File(tempPhotoUri));
	private Uri imageUri;
	private JSONObject jsondata = new JSONObject();
	//private StringBuilder myResponse = new StringBuilder();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageupload);
        try {
			jsondata.put("JSON", "Hello World!");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
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
	    		try {
					imageUri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), tempPhotoUri, null, null));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    		Log.d(LOG_NAME, "Attempting to get URI");
	    		//Log.d(LOG_NAME, "URI="+imageURI.toString());
	    		//Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	    		//ImageView image = (ImageView) findViewById(R.id.photoResultView);  
	    		//image.setImageBitmap(thumbnail);
	    		String imagePath = getRealPathFromURI(imageUri);
	    		Log.d(LOG_NAME, "file is at: "+imagePath);
	    		File file = new File(imagePath);
	    		try {
					jsondata.accumulate("image",new FileBody(file));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    		try {
	    			HttpClient client = new DefaultHttpClient();
	    			String postURL = "http://seansummers.couchone.com/tracker/";
	    			
	    			//Context context = getApplicationContext();
	    			//Intent postIntent = new Intent(context, Post.class);
	    			//postIntent.putExtra("jsonobject", jsondata)
	    		    JSONObject jsonObject = postToCouchDb(postURL,jsondata);
	    		    
	    		    Log.d(LOG_NAME, "RESPONSE " + jsonObject.toString());
	    		    postURL = postURL + jsonObject.get("id") +"/image.jpg?rev="+ jsonObject.get("rev");
	    		    Log.d(LOG_NAME, "postURL:" + postURL);
	    		    HttpPost post = new HttpPost(postURL);
	    		    FileBody bin = new FileBody(file);
	    		    try {
	    		    	MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	    		    	
	    		    	reqEntity.addPart("type", new StringBody("photo"));
	    		    	reqEntity.addPart("data", bin);
	    		    	post.setEntity(reqEntity);  
	    		    	HttpResponse response = client.execute(post);  
	    		    	HttpEntity resEntity = response.getEntity();
	    		    	if (resEntity != null) {    
	    		               Log.i("RESPONSE",EntityUtils.toString(resEntity));
	    		        }
	    		    } catch (ClientProtocolException e) {
	    		    	e.printStackTrace();
	    		    } catch (IOException e) {
	    		    	e.printStackTrace();
	    		    }
	    		} catch (Exception e) {
	    		    e.printStackTrace();
	    		}
	    	}
    	}
    }
	/*@Override
	protected void onSaveInstanceState() {
		super onSaveInstanceState();
		tmpUri = Uri.fromFile(new File(tempPhotoUri));
	}
	*/
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
	
	public JSONObject postToCouchDb(String postUrl, JSONObject data) {
		 HttpClient client = new DefaultHttpClient();
	     String postURL = "http://seansummers.couchone.com/tracker/";
	     HttpPost post = new HttpPost(postURL);
	     post.setHeader("Accept", "application/json");
	     post.addHeader("Content-type", "application/json;charset=UTF-8");
	     try {
			post.setEntity(new StringEntity(data.toString()));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	     
	     HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
	     
		JSONObject jsonobject = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			JSONTokener tokener = new JSONTokener(reader.readLine());
			jsonobject = new JSONObject(tokener);
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	     return jsonobject;
	}
	
}
