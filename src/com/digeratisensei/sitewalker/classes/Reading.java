package com.digeratisensei.sitewalker.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.digeratisensei.sitewalker.Post;

public class Reading extends Activity implements Runnable {
	private static final String TAG = "MySignalStrength";
	private static final int PHONE_TYPE_CDMA = 2;
	private static final int PHONE_TYPE_GSM = 1;
	
	private TelephonyManager telephonyManager;
	/*
	private int cdma_ecio;
	private int cdma_dbm;
	private int evdo_dbm;
	private int evdo_ecio;
	private int evdo_snr;
	private int gsm_ss;
	private int gsm_ber;
	private int base_station;
	private int base_station_lat;
	private int base_station_long;
	private int system_id;
	private int network_id;
	private int gsm_Cid;
	private int gsm_Lac;
	private int gsm_Psc;
	private String cdma_cell_location;
	*/
	private GsmCellLocation cellGsmLocation;
	private CdmaCellLocation cellLocation;
	public Bundle reading;
	public Bundle phone;
	private int phone_type;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    Thread thread = new Thread(Reading.this);
	    thread.start();
	}
	
	public void run() {
    	startReadingService();
    }
	
	private void startReadingService() {
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phone_type = telephonyManager.getPhoneType();
		phone.putInt("phone_type", phone_type);
	    phone.putString("network_operator_name", telephonyManager.getNetworkOperatorName());
	    phone.putString("network_operator", telephonyManager.getNetworkOperator());
	    phone.putInt("network_type", telephonyManager.getNetworkType());
	    phone.putString("device_id", telephonyManager.getDeviceId());
	    phone.putString("software_ver", telephonyManager.getDeviceSoftwareVersion());
		
		PhoneStateListener listener = new PhoneStateListener() {
		    @Override
		    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		    	switch (phone_type){
				    case PHONE_TYPE_CDMA:
			    		cellLocation =(CdmaCellLocation) telephonyManager.getCellLocation();
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
						break;
				    	
			    	case PHONE_TYPE_GSM:
		    			cellGsmLocation = (GsmCellLocation) telephonyManager.getCellLocation();
		    			reading.putInt("gsm_Cid", cellGsmLocation.getCid());
		    			reading.putInt("gsm_Lac", cellGsmLocation.getLac());
		    			//gsm_Psc = ((NeighboringCellInfo) cellGsmLocation).getPsc();
		    			reading.putString("gsm_cell_location", cellGsmLocation.toString());
		    			reading.putInt("gsm_ss", signalStrength.getGsmSignalStrength());
		    			reading.putInt("gsm_ber", signalStrength.getGsmBitErrorRate());
			    		break;
		    	}
		    	
		    	Intent postIntent = new Intent(null, Post.class);
		    	//String readingString = reading.toString();
		    	//String phoneString = phone.toString();
		    	postIntent.putExtra("reading", reading);
		    	postIntent.putExtra("phone", phone);
		    	startActivity(postIntent);
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
	    telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
}
