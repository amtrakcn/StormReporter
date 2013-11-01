package com.wisc.StormReporter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

import static com.wisc.StormReporter.Constants.COUNTY;
import static com.wisc.StormReporter.Constants.NAME;
import static com.wisc.StormReporter.Constants.STATE;
import static com.wisc.StormReporter.Constants.EMAIL;
import static com.wisc.StormReporter.Constants.LAT;
import static com.wisc.StormReporter.Constants.LON;
import static com.wisc.StormReporter.Constants.PHONE;
import static com.wisc.StormReporter.Constants.STATION_ID;

public class StormReport {

	SharedPreferences prefs;
	LocationData locations;
	String phone;
	String username;
	String homeStation;
	String stationEmail;
	String state;
	String date;
	String station;
	String[] stationData;

	//Data from the report
	ArrayList<String> weatherTypes;
	String tornadoHeading;
	String tornadoPosition;
	boolean tornadoAcrossCountyLine;
	String funnelCloudHeading;
	String funnelCloudPosition;
	boolean funnelCloudAcrossCountyLine;
	String wallCloudHeading;
	String wallCloudPosition;
	boolean wallCloudAcrossCountyLine;
	boolean wallCloudIsRotating;
	String windSpeed;
	boolean windSpeedMeasured;
	ArrayList<String> hailSizes;
	boolean hailSizeMeasured;
	boolean damage;
	boolean injuries;
	boolean fatalities;
	String otherDetails;


	String BODY;
	StationData stations;
	private String location;
	private String dateAndTime;
	boolean alreadyCompiled;

	//Set flags to know which data has been supplied
	boolean tornado;
	boolean funnelCloud;
	boolean wallCloud;
	boolean hail;
	boolean highWind;
	boolean otherDetailsSupplied;
	boolean imageSupplied;
	public StormReport(Context ctx){
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		stations = new StationData(ctx.getApplicationContext());

		/*Calendar cal = Calendar.getInstance();
		date = java.text.DateFormat.getDateTimeInstance().format(cal.getTime());
		TimeZone tz = cal.getTimeZone();
		String tzDisplay = tz.getDisplayName(tz.inDaylightTime(cal.getTime()), TimeZone.SHORT, Locale.getDefault());
		if(!(tzDisplay.contains("GMT"))){
			date = date + " (" + tzDisplay+ ")";
		}else{
			date = date + " (Local Time)";
		}*/
		locations = new LocationData(ctx.getApplicationContext());
		username = prefs.getString("username", "no user found");
		phone = "";
		homeStation ="";
		stationEmail="";
		state="";
		station = "";  
		weatherTypes = new ArrayList<String>();
		otherDetails = "";
		location = "";
		dateAndTime ="";
		alreadyCompiled =false;
		stationData = new String[3];


		tornado = false;
		funnelCloud = false;
		wallCloud = false;
		hail = false;
		highWind= false;
		otherDetailsSupplied =false;
		imageSupplied=false;


	}

	/** 						Addition Methods							**/

	public void setDateAndTime(String dateAndTime){
		this.dateAndTime=dateAndTime;
	}

	public void setLocation(String location){
		this.location = location;
	}
	public boolean addStationRecipient(String state, String county){
		/*this.homeStation = homeStation;
		this.stationEmail = email;
		this.state = state;
		this.lat = lat;
		this.lon = lon;*/

		//First get the nws station associated with the user's location as inputed into
		//this method as the two parameters

		station = findStation(state, county);
		stationData = findStationData(station);

		if(stationData[0].length() != 0 && stationData[1].length() !=0){

				stationEmail = stationData[0];
				//Put the state of this station in the data array so we can use it later on
				stationData[2] = state;
				return true;
		}else{
			return false;
		}


	}

	public void setHomeStation(String homeStation){
		this.homeStation = homeStation;
	}

	public void setWeatherTypes(ArrayList<String> types){
		this.weatherTypes = types;
	}

