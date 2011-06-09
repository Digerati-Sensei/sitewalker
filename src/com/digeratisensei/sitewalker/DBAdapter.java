package com.digeratisensei.sitewalker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
    public static final String KEY_XCOORD = "xcoord";
    public static final String KEY_YCOORD = "ycoord";
    //public static final String KEY_PUBLISHER = "publisher";    
    private static final String TAG = "DBAdapter";
    
    private static final String DATABASE_NAME = "projects";
    private static final String DATABASE_TABLE = "readings";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
        "create table readings (_id integer primary key autoincrement, "
        + "xcoord long not null, title text not null, " 
        + "ycoord long not null);";
        
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    } 
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    //---insert a title into the database---
    public long insertReading(Long xcoord, Long ycoord) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_XCOORD, xcoord);
        initialValues.put(KEY_YCOORD, ycoord);
        //initialValues.put(KEY_PUBLISHER, publisher);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteReading(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllReadings() 
    {
        return db.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID, 
        		KEY_XCOORD,
        		KEY_YCOORD
        		},
        		null, 
                null, 
                null, 
                null, 
                null);
    }

    //---retrieves a particular title---
    public Cursor getReading(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_XCOORD, 
                		KEY_YCOORD
                		//KEY_PUBLISHER
                		}, 
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a title---
    public boolean updateReading(long rowId, String xcoord, 
    String ycoord, String publisher) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_XCOORD, xcoord);
        args.put(KEY_YCOORD, ycoord);
        //args.put(KEY_PUBLISHER, publisher);
        return db.update(DATABASE_TABLE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
    }
}
