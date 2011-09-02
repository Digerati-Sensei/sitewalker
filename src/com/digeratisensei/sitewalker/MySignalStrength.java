package com.digeratisensei.sitewalker;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TextView;

public class MySignalStrength extends Fragment {
	  TextView textOut;
	  TextView networkOut;
	  TelephonyManager telephonyManager;
	  
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
	  public TextView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
        
	    // Get the UI
	    textOut = new TextView(getActivity());
	    //networkOut = (TextView) findViewById(R.id.networkOut);

	    // Get the telephony manager
	    telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
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
			    	text = text+"Base Station = " + base_station + "\n" + 
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
		    		text = text+"CID = " + gsm_Cid + "\n" +
		    			"LAC = " + gsm_Lac + "\n" +
		    			"PSC = " + gsm_Psc + "\n" + 
		    			"GSM Cell Location = " + gsm_cell_location;
		    		break;
		    	default:
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
			    	text = text+"Base Station = " + base_station + "\n" + 
			    	   "Latitude = " + base_station_lat + "\n" +
			    	   "Longitude = " + base_station_long + "\n" +
			    	   "System ID = " + system_id + "\n" +
			    	   "Network ID = " + network_id + "\n" +
			    	   "Cell Location = " + cdma_cell_location + "\n" +
			    	   "Miscellaneous Info:\n" +
			    	   "Current Network Provider = " + network_operator_name + "\n" +
			    	   "Current Network Provider ID = " + network_operator + "\n";
		    	}
		    	textOut.setText(text);
		    	Log.d(TAG, ""+device_id+","+phone_type+","+network_type+","+software_ver+","+cellText);
		    	//if(networkOut != null) {
		    	//	networkOut.setText(cellText);
		    	//}
		    	/*cellOut.setText("Device ID = " + device_id + "\n" +
		    		"Device Type = " + phone_type + "\n" +
		    		"Network Type = " + network_type + "\n" +
		    		"Software Version = " + software_ver + "\n");
		    	*/
		    	
		    	Log.v(TAG, "checking neighbor cell");
		    	List<NeighboringCellInfo> neigh_cells = telephonyManager.getNeighboringCellInfo();
		    	for(NeighboringCellInfo info:neigh_cells) {
		    		int cellid = info.getCid();
		    		int rssi = info.getRssi();
		    		int network_type_this = info.getNetworkType();
		    		networkOut.append("Neighboring Cell: " + cellid + "\n" +
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
	    
	    return textOut;  
        //registerForContextMenu(btn);
	}
	  
	  @Override  
	    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
	    super.onCreateContextMenu(menu, v, menuInfo);  
	        menu.setHeaderTitle("Project List");  
	        menu.add(0, v.getId(), 0, "CDC Atlanta");  
	        menu.add(0, v.getId(), 0, "Hyundai");  
	    }
		
		@Override  
	    public boolean onContextItemSelected(MenuItem item) {  
			Intent intent = new Intent(getActivity().getApplicationContext(), BuildingChooser.class);
			intent.putExtra("project", item.getTitle());
			startActivity(intent);
	    return true;  
	    }
}