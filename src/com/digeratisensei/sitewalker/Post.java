package com.digeratisensei.sitewalker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;



public class Post extends IntentService {
	
	public Post() {
		super("Post");
		// TODO Auto-generated constructor stub
	}

	private static final String TAG = "Post";
	private Bundle reading;
	private Bundle phone;
	private HttpClient httpclient = new DefaultHttpClient();
	private String postURL = "http://seansummers.couchone.com/tracker/";
	
	protected void onHandleIntent(Intent intent) {
		phone = new Bundle(intent.getBundleExtra("phone"));
		reading = new Bundle(intent.getBundleExtra("reading"));
		postReading(reading);
	}
	
	private JSONObject postReading(Bundle data) {
		HttpPost post = new HttpPost(postURL);
	    post.setHeader("Accept", "application/json");
	    post.addHeader("Content-type", "application/json;charset=UTF-8");
	    Gson gson = new Gson();
	    String json = gson.toJson(data);
	    try {
			post.setEntity(new StringEntity(json));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	     
	    HttpResponse response = null;
		try {
			response = httpclient.execute(post);
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
