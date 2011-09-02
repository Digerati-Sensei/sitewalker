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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;


public class GatherReadings extends Service {
	
	private static final String TAG = "GatherReadings";
	private static final int PHONE_TYPE_CDMA = 2;
	private static final int PHONE_TYPE_GSM = 1;
	
	public TelephonyManager telephonyManager;
	
	private GsmCellLocation cellGsmLocation;
	private CdmaCellLocation cellLocation;
	private JSONObject reading = new JSONObject();
	private JSONObject phone = new JSONObject();
	private int phone_type;
	public PhoneStateListener listener;
	private static String postUrl; // = new String("http://localhost:5984/tracker/");
	private static String couchDb;
	public static final String APP_PREFS = "SiteWalkerPrefs";
	
	/*
	public GatherReadings() {
		super("GatherReadings");
		// TODO Auto-generated constructor stub
	}
	*/
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreate.");
		Handler handler = new Handler();
		//handler.setDaemon(true);
		SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
		postUrl = settings.getString("couchUrl", "http://localhost:5984/");
		couchDb = settings.getString("couchDb", "tracker");
		handler.post(new Runnable() {
			public void run() {
				try {
					Log.i(TAG, "startReadingService");
					startReadingService();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/*
	protected void onHandleIntent(Intent intent) {
		//postUrl = new String(intent.getStringExtra("url"));
		
		Log.i(TAG, "received url: "+postUrl);
		try {
			startReadingService();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void startReadingService() throws InterruptedException {
		Log.i(TAG, "Starting reading service.");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		listener = new PhoneStateListener() {
		    @Override
		    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		    	Log.i(TAG, "Listening to Signal Strengths Changed.");
		    	telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				phone_type = telephonyManager.getPhoneType();
				Log.i(TAG, "phone_type: "+phone_type);
				try {
					phone.put("phone_type", phone_type);
				    phone.put("network_operator_name", telephonyManager.getNetworkOperatorName());
				    phone.put("network_operator", telephonyManager.getNetworkOperator());
				    phone.put("network_type", telephonyManager.getNetworkType());
				    phone.put("device_id", telephonyManager.getDeviceId());
				    phone.put("software_ver", telephonyManager.getDeviceSoftwareVersion());
				    phone.put("timestamp", System.currentTimeMillis());
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	switch (phone_type){
				    case PHONE_TYPE_CDMA:
			    		cellLocation =(CdmaCellLocation) telephonyManager.getCellLocation();
			    		try {
							reading.put("base_station", cellLocation.getBaseStationId());
							reading.put("base_station_lat", cellLocation.getBaseStationLatitude());
							reading.put("base_station_long", cellLocation.getBaseStationLongitude());
							reading.put("system_id", cellLocation.getSystemId());
							reading.put("network_id", cellLocation.getNetworkId());
							reading.put("cdma_cell_location", cellLocation.toString());
							reading.put("cdma_ecio", signalStrength.getCdmaEcio());
							reading.put("cdma_dbm", signalStrength.getCdmaDbm());
							reading.put("evdo_dbm", signalStrength.getEvdoDbm());
							reading.put("evdo_ecio", signalStrength.getEvdoEcio());
							reading.put("evdo_snr", signalStrength.getEvdoSnr());
			    		} catch (JSONException e) {
			    			e.printStackTrace();
			    		}
						break;
				    	
			    	case PHONE_TYPE_GSM:
			    		try {
			    			cellGsmLocation = (GsmCellLocation) telephonyManager.getCellLocation();
			    			reading.put("gsm_Cid", cellGsmLocation.getCid());
			    			reading.put("gsm_Lac", cellGsmLocation.getLac());
			    			//gsm_Psc = ((NeighboringCellInfo) cellGsmLocation).getPsc();
			    			reading.put("gsm_cell_location", cellGsmLocation.toString());
			    			reading.put("gsm_ss", signalStrength.getGsmSignalStrength());
			    			reading.put("gsm_ber", signalStrength.getGsmBitErrorRate());
			    		} catch (JSONException e) {
			    			e.printStackTrace();
			    		}
			    		break;
		    	}

		    	try {
					phone.put("reading", reading);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				postReading(phone.toString());
				/*
				Intent postIntent = new Intent(getApplicationContext(), Post.class);
				postIntent.putExtra("url", postUrl);
				Log.i(TAG, "sending url: "+postUrl);
		    	postIntent.putExtra("post", phone.toString());
		    	startService(postIntent);
		    	
		    	List<NeighboringCellInfo> neigh_cells = telephonyManager.getNeighboringCellInfo();
		    	for(NeighboringCellInfo info:neigh_cells) {
		    		cellid = info.getCid();
		    		rssi = info.getRssi();
		    		network_type_this = info.getNetworkType();
		    	}
		    	*/
		    }
	    };
	    Log.i(TAG, "starting telephonyManager"+listener.toString()+", "+PhoneStateListener.LISTEN_SIGNAL_STRENGTHS+", ");
	    telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	
	private JSONObject postReading(String data) {
		HttpClient httpclient = new DefaultHttpClient();
		Log.i(TAG, data);
		HttpPost post = new HttpPost(postUrl + couchDb);
	    post.setHeader("Accept", "application/json");
	    post.addHeader("Content-type", "application/json;charset=UTF-8");
	    try {
			post.setEntity(new StringEntity(data));
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
		return jsonobject;
	}
}
