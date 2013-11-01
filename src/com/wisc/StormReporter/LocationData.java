package com.wisc.StormReporter;

import static android.provider.BaseColumns._ID;
import static com.wisc.StormReporter.Constants.LOCATIONS_TABLE_NAME;
import static com.wisc.StormReporter.Constants.NAME;
import static com.wisc.StormReporter.Constants.STATE;
import static com.wisc.StormReporter.Constants.COUNTY;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationData extends SQLiteOpenHelper{
	private static final String DATABASE_NAME="locations.db";
	private static final int DATABASE_VERSION = 1;

	public LocationData(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		//Log.i("in locationdata", "called");
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + LOCATIONS_TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ STATE + " TEXT NOT NULL, " + COUNTY + " TEXT NOT NULL, " + NAME + " TEXT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE_NAME);
		onCreate(db);
	}

	public void addLocation(String name, String state, String county){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NAME, name);
		values.put(STATE, state);
		values.put(COUNTY, county);
		boolean safeToInsert = false;
		//Log.e("In location data", "adding location to database");
		//Need to check whether or not the county has a single qutation in it, because sqlite doesn't like that
		//if there is one, need to double it for sql syntax
		if(county.indexOf('\'') != -1 ){
			String d = "[']" ;
			String[] oldCounty = county.split(d);
		
			county = oldCounty[0] + "''" + oldCounty[1];
			//Log.e("split test",county);
		}
		
		//First evaluate whether or not there is already an equivalent entry in the database,
		//if not, add the entry to the database
		try{

			String[] choose = { STATE, COUNTY };
			String ORDER_BY = COUNTY + " ASC";
			String where = STATE + "='" +state +"' AND "+COUNTY +"='" +county+"'";
			Cursor cur = getStationFromLocation(choose, where, null, null, ORDER_BY);
			//if cursor has a value, the movetoNext function will return true
			//in which case there is a duplicate entry in the database and it is not
			//safe to insert the desire entry
			if (cur.equals(null) || cur.moveToNext()==false){
				safeToInsert=true;
			}
			cur.close();
		} catch (Exception e) {
			Log.i("Locations database output test when testing for duplicate in LocationData", "Error");
		}
		
		//If no duplicates found currently, add it to the database
		if(safeToInsert){
			try{
				db.insertOrThrow(LOCATIONS_TABLE_NAME, null, values);
			}catch(Exception e){
				Log.e("Add location in main activity","error adding new location");
			}
		}

	}

	public Cursor getStationFromLocation(String[] FROM, String where, String[] findParams, String groupBy, String ORDER_BY){
		SQLiteDatabase db =  this.getReadableDatabase();
		if (where != null && !where.contains("=")){
			where = where+ "=?";
		}
		try{
			Cursor cursor = db.query(LOCATIONS_TABLE_NAME, FROM, where, findParams, groupBy, null, ORDER_BY);
			return cursor;
		}catch(Exception e){
			Log.e("Error performing query in locations database", "error");
		}
		return null;
	}

}