	public void setTornadoDetails(String position, String heading, boolean acrossCountyLine){
		this.tornadoHeading = heading;
		this.tornadoPosition = position;
		tornadoAcrossCountyLine = acrossCountyLine;
		tornado = true;
	}

	public void setFunnelCloudDetails(String position, String heading, boolean acrossCountyLine){
		this.funnelCloudHeading = heading;
		this.funnelCloudPosition = position;
		funnelCloudAcrossCountyLine = acrossCountyLine;
		funnelCloud = true;
	}

	public void setWallCloudDetails(String position, String heading, boolean acrossCountyLine, boolean isRotating){
		this.wallCloudHeading = heading;
		this.wallCloudPosition = position;
		this.wallCloudAcrossCountyLine = acrossCountyLine;
		this.wallCloudIsRotating = isRotating;
		wallCloud = true;
	}

	public void setHailDetails(ArrayList<String> sizes, boolean sizesWereMeasured){
		this.hailSizes = sizes;
		this.hailSizeMeasured = sizesWereMeasured;
		hail = true;
	}

	public void setWindSpeedDetails(String windSpeed, boolean windWasMeasured){
		this.windSpeed = windSpeed;
		this.windSpeedMeasured = windWasMeasured;
		highWind = true;
	}

	public void setOtherDetails(String otherDetails){
		this.otherDetails = otherDetails;
		this.otherDetailsSupplied = true;
	}

	public void setDamage(boolean damage){
		this.damage = damage;
	}

	public void setInjuries(boolean injuries){
		this.injuries = injuries;
	}

	public void setFatalities(boolean fatalities){
		this.fatalities = fatalities;
	}


