package com.digeratisensei.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class Database {
	private SQLiteDatabase db;
	private final Context context;
	private final DBHelper dbhelper;
	public Database(Context c){
		context = c;
		dbhelper = new DBHelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
	}
	public void close() {
		db.close();
	}
	public void open() throws SQLiteException {
		try {
			db = dbhelper.getWritableDatabase();
		} catch(SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = dbhelper.getReadableDatabase();
		}
	}
	public long insertDevice(String device_id, String software_ver, int phone_type) {
		try {
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(Constants.DEVICE_ID, device_id);
			newTaskValue.put(Constants.SOFTWARE_VER, software_ver);
			newTaskValue.put(Constants.PHONE_TYPE, phone_type);
			newTaskValue.put(Constants.TIME_STAMP, java.lang.System.currentTimeMillis());
			return db.insert(Constants.DEVICE_TABLE_NAME, null, newTaskValue);
		} catch(SQLiteException ex) {
			Log.v("Insert into database exception caught", ex.getMessage());
			return -1;
		}
	}
	public long insertReading(int cdma_ecio, int cdma_dbm, int evdo_dbm, int evdo_ecio, int evdo_snr, int base_station, int base_station_lat, int base_station_long, int system_id, int network_id, String cdma_cell_location, String network_operator_name, String network_operator, int network_type, String device_id) {
		try {
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(Constants.CDMA_DBM, cdma_dbm);
			newTaskValue.put(Constants.CDMA_ECIO, cdma_ecio);
			newTaskValue.put(Constants.EVDO_DBM, evdo_dbm);
			newTaskValue.put(Constants.EVDO_ECIO, evdo_ecio);
			newTaskValue.put(Constants.EVDO_SNR, evdo_snr);
			newTaskValue.put(Constants.BASE_STATION, base_station);
			newTaskValue.put(Constants.BASE_STATION_LAT, base_station_lat);
			newTaskValue.put(Constants.BASE_STATION_LONG, base_station_long);
			newTaskValue.put(Constants.SYSTEM_ID, system_id);
			newTaskValue.put(Constants.NETWORK_ID, network_id);
			newTaskValue.put(Constants.CDMA_CELL_LOCATION, cdma_cell_location);
			newTaskValue.put(Constants.NETWORK_OPERATOR_NAME, network_operator_name);
			newTaskValue.put(Constants.NETWORK_OPERATOR, network_operator);
			newTaskValue.put(Constants.NETWORK_TYPE, network_type);
			newTaskValue.put(Constants.TIME_STAMP, java.lang.System.currentTimeMillis());
			return db.insert(Constants.READING_TABLE_NAME, null, newTaskValue);
		} catch(SQLiteException ex) {
			Log.v("Insert into database exception caught", ex.getMessage());
			return -1;
		}
	}
	public Cursor getReadings() {
		Cursor c = db.query(Constants.READING_TABLE_NAME, null, null, null, null, null, null);
		return c;
	}
}