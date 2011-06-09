package com.digeratisensei.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String CREATE_READING_TABLE="create table "+
		Constants.READING_TABLE_NAME+" ("+
		Constants.READING_KEY_ID+" integer primary key autoincrement, "+
		Constants.CDMA_DBM+" integer, "+
		Constants.CDMA_ECIO+" integer, "+
		Constants.EVDO_DBM+" integer, "+
		Constants.EVDO_ECIO+" integer, "+
		Constants.EVDO_SNR+" integer, "+
		Constants.BASE_STATION+" integer, "+
		Constants.BASE_STATION_LAT+" integer, "+
		Constants.BASE_STATION_LONG+" integer, "+
		Constants.SYSTEM_ID+" integer, "+
		Constants.NETWORK_ID+" integer, "+
		Constants.CDMA_CELL_LOCATION+" text, "+
		Constants.NETWORK_OPERATOR_NAME+" text, "+
		Constants.NETWORK_OPERATOR+" text, "+
		Constants.NETWORK_TYPE+" integer, "+
		Constants.TIME_STAMP+" integer);";
	
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v("DBHelper onCreate", "Creating all the tables");
		try {
			db.execSQL(CREATE_READING_TABLE);
		} catch(SQLiteException ex) {
			Log.v("Create table exception", ex.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w("TaskDBAdapter", "Upgrading from version "+oldVersion+" to "+newVersion+", which will destroy all old data");
		db.execSQL("drop table if exists "+Constants.READING_TABLE_NAME);
		onCreate(db);
	}

}
