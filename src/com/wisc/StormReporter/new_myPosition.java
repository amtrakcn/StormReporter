package com.wisc.StormReporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;



public class new_myPosition{

	private double latitude;
	private double longitude;
	private String likelyCounty;
	private String closestAddress;
	List<Address> addresses;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
	//protected LocationManager locationManager;
	Location location;
	Geocoder geocoder;// = new Geocoder(this, Locale.getDefault());

	public new_myPosition(LocationManager locationManager, Geocoder geocoder) {
		//this.locationManager = locationManager;
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
				new MyLocationListener());
		this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		this.geocoder = geocoder;
		 //(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		/*
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
				new MyLocationListener()
		);
		 */

		try {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			addresses = geocoder.getFromLocation(latitude, longitude, 20);
		} catch (Exception e) {
			//Toast.makeText(myPosition.this, "Your Location could not be found",
				//	Toast.LENGTH_LONG).show();
			Log.e("myPosition.class","Your Location could not be found");
		}
	}


	// ******** Getters *********

	/**
	 * Parses through the addresses to find what the most likely county the user
	 * is in based on a majority
	 * @param addresses
	 * @return string representing most likely county
	 */
	public String getCounty() {
		String majCounty = "";
		boolean newCounty = true;
		ArrayList<County> counties = new ArrayList<County>();
		if (this.addresses != null) {
			for (int i = 0; i < this.addresses.size(); i++){
				String temp = this.addresses.get(i).getSubAdminArea();
				newCounty = true;
				if ((temp != null)){
					for (int j = 0; j < counties.size(); j++) {
						County current = counties.get(j);
						String currentName = current.getName();
						if ((currentName.equals(temp))) {
							newCounty = false;
							current.addCount();
						}
					}
					if (newCounty == true) {
						counties.add(new County(temp));
					}
				}
			}
			County[] countyList = new County[counties.size()];
			for (int i = 0; i < counties.size();i++) {
				countyList[i] = counties.get(i);
			}
			Arrays.sort(countyList);
			majCounty = countyList[0].getName();
		}

		return majCounty;
	}
	/**
	 * Retrieve user's current latitude
	 * @return double longitude
	 */
	public double getLatitude() {
		return location.getLatitude();
	}
	/**
	 * Retrieve user's current longitude
	 * @return double longitude
	 */
	public double getLongitude() {
		return location.getLongitude();
	}
	/**
	 * Retrieve the user's most likely street address
	 * @return
	 */
	public String getAddress() {
		String address = "No address found";
		String temp = this.addresses.get(0).getAddressLine(0);
		if (!(temp.equals(null))) {
			address = temp;
		}
		
		return address;
	}

	// ***** Possibly not needed--TEST*****
	//TODO: TEST IF NEEDED
	/**
	 * Method to check for change in user's position. Taken from the example I 
	 * found so I'm not sure if it's entirely needed.  With some investigation we 
	 * could trim it out, possibly.
	 * @author Tyrell
	 *
	 */
	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			String message = String.format(
					"New Location \n Longitude: %1$s \n Latitude: %2$s",
					location.getLongitude(), location.getLatitude()
			);
			//Toast.makeText(myPosition.this, message, Toast.LENGTH_LONG).show();
			Log.e("myPosition.class","Your Location could not be found");
		}

		public void onStatusChanged(String s, int i, Bundle b) {
			//Toast.makeText(myPosition.this, "Provider status changed",
					//Toast.LENGTH_LONG).show();
		}

		public void onProviderDisabled(String s) {
			//Toast.makeText(myPosition.this,
					//"Provider disabled by the user. GPS turned off",
					//Toast.LENGTH_LONG).show();
		}
		public void onProviderEnabled(String s) {
			//Toast.makeText(myPosition.this,
					//"Provider enabled by the user. GPS turned on",
					//Toast.LENGTH_LONG).show();
		}

	}

}