	public String compile(){
		//First, evaluate whether or not compile has been called already, because if so we don't want to 
		//build an entirely new string, just return the already compiled string
		if(!alreadyCompiled){
			//Build the email body based off of the currently set StormReport variables, 
			//if there are nor report types set, don't compile
			if (weatherTypes.size() != 0){
				//For the report body Start a new html table with one row that starts out with bold text
				BODY = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><body>";

				BODY= BODY + "<p><h1>Storm Report</h1><h3>" +location+"</h3><h4>"+ dateAndTime + "</h4><font size=\"3\">Sent by:&nbsp;&nbsp;&nbsp;&nbsp;"
						+ username;
				//Evaluate whether or not the current user is trained by looking at the preferences
				if(prefs.getBoolean("isTrainedSpotter", false)){
					BODY = BODY + "&nbsp;&nbsp;&nbsp;(NWS Trained Spotter)";
				}
				else{
					BODY = BODY + "&nbsp;&nbsp;&nbsp;(NOT a Trained Spotter)";
				}
				//End the header for the body and put some breaks in the html to separate the header from the report
				BODY = BODY + "</font></p><p>&mdash;&mdash;&mdash;&mdash;&mdash;&mdash;&mdash;&mdash;&mdash;&mdash;&mdash;&mdash;" +
						"&mdash;&mdash;&mdash;&mdash;&mdash;</p><p>&nbsp;</p>";


				//Start the table
				//BODY = BODY + "<table cellpadding=\"7\">";
				//Set the table row header for the report:
				//BODY = BODY + "<tr><td><b>Report: </b></td>";
				BODY=BODY+"<p><b><u>Report:</u></b>";

				//Add this report type to the html body
				//First evaluate the report parameter with the biggest array list
				//so we know how many columns this row needs to span
				//int big = findBiggestArrayList();

				//BODY = BODY + "<td colspan =\"" + String.valueOf(big) + "\" align = \"center\"><table border=\"1\"";
				//Evaluate what type of report this is to highlight the report a certain color	
				for(int i=0; i < weatherTypes.size(); i++){
					String reportType = weatherTypes.get(i);
					if(reportType.equalsIgnoreCase("tornado")){
						//BODY = BODY + "bordercolor=\"red\"><tr><td>&nbsp;&nbsp;TORNADO&nbsp;&nbsp;</td></tr></table></td>";
						BODY = BODY + "<font color=\"red\">&nbsp;&nbsp;" + reportType + "</font>";
					}else if (reportType.equalsIgnoreCase("hail")){
						//BODY = BODY + "bordercolor=\"blue\"><tr><td>&nbsp;&nbsp;Hail&nbsp;&nbsp;</td></tr></table></td>";
						BODY = BODY + "<font color=\"blue\">&nbsp;&nbsp;" + reportType + "</font>";
					}else if (reportType.equalsIgnoreCase("wind")){
						//BODY = BODY + "bordercolor=\"green\"><tr><td>&nbsp;&nbsp;High Wind&nbsp;&nbsp;</td></tr></table></td>";
						BODY = BODY + "<font color=\"green\">&nbsp;&nbsp;" + reportType + "</font>";
					}else{
						//BODY = BODY +"><tr><td>&nbsp;&nbsp;" + reportType + "&nbsp;&nbsp;</td></tr></table></td>";
						BODY = BODY + "&nbsp;&nbsp;" + reportType;
					}
					if(i != weatherTypes.size()-1){
						BODY=BODY + ", ";
					}
				}
				//End the reportType table row tag.
				BODY = BODY + "</p><p>&nbsp;</p>";

				BODY = BODY + "<p><b><u>Details:</u></b></p>";

				//Start with tornado details
				if(tornado){
					if(this.tornadoPosition.length() != 0){
						BODY = BODY + "<p>&sdot; Tornado is oriented " + tornadoPosition.toLowerCase() +"</p><p></p>";
					}
					if(this.tornadoHeading.length() != 0){
						BODY = BODY + "<p>&sdot; Tornado is " + tornadoHeading.toLowerCase() +"</p><p></p>";
					}
					try{
						if(tornadoAcrossCountyLine == true){
							BODY = BODY + "<p>&sdot; Tornado seems to be located across the county line</p><p></p>";
						}else{
							BODY = BODY + "<p>&sdot; Tornado does not seem to be located across the county line</p><p></p>";
						}
					}catch(Exception e){
						//If get here tornadoAcrossCountyLine was null and don't weant to say anything about it
					}
				}
				if(funnelCloud){
					if(this.funnelCloudPosition.length() != 0){
						BODY = BODY + "<p>&sdot; Funnel cloud is oriented " + funnelCloudPosition.toLowerCase() +"</p><p></p>";
					}
					if(this.funnelCloudHeading.length() != 0){
						BODY = BODY + "<p>&sdot; Funnel cloud is " + funnelCloudHeading.toLowerCase() +"</p><p></p>";
					}
					try{
						if(funnelCloudAcrossCountyLine == true){
							BODY = BODY + "<p>&sdot; Funnel cloud seems to be located across the county line</p><p></p>";
						}else{
							BODY = BODY + "<p>&sdot; Funnel cloud does not seem to be located across the county line</p><p></p>";
						}
					}catch(Exception e){
						//If get here funnelCloudAcrossCountyLine was null and don't want to say anything about it
					}
				}
				if(wallCloud){
					try{
						if(wallCloudIsRotating == true){
							BODY = BODY + "<p>&sdot; Wall cloud is rotating</p><p></p>";
						}else{
							BODY = BODY + "<p>&sdot; Wall cloud does not seem to be rotating</p><p></p>";
						}
					}catch(Exception e){
						//If get here wallCloudIsRotaing was null and don't want to say anything about it
					}
					if(this.wallCloudPosition.length() != 0){
						BODY = BODY + "<p>&sdot; Wall cloud is oriented " + wallCloudPosition.toLowerCase() +"</p><p></p>";
					}
					if(this.wallCloudHeading.length() != 0){
						BODY = BODY + "<p>&sdot; Wall cloud is " + wallCloudHeading.toLowerCase() +"</p><p></p>";
					}
					try{
						if(wallCloudAcrossCountyLine == true){
							BODY = BODY + "<p>&sdot; Wall cloud seems to be located across the county line</p><p></p>";
						}else{
							BODY = BODY + "<p>&sdot; Wall cloud does not seem to be located across the county line</p><p></p>";
						}
					}catch(Exception e){
						//If get here wallCloudAcrossCountyLine was null and don't want to say anything about it
					}
				}
				if(hail){

					if (hailSizes.size() != 0){
						//First see if the sizes were measured or estimated
						if(this.hailSizeMeasured){
							BODY = BODY + "<p>&sdot; Measured hail sizes of ";
						}else{
							BODY = BODY + "<p>&sdot; Estimated hail sizes of ";
						}
						for (int i = 0; i < hailSizes.size(); i++){
							BODY = BODY + hailSizes.get(i);
							if(i != hailSizes.size()-1){
								BODY = BODY + ", ";
							}
						}
					}
					//End the hail details block
					BODY = BODY + "</p><p></p>";
				}
				if(highWind){
					try{
						if(windSpeed.length() != 0){
							if(this.windSpeedMeasured == true){
								BODY = BODY + "<p>&sdot; Measured wind speeds of " + this.windSpeed+ "</p><p></p>";
							}else{
								BODY = BODY + "<p>&sdot; Estimated wind speeds of " + this.windSpeed+ "</p><p></p>";
							}
						}
					}catch(Exception e){
						//If get here windSpeedMeasured was null and don't want to say anything about it
					}
				}


				//End the details paragraph
				//BODY = BODY + "</p>";
				BODY = BODY + "<p>&nbsp;</p>";


				if(this.otherDetailsSupplied){
					BODY = BODY + "<p><b><u>Other Details/Comments:</u></b></p>";
					BODY = BODY + "<p>&sdot;" + this.otherDetails + "</p><p></p>";
				}

				//End the other details paragraph
				//BODY = BODY + "</p>";
				BODY = BODY + "<p>&nbsp;</p>";

				//Evaluate if any damages/injuries/fatalities have been reported
				//Damages
				try{
					if(this.damage){
						BODY = BODY + "<p><b><u>Damage:</u></b>&nbsp;&nbsp;&nbsp;YES</p>";
					}
					else{
						BODY = BODY + "<p><b><u>Damage:</u></b>&nbsp;&nbsp;&nbsp;NONE REPORTED</p>";
					}
				}catch(Exception e){
					BODY = BODY + "<p><b><u>Damage:</u></b>&nbsp;&nbsp;&nbsp;NONE REPORTED</p>";
				}

				//Injuries
				try{
					if(this.injuries){
						BODY = BODY + "<p><b><u>Injuries:</u></b>&nbsp;&nbsp;&nbsp;YES</p>";
					}
					else{
						BODY = BODY + "<p><b><u>Injuries:</u></b>&nbsp;&nbsp;&nbsp;NONE REPORTED</p>";
					}
				}catch(Exception e){
					BODY = BODY + "<p><b><u>Injuries:</u></b>&nbsp;&nbsp;&nbsp;NONE REPORTED</p>";
				}

				//Fatalities
				try{
					if(this.fatalities){
						BODY = BODY + "<p><b><u>Fatalities:</u></b>&nbsp;&nbsp;&nbsp;YES</p>";
					}
					else{
						BODY = BODY + "<p><b><u>Fatalities:</u></b>&nbsp;&nbsp;&nbsp;NONE REPORTED</p>";
					}
				}catch(Exception e){
					BODY = BODY + "<p><b><u>Fatalities:</u></b>&nbsp;&nbsp;&nbsp;NONE REPORTED</p>";
				}

				//End the damage/injury/fatality paragraph
				BODY = BODY + "<p>&nbsp;</p>";

				/**TODO COMPILE IMAGES ARRAYLIST
				 * 
				 */
				/*
				BODY = BODY + "<p><b><u>Images:</u></b></p>";
				for(int i=0; i < images.size(); i++){
					BODY = BODY + "<p>" + images.get(i) +"<p></p>";

				}
				//End the damage paragraph
				//BODY = BODY + "</p>";
				BODY = BODY + "<p>&nbsp;</p>";
				 */


				//Add the Storm Report tag at the end
				BODY = BODY + "<br/><br/><br/><font size=\"2\">Sent via Storm Reporter&copy;<p><&nbsp;</p></body></html>";
				alreadyCompiled = true;
				return BODY;
			}
			else{
				return "";
			}
		}else{
			return BODY;
		}

	}

