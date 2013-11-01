package com.wisc.StormReporter;

import static android.provider.BaseColumns._ID;
import static com.wisc.StormReporter.Constants.TABLE_NAME;
import static com.wisc.StormReporter.Constants.NAME;
import static com.wisc.StormReporter.Constants.STATE;
import static com.wisc.StormReporter.Constants.EMAIL;
import static com.wisc.StormReporter.Constants.PHONE;
import static com.wisc.StormReporter.Constants.LAT;
import static com.wisc.StormReporter.Constants.LON;
import static com.wisc.StormReporter.Constants.PW;
import static com.wisc.StormReporter.Constants.STATION_ID;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StationData extends SQLiteOpenHelper{
	private static final String DATABASE_NAME="stations.db";
	private static final int DATABASE_VERSION = 1;
	
	public StationData(Context ctx){
		
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
					+ STATION_ID + " TEXT NOT NULL, " + NAME + " TEXT NOT NULL, " + STATE + " TEXT NOT NULL, " 
				+ EMAIL + " TEXT NOT NULL, " + PHONE + " TEXT NOT NULL, " 
				+  PW + " PW);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public void addStation(String stationID, String name, String state, String email, String phone, String pw){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(STATION_ID, stationID);
		values.put(NAME, name);
		values.put(STATE, state);
		values.put(EMAIL, email);
		values.put(PHONE, phone);
		values.put(PW, pw);
		try{
			db.insertOrThrow(TABLE_NAME, null, values);
		}catch(Exception e){
			Log.e("Add station in main activity","error adding new station");
		}
	}
	
	public Cursor getStation(String[] FROM, String where, String[] findParams, String groupBy, String ORDER_BY){
		SQLiteDatabase db =  this.getReadableDatabase();
		if (where != null){
			where = where+ "=?";
		}
		try{
		Cursor cursor = db.query(TABLE_NAME, FROM, where, findParams, groupBy, null, ORDER_BY);
		return cursor;
		
		}catch(Exception e){
			Log.e("Error performing query in stations database", "in StationData");
		}
		return null;
	}
	
	

}
