package com.digeratisensei.sitewalker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	private Bundle reading = new Bundle();
	private Bundle phone = new Bundle();
	private int phone_type;
	public PhoneStateListener listener;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreate.");
		Handler handler = new Handler();
		//handler.setDaemon(true);
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
		
		//thread.start();
	}

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
					phone.putInt("phone_type", phone_type);
				    phone.putString("network_operator_name", telephonyManager.getNetworkOperatorName());
				    phone.putString("network_operator", telephonyManager.getNetworkOperator());
				    phone.putInt("network_type", telephonyManager.getNetworkType());
				    phone.putString("device_id", telephonyManager.getDeviceId());
				    phone.putString("software_ver", telephonyManager.getDeviceSoftwareVersion());
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
		    	switch (phone_type){
				    case PHONE_TYPE_CDMA:
			    		cellLocation =(CdmaCellLocation) telephonyManager.getCellLocation();
			    		try {
							reading.putInt("base_station", cellLocation.getBaseStationId());
							reading.putInt("base_station_lat", cellLocation.getBaseStationLatitude());
							reading.putInt("base_station_long", cellLocation.getBaseStationLongitude());
							reading.putInt("system_id", cellLocation.getSystemId());
							reading.putInt("network_id", cellLocation.getNetworkId());
							reading.putString("cdma_cell_location", cellLocation.toString());
							reading.putInt("cdma_ecio", signalStrength.getCdmaEcio());
							reading.putInt("cdma_dbm", signalStrength.getCdmaDbm());
							reading.putInt("evdo_dbm", signalStrength.getEvdoDbm());
							reading.putInt("evdo_ecio", signalStrength.getEvdoEcio());
							reading.putInt("evdo_snr", signalStrength.getEvdoSnr());
			    		} catch (NullPointerException e) {
			    			e.printStackTrace();
			    		}
						break;
				    	
			    	case PHONE_TYPE_GSM:
			    		try {
			    			cellGsmLocation = (GsmCellLocation) telephonyManager.getCellLocation();
			    			reading.putInt("gsm_Cid", cellGsmLocation.getCid());
			    			reading.putInt("gsm_Lac", cellGsmLocation.getLac());
			    			//gsm_Psc = ((NeighboringCellInfo) cellGsmLocation).getPsc();
			    			reading.putString("gsm_cell_location", cellGsmLocation.toString());
			    			reading.putInt("gsm_ss", signalStrength.getGsmSignalStrength());
			    			reading.putInt("gsm_ber", signalStrength.getGsmBitErrorRate());
			    		} catch (NullPointerException e) {
			    			e.printStackTrace();
			    		}
			    		break;
		    	}
		    	//Context ctx = Application.getContext();
		    	Intent postIntent = new Intent(getApplicationContext(), Post.class);
		    	postIntent.putExtra("reading", reading);
		    	postIntent.putExtra("phone", phone);
		    	startService(postIntent);
		    	/*
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
}
