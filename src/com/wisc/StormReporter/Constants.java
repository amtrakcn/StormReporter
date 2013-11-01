package com.wisc.StormReporter;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns{
	public static final String TABLE_NAME = "stations";
	public static final String LOCATIONS_TABLE_NAME="locations";
	
	//Columns in databases
	public static final String NAME = "name";
	public static final String STATE = "state";
	public static final String EMAIL = "email";
	public static final String PHONE = "phone";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String PW = "pw";
	public static final String COUNTY = "county";
	public static final String STATION_ID= "stationID";
	
}
