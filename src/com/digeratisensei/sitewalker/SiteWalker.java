package com.digeratisensei.sitewalker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.couchbase.libcouch.AndCouch;
import com.couchbase.libcouch.CouchDB;
import com.couchbase.libcouch.ICouchClient;

public class SiteWalker extends Activity {
	private final SiteWalker self = this;
	protected static final String TAG = "CouchAppActivity";
	public static final String APP_PREFS = "SiteWalkerPrefs";
	public static final String DB_NAME = "tracker";
	
	private ServiceConnection couchServiceConnection;
	private ProgressDialog installProgress;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*
        Button button = (Button)findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	    		//Intent projectIntent = new Intent(v.getContext(), ImageUpload.class);
	        	Intent projectIntent = new Intent(v.getContext(), Map.class);
	        	startActivityForResult(projectIntent, 0);
	        }
        });
        */
        startCouch();
    }
    
    @Override
	public void onRestart() {
		super.onRestart();
		startCouch();
	}

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	stopService(new Intent(this, GatherReadings.class));
    	try {
			unbindService(couchServiceConnection);
		} catch (IllegalArgumentException e) {
		}
    	System.exit(0);
    }
    
    private final ICouchClient mCallback = new ICouchClient.Stub() {
		public void couchStarted(String host, int port) {

			if (installProgress != null) {
				installProgress.dismiss();
			}

			String url = "http://" + host + ":" + Integer.toString(port) + "/";
		    
			ensureDesignDoc("tracker", url);
			
			SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
			SharedPreferences.Editor editor = settings.edit();
		    editor.putString("couchUrl", url);
		    editor.putString("couchDb", "tracker");
		    editor.putString("mainUrl", "http://www.digeratisensei.com:5984/tracker/");
		    editor.commit();
		    startReplication(url);
			Intent intent = new Intent(SiteWalker.this, Map.class);
			startActivity(intent);
		}

		public void installing(int completed, int total) {
			ensureProgressDialog();
			installProgress.setTitle("Initialising CouchDB");
			installProgress.setProgress(completed);
			installProgress.setMax(total);
		}

		public void exit(String error) {
			//Log.v(TAG, error);
			couchError();
		}
	};
    
    private void startCouch() {
		couchServiceConnection = CouchDB.getService(getBaseContext(), null, "release-0.1", mCallback);
	}
    
    private void ensureProgressDialog() {
		if (installProgress == null) {
			installProgress = new ProgressDialog(SiteWalker.this);
			installProgress.setTitle(" ");
			installProgress.setCancelable(false);
			installProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			installProgress.show();
		}
	}
    
    private void couchError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(self);
		builder.setMessage("Error")
				.setPositiveButton("Try Again?",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startCouch();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								self.moveTaskToBack(true);
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	* Will check for the existence of a design doc and if it doesnt exist,
	* upload the json found at dataPath to create it
	*/
	private void ensureDesignDoc(String dbName, String url) {

		try {
			String data = readAsset(getAssets(), dbName + ".json");
			String ddocUrl = url + dbName + "/_design/" + dbName;

			AndCouch req = AndCouch.get(ddocUrl);

			if (req.status == 404) {
				AndCouch.put(url + dbName, null);
				AndCouch.put(ddocUrl, data);
			}

		} catch (IOException e) {
			e.printStackTrace();
			// There is no design doc to load
		} catch (JSONException e) {
			e.printStackTrace();
		}
	};

	public static String readAsset(AssetManager assets, String path) throws IOException {
		InputStream is = assets.open(path);
		int size = is.available();
		byte[] buffer = new byte[size];
		is.read(buffer);
		is.close();
		return new String(buffer);
	}
	
	private void startReplication(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpPost post = new HttpPost(url + "_replicate");
	    post.setHeader("Accept", "application/json");
	    post.addHeader("Content-type", "application/json;charset=UTF-8");
	    try {
			post.setEntity(new StringEntity("{\"source\":\""+url+"tracker\""+",\"target\":\"http://www.digeratisensei.com:5984/tracker\", \"continuous\":true}"));
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
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}