package com.digeratisensei.sitewalker;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.TextView;

import com.digeratisensei.data.Database;

public class MySignalStrength extends Activity {
	  TextView textOut;
	  TextView cellOut;
	  TelephonyManager telephonyManager;
	  Database dbase;
	  
	  private static final String TAG = "MySignalStrength";
	  private static final int PHONE_TYPE_CDMA = 2;
	  private static final int PHONE_TYPE_GSM = 1;
	  
	  int cdma_ecio;
	  	int cdma_dbm;
	  	int evdo_dbm;
	  	int evdo_ecio;
	  	int evdo_snr;
	  	int gsm_ss;
	  	int gsm_ber;
	  	int base_station;
    	int base_station_lat;
    	int base_station_long;
    	int system_id;
    	int network_id;
    	int gsm_Cid;
    	int gsm_Lac;
    	int gsm_Psc;
    	CdmaCellLocation cellLocation;
    	String cdma_cell_location;
    	GsmCellLocation cellGsmLocation;
    	
	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.signalstrength);
	    dbase = new Database(this);
	    dbase.open();
	    
	    // Get the UI
	    textOut = (TextView) findViewById(R.id.textOut);
	    cellOut = (TextView) findViewById(R.id.cellOut);

	    // Get the telephony manager
	    telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	    final int phone_type = telephonyManager.getPhoneType();
		final String network_operator_name = telephonyManager.getNetworkOperatorName();
    	final String network_operator = telephonyManager.getNetworkOperator();
    	final int network_type = telephonyManager.getNetworkType();
    	final String device_id = telephonyManager.getDeviceId();
    	final String software_ver = telephonyManager.getDeviceSoftwareVersion();
    	
	    PhoneStateListener listener = new PhoneStateListener() {
	    	@Override
	    	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
	    		
	    		
		    	
		    	String text = "";
		    	String cellText = "";
		    	switch(phone_type){
		    	case PHONE_TYPE_CDMA:
		    		cellLocation =(CdmaCellLocation) telephonyManager.getCellLocation();
		    		base_station = cellLocation.getBaseStationId();
			    	base_station_lat = cellLocation.getBaseStationLatitude();
			    	base_station_long = cellLocation.getBaseStationLongitude();
			    	system_id = cellLocation.getSystemId();
			    	network_id = cellLocation.getNetworkId();
			    	cdma_cell_location = cellLocation.toString();
			    	
		    		cdma_ecio = signalStrength.getCdmaEcio();
			    	cdma_dbm = signalStrength.getCdmaDbm();
			    	evdo_dbm = signalStrength.getEvdoDbm();
			    	evdo_ecio = signalStrength.getEvdoEcio();
			    	evdo_snr = signalStrength.getEvdoSnr();
			    	text = "CDMA Strength = " + cdma_dbm + "\n" +
			    	   "CDMA EC\\IO = " + cdma_ecio + "\n" +
			    	   "EVDO Strength = " + evdo_dbm + "\n" +
			    	   "EVDO EC\\IO = " + evdo_ecio + "\n" +
			    	   "EVDO SNR = " + evdo_snr + "\n";
			    	cellText = "Base Station = " + base_station + "\n" + 
			    	   "Latitude = " + base_station_lat + "\n" +
			    	   "Longitude = " + base_station_long + "\n" +
			    	   "System ID = " + system_id + "\n" +
			    	   "Network ID = " + network_id + "\n" +
			    	   "Cell Location = " + cdma_cell_location + "\n" +
			    	   "Miscellaneous Info:\n" +
			    	   "Current Network Provider = " + network_operator_name + "\n" +
			    	   "Current Network Provider ID = " + network_operator + "\n";
			    	break;
			    	
		    	case PHONE_TYPE_GSM:
		    		cellGsmLocation = (GsmCellLocation) telephonyManager.getCellLocation();
		    		gsm_Cid = cellGsmLocation.getCid();
		    		gsm_Lac = cellGsmLocation.getLac();
		    		//gsm_Psc = ((NeighboringCellInfo) cellGsmLocation).getPsc();
		    		String gsm_cell_location = cellGsmLocation.toString();
		    		
		    		gsm_ss = signalStrength.getGsmSignalStrength();
		    		gsm_ber = signalStrength.getGsmBitErrorRate();
		    		text = "GSM Strength = " + gsm_ss + "\n" +
		    			"GSM Bit Error Rate = " + gsm_ber + "\n";
		    		cellText = "CID = " + gsm_Cid + "\n" +
		    			"LAC = " + gsm_Lac + "\n" +
		    			"PSC = " + gsm_Psc + "\n" + 
		    			"GSM Cell Location = " + gsm_cell_location;
		    		break;
		    	}
		    	textOut.setText(text);
		    	Log.d(TAG, ""+device_id+","+phone_type+","+network_type+","+software_ver+","+cellText);
		    	cellOut.setText(cellText);
		    	/*cellOut.setText("Device ID = " + device_id + "\n" +
		    		"Device Type = " + phone_type + "\n" +
		    		"Network Type = " + network_type + "\n" +
		    		"Software Version = " + software_ver + "\n");
		    	*/
		    	dbase.insertReading(cdma_ecio, cdma_dbm, evdo_dbm, evdo_ecio, evdo_snr, base_station, base_station_lat, base_station_long, system_id, network_id, cdma_cell_location, network_operator_name, network_operator, network_type, device_id);
		    	
		    	Log.v(TAG, "checking neighbor cell");
		    	List<NeighboringCellInfo> neigh_cells = telephonyManager.getNeighboringCellInfo();
		    	for(NeighboringCellInfo info:neigh_cells) {
		    		int cellid = info.getCid();
		    		int rssi = info.getRssi();
		    		int network_type_this = info.getNetworkType();
		    		cellOut.append("Neighboring Cell: " + cellid + "\n" +
		    				"RSSI = " + rssi + "\n" +
		    				"Network Type = " + network_type_this
		    				);
		    		Log.v(TAG, "appending neighbor cell");
		    	}
	    	}
	    };
    	
	    //Register the listener wit the telephony manager
	    Log.v(TAG, "starting telephonyManager");
	    telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	    Log.d(TAG, "" + telephonyManager);
	}
}