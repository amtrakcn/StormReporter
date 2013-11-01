package com.wisc.StormReporter;


import java.io.DataInputStream;

import java.util.ArrayList;
import java.util.HashMap;



import static com.wisc.StormReporter.Constants.EMAIL;
import static com.wisc.StormReporter.Constants.NAME;
import static com.wisc.StormReporter.Constants.STATE;
import static com.wisc.StormReporter.Constants.PW;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class Prefs extends PreferenceActivity {
	SharedPreferences prefs;
	EditTextPreference username;
	ListPreference home_station;
	EditTextPreference verification_code;
	Preference change_pw;
	boolean newUser = false;
	String USERNAME = "";
	String STATIONEMAIL = "";
	String[] stationEmails;
	String[] stationNames;
	//String USERPW = "";
	HashMap<String, String> STATIONS;
	ArrayList<String[]>stationdata;
	int requestCode;
	StationData stations;
	String oldHomeStation ="";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		//Use a try/catch block to avoid any crash when trying to retrieve data from the parent
		//activity
		try {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				newUser = extras.getBoolean("newUser");
				//USERPW = extras.getString("USERPW");
				//USERNAME = extras.getString("username");
			}
		} catch (Exception e) {
			Log.e("IntentCatch","Can't input bundle from intent");

		}

		//Get the preferences currently set
		getPrefs();

		//Call getStations() to load the STATION variable with appropriate data that can then be used by loadStations()
		loadStations(); //Sets STATIONS variable to be used

		//Evaluate if on the creation of this preferences activity the user is considered new
		//or there is data that has not been set and set the interface accordingly
		if(!newUser || !USERNAME.equals("none") || !STATIONEMAIL.equals("none")){
			if(!USERNAME.equals("none")){
				username.setSummary("Currently: " + USERNAME);
			}
			if(home_station.getEntry() != null){
				home_station.setSummary(home_station.getEntry());

				verification_code.setDialogMessage("Verification code for " + home_station.getEntry() + ":");				
				try{
					if(prefs.getBoolean("isTrainedSpotter", false)){
						verification_code.setSummary("Status: Verified Storm Reporter");
					}
					else{
						verification_code.setEnabled(true);
					}
				}catch(Exception e){
					Log.e("(Prefs) User Verification Error", "Error getting type of user preference from preferences");
				}

			}
		}



	}
	@Override
	public void onDestroy(){
		super.onDestroy();

	}

	@Override
	public void onPause(){
		stations.close();
		super.onPause();
	}

	@Override
	public void onResume(){
		super.onResume();
	}

	public void getPrefs(){
		stations = new StationData(this);


		//Load and locate any already defined preferences that should be displayed
		//in this activity
		prefs =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		USERNAME = prefs.getString("username", "none");
		//STATIONEMAIL = prefs.getString("home_station", "none");
		oldHomeStation = prefs.getString("home_station", "none");

		//Instantiate the preferences we will be using to update/set data with
		addPreferencesFromResource(R.xml.settings);
		change_pw = (Preference) findPreference("change_password");
		username = (EditTextPreference) findPreference("username");
		home_station = (ListPreference) findPreference("home_station");
		verification_code = (EditTextPreference) findPreference("verification_code");

		//Set an on preference click listener to change to the set password
		//activity when the generic preference for changing the password is clicked
		change_pw.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(com.wisc.StormReporter.Prefs.this, passwordActivity.class);
				//i.putExtra("USERPW", USERPW);
				startActivityForResult(i,requestCode);

				return true;
			}

		});

		verification_code.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference arg0) {
				verification_code.getEditText().setText("");
				//Need to reset the verification_code string so the preference changes and on_preference change is called
				prefs.edit().putString("verification_code", "").commit();
				Log.e("TEST", prefs.getString("verification_code", "none"));

				return false;
			}

		});


		//set a generic preference change listener over the entire collection of preferences
		//to automatically update the preferences and display to the user any changes to verify
		//to the user that preferences have been changed
		prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener(){
			public void onSharedPreferenceChanged(SharedPreferences arg0,
					String arg1) {Log.e("TEST", "in shared preferences change");
				if (arg1.equals("username")){
					USERNAME = prefs.getString("username", "none");
					username.setSummary("Currently: " + USERNAME);
				}
				if (arg1.equals("home_station")){
					home_station.setSummary(home_station.getEntry());
					
					//Compare to existing home station in preferences (if one), don't want to remake the user
					//input a station verification code if they hit the same home nws station as before			
					if(!prefs.getString("home_station", "none").equals(oldHomeStation)){
						home_station.setSummary(home_station.getEntry());
						prefs.edit().putBoolean("isTrainedSpotter",false).commit();
						verification_code.setEnabled(true);
						verification_code.setDialogMessage("Verification code for " + home_station.getEntry() + ":");
						verification_code.setSummary("Indicate if you are a certified spotter");
						oldHomeStation = prefs.getString("home_station", "none");
					}
					
				}
				if(arg1.equals("verification_code")){
					Editor prefEditor = prefs.edit();
					if(userIsTrained(true)){
						prefEditor.putBoolean("isTrainedSpotter",true).commit();
						verification_code.setSummary("Status: Verified Storm Reporter");
						verification_code.setEnabled(false);
						Log.i("(Prefs) test",String.valueOf(prefs.getBoolean("isTrainedSpotter", false)));
					}
					else{
						prefEditor.putBoolean("isTrainedSpotter",false).commit();
						Log.i("(Prefs) test",String.valueOf(prefs.getBoolean("isTrainedSpotter", false)));
					}
				}
			}

		});
	}
	//This method parses the raw stations file based on the coding put in place to distinguish the different qualities of ecah station
	//This method will need to be updated to take in more data as the app becomes more functional. Thus, the HashMap will need the key
	//to point to information about the station more than just its email address (to include phone number, GPS location, etc.)
	private void loadStations(){
		String [] STATIONS;
		try{

			String[] choose = { NAME, STATE };
			String ORDER_BY = STATE + " ASC, "+ NAME + " ASC";
			Cursor cur = stations.getStation(choose, null, null, null, ORDER_BY);
			STATIONS = new String[cur.getCount()];
			int i = 0;
			while(cur.moveToNext()){
				STATIONS[i] = cur.getString(0);
				i++;
			}
			cur.close();
		} catch (Exception e) {
			Log.i("Stations Input in Prefs class", "I/O Error");
			STATIONS = new String[1];
			STATIONS[0] = "Error Loading Stations";
		}

		home_station.setEntries(STATIONS);
		home_station.setEntryValues(STATIONS);


	}

	private boolean userIsTrained(boolean showUser){
		String enteredCode = prefs.getString("verification_code", "unset");
		String stationCode = "";
		
		if(!enteredCode.equals("unset") && enteredCode.length() > 0){
			try{
				String[] FROM = { NAME, PW };
				String where = NAME;
				String[] findParam = { prefs.getString("home_station", "nowhere") };
				Cursor cur = stations.getStation(FROM, where, findParam, null, null);
				if (cur.getCount() == 1){
					cur.moveToNext();
					stationCode = cur.getString(1);
				}else{
					stationCode = "";
					if(showUser){
						Toast.makeText(this, "Error retrieving station spotter verification code", 1000).show();
					}
					return false;
				}
				cur.close();
			}catch(Exception e){
				e.printStackTrace();
				stationCode = "";
				if(showUser){
					Toast.makeText(com.wisc.StormReporter.Prefs.this, "Error retrieving station spotter verification code", 1000).show();
				}
				return false;
			}

			if(enteredCode.equals(stationCode)){
				if(showUser){
					Toast.makeText(this, "Code Verified", 1000).show();
				}
				return true;
			}
			else{
				if(showUser){
					Toast.makeText(this, "Invalid spotter verification code..." + '\n' + '\n'
							+ "Please contact your home NWS station" + '\n' + "if you believe this is an error", 1000).show();

				}
				return false;
			}
		}
		else{
			return false;
		}
	}

	/**
	 * This method evaluates the result of the activity called by this activity to evaluate whether or
	 * not the user wants to quit this app or start a new search
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == this.requestCode){
			//Evaluate what the result was of the activity that this activity called
			//If the result is 999 the activity called was the passwordActivity and 
			//the user successfully changed his password, so we don't want the user to permanently 
			//be logged in anymore
			if (resultCode == 999) {
				prefs.edit().putBoolean("permLogin",false).commit();
			}

		}
	}

	/*******************************************************************************************************************************/
	/*******************************			OPTIONS MENU METHODS					********************************************/
	/*******************************************************************************************************************************/	

	/**
	 * This method creates the menu given the resource xml file for this menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.prefs_menu, menu);
		return true;
	}

	/**
	 * This method responds to the user's response to the menu items
	 * 
	 * If the user wants to start a new search, inform them with toast that they
	 * are currently at the right activity
	 * 
	 * If they want to exit, terminate the activity
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.prefs_back){
			onDestroy();
			finish();
		}
		else if (id == R.id.help){
			/**TODO Make and display a help file to teach the users about the preferences and settings
			 * 
			 */
			return true;
		}
		return false;
	}

}