	public Intent send(){
		if (stationEmail.length() != 0){
			String tempEmail = "testnwsstation@yahoo.com";
			compile();
			String subject = "Test Storm Report";
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/html");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
					new String[]{tempEmail});
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
					subject);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
					Html.fromHtml(BODY));


			return emailIntent;
		}else{
			Log.e("StormReport send error","Station data not found or not asked to be found before asking to send report");
			return null;
		}
	}

	public boolean store(boolean locally){
		return false;
	}

	/*private int findBiggestArrayList(){
		int d = damage.size();
		int de = details.size();

		if(d > de){
			return d;
		}
		return de;

	}*/

	public boolean alreadyCompiled(){
		return alreadyCompiled;
	}

	public String findStation(String state, String county){
		String tempStation = "";
		try{
			//Log.e("IN TESTING LOCATIONS","SHOULD BE WORKING");
			String[] choose = { STATE, COUNTY, NAME};
			String ORDER_BY = STATE + " ASC, " + COUNTY + " ASC, " + NAME + " ASC";
			//String where = STATE + "=" + state + " AND " + COUNTY + "=" + county;
			String where = STATE + "='" + state +"' AND " + COUNTY +"='"+county+"'";
			Cursor cur = locations.getStationFromLocation(choose, where, null, null, ORDER_BY);
			if (cur.getCount() == 1){
				cur.moveToNext();
				tempStation = cur.getString(2);
				cur.close();
				locations.close();
				//Log.e("TEST GETTING STATION FROM LOCATION", "STATION: " + tempStation);
				return tempStation;
			}
			cur.close();
			locations.close();
			return tempStation;
		} catch (Exception e) {
			Log.e("Locations database output test in StormReport activity", "Error");
			locations.close();
			return tempStation;
		}
	}
	/**
	 * 
	 * @param stationId
	 * @return string array with first index equal to the station's email address and second index equal to the station's phone number
	 * or empty strings in each index if station data could not be found
	 */
	public String[] findStationData(String stationId){
		Log.e("test initial station id in findSTationData in StormReport", stationId);
		String stationEmail = "";
		String phone = "";
		String[] data = new String[3];
		try{
			String[] FROM = { STATION_ID, EMAIL, PHONE};
			String where = STATION_ID;
			String[] findParam = { stationId };
			Cursor cur = stations.getStation(FROM, where, findParam, null, null);
			if (cur.getCount() == 1){
				cur.moveToNext();
				stationEmail = cur.getString(1);
				phone = cur.getString(2);
				cur.close();
				stations.close();
			}else{
				cur.close();
				stations.close();
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.e("Error loading station data in StormReport class", "error");
		}
		data[0] = stationEmail;
		data[1] = phone;
		//Log.e("test initial station data in getSTationData in StormReport", stationEmail+" "+phone);
		return data;
	}
	
	public String[] getStationData(){
		if (station.length() != 0 && stationData[0].length()!=0 && stationData[2].length() != 0){
			String data[] = { station, stationData[0], stationData[2] };
			return data;
		}
		else{
			return null;
		}
	}
}
