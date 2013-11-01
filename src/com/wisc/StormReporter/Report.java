package com.wisc.StormReporter;

import java.util.ArrayList;

import android.net.Uri;

/**
 * Generated report object
 * @author Amtrak
 *
 */
public class Report {
	
	private ArrayList<String> weatherType;
	private ArrayList<Uri> photo;
	private String location;
	
	
	public Report() {
		weatherType = new ArrayList<String>();
		photo = new ArrayList<Uri>();
		location = new String();
	}
	
	public Report(ArrayList<String> inputType, ArrayList<Uri> inputPhoto, String inputLocation) {
		weatherType = inputType;
		photo = inputPhoto;
		location = inputLocation;
	}
	
	public void setWeatherType(ArrayList<String> inputType) {
		weatherType = inputType;
	}
	
	public void setPhoto(ArrayList<Uri> inputPhoto) {
		photo = inputPhoto;
	}
	public void setLocation(String inputLocation) {
		location = inputLocation;
	}
	
	public ArrayList<String> getWeatherType() {
		return weatherType;
	}
	
	public ArrayList<Uri> getPhoto() {
		return photo;
	}
	
	public String getLocation() {
		return location;
	}

}
