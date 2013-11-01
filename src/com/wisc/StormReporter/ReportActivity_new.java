package com.wisc.StormReporter;
import static com.wisc.StormReporter.Constants.COUNTY;
import static com.wisc.StormReporter.Constants.NAME;
import static com.wisc.StormReporter.Constants.STATE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;



public class ReportActivity_new extends Activity{

	final int FROM_CHANGE_LOCATION = 1111;
	final int FROM_CHANGE_DATE_AND_TIME = 2222;

	RelativeLayout reportLayout;
	LocationData locations;
	//View initialization for time and location (in report header) block
	View timeBlock;
	View locationBlock;
	TextView timeTextView;
	TextView locationTextView;
	myPosition position;
	//myPosition position;
	Button timeChange;
	Button locationChange;
	String dateAndTime;
	String location;
	Geocoder g;
	LocationManager lm;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds



	//View initialization for photos (in report header) block
	View photoBlock;
	TextView photoTextView;
	ImageView photoImageView;
	private Bitmap photoImageBitmap;
	Button addPhoto;
	boolean PHOTOTAKEN;
	takePicture stormPicture;
	Uri photoToSend;
	String reportAlbumName;
	Bitmap genericBitmap;
	private static final int TAKE_PHOTO = 123445;
	Intent takePictureIntent;

	//View and data structure initialization for weather type
	RelativeLayout weatherTypeLayout;
	View otherWeatherType;
	View weatherType1;
	View weatherType2;
	View weatherType3;
	EditText otherWeatherTypeTextBox;
	String otherWeatherTypeText;
	int numberOfWeatherTypes;
	String currChosenWeatherType;
	ArrayList<String> weatherTypes;
	AlertDialog weatherTypeDialog;

	//View and data structure initialization for tornado detail
	View tornadoDetail;
	AlertDialog tornadoLocationDialog;
	AlertDialog tornadoHeadingDialog;
	String currTornadoLocation;
	String currTornadoHeading;
	Boolean tornadoAcrossCountyLine;
	CheckBox tornadoAcrossCountyLineCheckBox;

	//View and data structure initialization for funnel cloud detail
	View funnelCloudDetail;
	AlertDialog funnelCloudLocationDialog;
	AlertDialog funnelCloudHeadingDialog;
	String currFunnelCloudLocation;
	String currFunnelCloudHeading;
	Boolean funnelCloudAcrossCountyLine;
	CheckBox funnelCloudAcrossCountyLineCheckBox;

	//View and data structure initialization for wall cloud detail
	View wallCloudDetail;
	AlertDialog wallCloudLocationDialog;
	AlertDialog wallCloudHeadingDialog;
	String currWallCloudLocation;
	String currWallCloudHeading;
	Boolean wallCloudAcrossCountyLine;
	CheckBox wallCloudAcrossCountyLineCheckBox;
	Boolean wallCloudRotating;
	CheckBox wallCloudRotatingCheckBox;

	//View and data structure initialization for hail details
	View hailDetail;
	TextView hailSize1;
	TextView hailSize2;
	TextView hailSize3;
	TextView hailSizeMeasuredOrEstimatedTextView;
	int numberOfHailSizes;
	String currChosenHailSize;
	ArrayList<String> hailSizes;
	AlertDialog hailSizeDialog;
	RadioButton hailMeasuredRadioButton;
	RadioButton hailEstimatedRadioButton;
	Button addHailSize1;
	Button addHailSize2;
	Button addHailSize3;
	boolean hailSizeMeasured;
	boolean hailSizeEstimated;

	//View and data structure initialization for hail details
	View highWindDetail;
	TextView windSpeedTextView;
	TextView windSpeedMeasuredOrEstimatedTextView;
	String currChosenWindSpeed;
	AlertDialog windSpeedDialog;
	RadioButton windSpeedMeasuredRadioButton;
	RadioButton windSpeedEstimatedRadioButton;
	Button addWindSpeed;
	boolean windSpeedMeasured;
	boolean windSpeedEstimated;

	//View and data structure initialization for damage/injuries/fatalities block
	View damageAndInjuries;
	CheckBox damageCheckBox;
	CheckBox injuriesCheckBox;
	CheckBox fatalitiesCheckBox;
	boolean DAMAGE;
	boolean INJURIES;
	boolean FATALITIES;

	//View and data structure initialization for extra comments block
	View otherDetails;
	EditText otherDetailsTextBox;
	TextView otherDetailsTextView;
	Button otherDetailsButton;
	Button editOtherDetailsButton;
	String currOtherDetails;

	EditText editLocation;
	Button confirmLocationChange;
	Button cancelLocationChange;

	//View and data structure initialization for extra comments block
	View submitAndSaveBlock;
	Button submitButton;

	//create a new report object
	StormReport report;

	//Data structure initialization for user location and current date and time
	String USER_STATE_LOC;
	String USER_COUNTY_LOC;
	Boolean userOverrideDateAndTime;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.report_main_new);
		locations = new LocationData(this);
		report = new StormReport(getApplicationContext());
		reportLayout = (RelativeLayout) findViewById(R.id.reportLayout);

		timeBlock = (View) reportLayout.findViewById(R.id.timeBlock);
		locationBlock = (View) reportLayout.findViewById(R.id.locationBlock);
		timeTextView = (TextView) timeBlock.findViewById(R.id.timeTextView);
		timeChange = (Button) timeBlock.findViewById(R.id.changeTime);
		locationTextView = (TextView) locationBlock.findViewById(R.id.locationTextView);
		locationChange = (Button) locationBlock.findViewById(R.id.changeLocation);
		dateAndTime = "";
		location = "";

		//Now initialize the photo block
		photoBlock = (View) reportLayout.findViewById(R.id.photoBlock);
		photoTextView = (TextView) photoBlock.findViewById(R.id.photoTextView);
		photoImageView = (ImageView) photoBlock.findViewById(R.id.photoImageView);
		addPhoto = (Button) photoBlock.findViewById(R.id.addPhoto);
		PHOTOTAKEN = false;

		userOverrideDateAndTime = false;
		
		genericBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);

		g = new Geocoder(this, Locale.getDefault());
		//Setup the current time and location for the user
		findTimeAndLocation();

		//Setup the weather type graphics
		weatherTypeLayoutSetup();

		//Setup the tornado detail graphics
		tornadoDetailLayoutSetup();

		//Setup the funnel cloud detail graphics
		funnelCloudDetailLayoutSetup();

		//Setup the wall cloud detail graphics
		wallCloudDetailLayoutSetup();

		//Setup the hail detail graphics
		hailDetailLayoutSetup();

		//Setup the high wind detail graphics
		highWindDetailLayoutSetup();

		//Setup the damage/injuriy/fatality detail graphics
		damageAndInjuryDetailLayoutSetup();

		//Setup other details graphics
		otherDetailsLayoutSetup();

		//Setup submit and save report graphics
		submitAndSaveLayoutSetup();

	}

	@Override
	public void onRestart(){
		/*if (weatherTypeDialog.isShowing()){
			weatherTypeDialog.cancel();
		}*/
		//Log.e("In restart","check");
		//clearAllReportData();
		//this.onCreate(new Bundle());
		report = new StormReport(getApplicationContext());
		findTimeAndLocation();
		super.onRestart();
	}

	public void addPhoto(View v){
		PHOTOTAKEN = true;
		//Go through capturing the photo

		reportAlbumName = getAlbumName();

		addPhoto.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				removePhoto(arg0);


			}});

		//Display appropriate graphics
		photoTextView.setVisibility(View.GONE);
		photoImageView.setImageBitmap(genericBitmap);
		photoImageView.setVisibility(View.VISIBLE);
		addPhoto.setText(R.string.erase);
		//TODO: Insert this into above method

		try {
			stormPicture = new takePicture(reportAlbumName);

			try {
				takePictureIntent = stormPicture.getPictureIntent(TAKE_PHOTO);
				startActivityForResult(takePictureIntent, TAKE_PHOTO);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		//photoImageView.setImageBitmap(stormPicture.getCurrentPhotoBitmap());



		/*
		Button picBtn = (Button) findViewById(R.id.btnIntend);
		setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);
		 */
	}
	/**
	 * Overrides regular activity result to handle the camera's output.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == FROM_CHANGE_LOCATION){
			if(resultCode == Activity.RESULT_CANCELED){
				//Log.e("Test onactivityresult","CANCELED");
			}
			else if(resultCode == Activity.RESULT_OK){
				//Log.e("Test onactivityresult","OK");
				USER_STATE_LOC = data.getStringExtra("STATE");
				USER_COUNTY_LOC = data.getStringExtra("COUNTY");
				String toDisplay = USER_COUNTY_LOC + " County, " + USER_STATE_LOC;
				if(data.getStringExtra("SPECIFIC_LOC").length() != 0){
					toDisplay = "Location: " + '\n' + data.getStringExtra("SPECIFIC_LOC") + " (" + toDisplay+")";
				}
				else{
					toDisplay = "Location: " + '\n' + toDisplay;
				}
				locationTextView.setText(toDisplay);
			}
		}
		else if(requestCode == FROM_CHANGE_DATE_AND_TIME){
			if(resultCode == Activity.RESULT_CANCELED){
				userOverrideDateAndTime = false;
				//Log.e("Test onactivityresult","CANCELED");
			}
			else if(resultCode == Activity.RESULT_OK){
				//Log.e("Test onactivityresult","OK");
				String toDisplay = "Date and Time:" + '\n';
				toDisplay = toDisplay + data.getStringExtra("MONTH") +" " + data.getStringExtra("DAY") + ", " + data.getStringExtra("YEAR");
				toDisplay = toDisplay + " " + data.getStringExtra("HOUR")+":"+data.getStringExtra("MINUTE");
				if(data.getBooleanExtra("PM", false)){
					toDisplay = toDisplay + " PM";
				}
				else{
					toDisplay = toDisplay + " AM";
				}
				toDisplay = toDisplay + " (Local Time)";
				userOverrideDateAndTime = true;
				timeTextView.setText(toDisplay);
			}
		}
		else if(requestCode == TAKE_PHOTO){
			if (resultCode == RESULT_OK) {
				try{
					stormPicture.handleCameraPhoto(data, photoImageView);
					Intent mediaScanIntent = stormPicture.galleryAddPic();
					this.sendBroadcast(mediaScanIntent);
					photoImageView.setImageBitmap(stormPicture.getCurrentPhotoBitmap());
					photoToSend = stormPicture.getPictureURI();
				}catch(Exception e){
					Toast.makeText(this, "Error loading picture, please try again...", 1000).show();
				}
			}
		}

	}
	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
			) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
					getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public String getAlbumName(){
		return getString(R.string.album_name);
	}

	public void removePhoto(View v){
		PHOTOTAKEN = false;
		//Remove the photo and reset the photo graphics
		photoTextView.setVisibility(View.VISIBLE);
		photoImageView.setVisibility(View.GONE);
		addPhoto.setText(R.string.add);
		addPhoto.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				addPhoto(arg0);
			}

		});
	}

	public void findTimeAndLocation(){
		//Start with the current time

		if (!userOverrideDateAndTime){
			try{
				Calendar cal = Calendar.getInstance();
				dateAndTime = "Date and Time:" + '\n' + java.text.DateFormat.getDateTimeInstance().format(cal.getTime());
				TimeZone tz = cal.getTimeZone();
				String tzDisplay = tz.getDisplayName(tz.inDaylightTime(cal.getTime()), TimeZone.SHORT, Locale.getDefault());
				if(!(tzDisplay.contains("GMT"))){
					dateAndTime = dateAndTime + " (" + tzDisplay+ ")";
				}else{
					dateAndTime = dateAndTime + " (Local Time)";
				}
				//set the text view to show the current date and time
				timeTextView.setText(dateAndTime);
			}catch (Exception e){

			}
		}

		//Now figure out the current location: WILL COMPLETE LATER

		/*lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		LocationListener mlocListener = new MyLocationListener(); 

		lm.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
				mlocListener
				);
		//lm.removeUpdates(mlocListener);

		String currentLocation = "Location:";
		try{
			position = new myPosition(lm, g);

			String locationSpecifics;
			//currentLocation = currentLocation + position.getAddress() + position.getCounty() + "(" + String.valueOf(position.getLatitude()) + "," + String.valueOf(position.getLongitude()) + ")";
			//currentLocation  = currentLocation + "(" + String.valueOf(locationManager.getLastKnownLocation(locationManager.getProvider("Test").getName()).getLatitude()) + "," +String.valueOf(locationManager.getLastKnownLocation(locationManager.getProvider("Test").getName()).getLongitude() + ")");
			locationSpecifics = position.getAddress() + ", \n" + position.getCounty() + " County";
			currentLocation = currentLocation + " \n" + locationSpecifics;
			locationTextView.setText(currentLocation);

		}catch (Exception e){
			e.printStackTrace();
			Log.e("In main", "GPS test did not work");
		}

		//locationManager.removeTestProvider("Test");
		 */


	}

	public void changeLocation(View v){
		Intent i = new Intent(com.wisc.StormReporter.ReportActivity_new.this, locationActivity.class);
		startActivityForResult(i, FROM_CHANGE_LOCATION);
	}
	public void changeTime(View v){
		Intent i = new Intent(com.wisc.StormReporter.ReportActivity_new.this, dateAndTimeActivity.class);
		startActivityForResult(i, FROM_CHANGE_DATE_AND_TIME);
	}


	/**
	 * Method to check for change in user's position. Taken from the example I 
	 * found so I'm not sure if it's entirely needed.  With some investigation we 
	 * could trim it out, possibly.
	 * @author Tyrell
	 *
	 */
	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			String currentLocation = "Location:";
			try{
				position = new myPosition(lm, g);

				String locationSpecifics;
				//currentLocation = currentLocation + position.getAddress() + position.getCounty() + "(" + String.valueOf(position.getLatitude()) + "," + String.valueOf(position.getLongitude()) + ")";
				//currentLocation  = currentLocation + "(" + String.valueOf(locationManager.getLastKnownLocation(locationManager.getProvider("Test").getName()).getLatitude()) + "," +String.valueOf(locationManager.getLastKnownLocation(locationManager.getProvider("Test").getName()).getLongitude() + ")");
				locationSpecifics = position.getAddress() + ", \n" + position.getCounty() + " County";
				currentLocation = currentLocation + " \n" + locationSpecifics;
				locationTextView.setText(currentLocation);

			}catch (Exception e){
				e.printStackTrace();
				Log.e("In main", "GPS test did not work");
			}
			/*
			String message = String.format(
					"New Location \n Longitude: %1$s \n Latitude: %2$s",
					location.getLongitude(), location.getLatitude()
			);
			Toast.makeText(ReportActivity_new.this, message, Toast.LENGTH_LONG).show();
			 */
		}

		public void onStatusChanged(String s, int i, Bundle b) {
			Toast.makeText(ReportActivity_new.this, "Provider status changed",
					Toast.LENGTH_LONG).show();
		} 

		public void onProviderDisabled(String s) {
			Toast.makeText(ReportActivity_new.this,
					"Provider disabled by the user. GPS turned off",
					Toast.LENGTH_LONG).show();
		}
		public void onProviderEnabled(String s) {
			Toast.makeText(ReportActivity_new.this,
					"Provider enabled by the user. GPS turned on",
					Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * WEATHER TYPE METHODS
	 */
	public void weatherTypeLayoutSetup(){
		weatherTypeLayout = (RelativeLayout) findViewById(R.id.weatherTypeLayout);
		weatherType1 = (View) weatherTypeLayout.findViewById(R.id.weatherType1);
		weatherType2 = (View) weatherTypeLayout.findViewById(R.id.weatherType2);
		weatherType3 = (View) weatherTypeLayout.findViewById(R.id.weatherType3);
		//otherWeatherType = weatherTypeLayout.findViewById(R.id.otherWeatherType);
		//Remove the other weather type view until the "other" weather type is selected by the user
		//from the drop down menu
		//otherWeatherType.setVisibility(View.GONE);
		//otherWeatherTypeText = "";
		weatherType1.setVisibility(View.VISIBLE);

		numberOfWeatherTypes=0;

		weatherTypes = new ArrayList<String>();

		//Set up listener for the edit text box for other weather type
		/*otherWeatherTypeTextBox = (EditText) otherWeatherType.findViewById(R.id.otherWeatherTypeTextBox);
		otherWeatherTypeTextBox.setOnEditorActionListener(new OnEditorActionListener(){

			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if(arg2.equals(KeyEvent.KEYCODE_BACK) || arg2.equals(KeyEvent.KEYCODE_ENTER)){
					addOtherWeatherType(arg0);
				}
				return false;
			}

		});*/

	}

	public void displayWeatherType(View v){
		Boolean choseOtherWeatherType =false;
		//displayWeatherTypeDialog();
		weatherTypeDialog.dismiss();

		TextView type;

		if (weatherTypes.indexOf(currChosenWeatherType) == -1){


			if (numberOfWeatherTypes < 3){
				//Evaluate whether or not the user chose to add an "other" weather type so we display the text box
				if(currChosenWeatherType.equalsIgnoreCase("other")){
					//make the other weather type block visible
					//otherWeatherType.setVisibility(View.VISIBLE);
					choseOtherWeatherType=true;
				}
				else if(currChosenWeatherType.equalsIgnoreCase("tornado")){
					tornadoDetail.setVisibility(View.VISIBLE);
				}
				else if(currChosenWeatherType.equalsIgnoreCase("funnel cloud")){
					funnelCloudDetail.setVisibility(View.VISIBLE);
				}
				else if(currChosenWeatherType.equalsIgnoreCase("wall cloud")){
					wallCloudDetail.setVisibility(View.VISIBLE);
				}
				else if(currChosenWeatherType.equalsIgnoreCase("hail")){
					hailDetail.setVisibility(View.VISIBLE);
				}
				else if(currChosenWeatherType.equalsIgnoreCase("high wind")){
					highWindDetail.setVisibility(View.VISIBLE);
				}
				//Change the button (which would be the parameter v in this case to show a negative
				//and change the onClick for that button to removeWeatherType
				Button b = (Button) v;
				b.setText(R.string.erase);
				b.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						removeWeatherType(v);
					}

				});

				//If first inputed weather type
				if(numberOfWeatherTypes == 0){
					// If this is the first weather type, display the damage/injuries/fatalities block
					//and the other details/comments block as well as the submit and save button graphics
					damageAndInjuries.setVisibility(View.VISIBLE);
					otherDetails.setVisibility(View.VISIBLE);
					submitAndSaveBlock.setVisibility(View.VISIBLE);


					weatherType1.setVisibility(View.VISIBLE);
					type = (TextView) weatherType1.findViewById(R.id.typeblock_weatherType);

					if (!choseOtherWeatherType){
						type.setText(currChosenWeatherType);
					}else{
						otherWeatherType = (View)weatherType1.findViewById(R.id.otherWeatherTypeInView);
						type.setVisibility(View.GONE);
						otherWeatherType.setVisibility(View.VISIBLE);
						otherWeatherTypeTextBox = (EditText) otherWeatherType.findViewById(R.id.otherWeatherTypeTextBox);
						otherWeatherTypeTextBox.setOnEditorActionListener(new OnEditorActionListener(){

							public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
								if(arg2.equals(KeyEvent.KEYCODE_BACK) || arg2.equals(KeyEvent.KEYCODE_ENTER)){
									addOtherWeatherType(arg0);
								}
								return false;
							}

						});

						otherWeatherTypeText = "";
					}

					weatherType2.setVisibility(View.VISIBLE);
				}
				if(numberOfWeatherTypes == 1){
					type = (TextView) weatherType2.findViewById(R.id.typeblock_weatherType);
					if (!choseOtherWeatherType){
						type.setText(currChosenWeatherType);
					}else{
						otherWeatherType = (View)weatherType2.findViewById(R.id.otherWeatherTypeInView);
						type.setVisibility(View.GONE);
						otherWeatherType.setVisibility(View.VISIBLE);
						otherWeatherTypeTextBox = (EditText) otherWeatherType.findViewById(R.id.otherWeatherTypeTextBox);
						otherWeatherTypeTextBox.setOnEditorActionListener(new OnEditorActionListener(){

							public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
								if(arg2.equals(KeyEvent.KEYCODE_BACK) || arg2.equals(KeyEvent.KEYCODE_ENTER)){
									addOtherWeatherType(arg0);
								}
								return false;
							}

						});

						otherWeatherTypeText = "";
					}
					weatherType3.setVisibility(View.VISIBLE);

					weatherType1.findViewById(R.id.typeblock_buttonAddWeather).setVisibility(View.INVISIBLE);
					weatherType1.findViewById(R.id.typeblock_buttonAddWeather).setClickable(false);
				}
				if(numberOfWeatherTypes == 2){
					type = (TextView) weatherType3.findViewById(R.id.typeblock_weatherType);
					if (!choseOtherWeatherType){
						type.setText(currChosenWeatherType);
					}else{
						otherWeatherType = (View)weatherType3.findViewById(R.id.otherWeatherTypeInView);
						type.setVisibility(View.GONE);
						otherWeatherType.setVisibility(View.VISIBLE);
						otherWeatherTypeTextBox = (EditText) otherWeatherType.findViewById(R.id.otherWeatherTypeTextBox);
						otherWeatherTypeTextBox.setOnEditorActionListener(new OnEditorActionListener(){

							public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
								if(arg2.equals(KeyEvent.KEYCODE_BACK) || arg2.equals(KeyEvent.KEYCODE_ENTER)){
									addOtherWeatherType(arg0);
								}
								return false;
							}

						});

						otherWeatherTypeText = "";
					}
					//weatherType4.setVisibility(View.VISIBLE);

					weatherType2.findViewById(R.id.typeblock_buttonAddWeather).setVisibility(View.INVISIBLE);
					weatherType2.findViewById(R.id.typeblock_buttonAddWeather).setClickable(false);
				}
				/*if(numberOfWeatherTypes == 3){
				type = (TextView) weatherType4.findViewById(R.id.typeblock_weatherType);
				type.setText(currChosenWeatherType);

				weatherType3.findViewById(R.id.typeblock_buttonAddWeather).setVisibility(View.INVISIBLE);
				weatherType3.findViewById(R.id.typeblock_buttonAddWeather).setClickable(false);
			}*/

				//finally...add the currChosenWeatherType to the weatherTypes array list and update
				//the number of weather types reported so far
				weatherTypes.add(numberOfWeatherTypes, currChosenWeatherType);

				numberOfWeatherTypes++;
				/*int temp = weatherTypes.size();
			if(temp == 0){
				Toast.makeText(this,"nothing", 1000).show();
			}else if (temp == 1){
				Toast.makeText(this,"1: "+weatherTypes.get(0), 1000).show();
			}else if (temp == 2){
				Toast.makeText(this,"1: "+weatherTypes.get(0)+", 2: "+weatherTypes.get(1), 1000).show();
			}else if (temp == 3){
				Toast.makeText(this,"1: "+weatherTypes.get(0)+", 2: "+weatherTypes.get(1) + ", 3: " +weatherTypes.get(2), 1000).show();
			}else if (temp ==4){
				Toast.makeText(this,"1: "+weatherTypes.get(0)+", 2: "+weatherTypes.get(1) + ", 3: " +weatherTypes.get(2)+", 4: "+weatherTypes.get(3), 1000).show();
			}*/
			}

		}else{
			Toast.makeText(this, "Already submitted weather type: "+currChosenWeatherType, 1000).show();
		}

	}

	public void removeWeatherType(View button){
		//Toast.makeText(this, "It worked!", 1000).show();
		boolean removeOtherWeatherType = false;
		TextView type;
		Button b;

		//Evaluate what weather type is being erased and remove the asssociated details
		//from the screen
		String typeToRemove = weatherTypes.get(numberOfWeatherTypes-1);
		if(typeToRemove.equalsIgnoreCase("tornado")){
			eraseAllTornadoDetails();
		}
		else if(typeToRemove.equalsIgnoreCase("funnel cloud")){
			eraseAllFunnelCloudDetails();
		}
		else if(typeToRemove.equalsIgnoreCase("wall cloud")){
			eraseAllWallCloudDetails();
		}
		else if(typeToRemove.equalsIgnoreCase("hail")){
			eraseAllHailDetails();
		}
		else if(typeToRemove.equalsIgnoreCase("high wind")){
			eraseAllHighWindDetails();
		}


		switch(numberOfWeatherTypes){
		case 0:
			//weatherType1.setVisibility(View.GONE);
			break;
		case 1:
			//If this is called, the user wanted to remove the first displayed weather type
			//so remove it from the arraylist, but first check to see if was the "other" weather type

			if(weatherTypes.get(0).equalsIgnoreCase("other")){
				removeOtherWeatherType = true;
			}

			//Since this is the only remaining weather type on the board and the user wants to delete it, 
			//remove the damage/injuries/fatalities block and extra details/comments block as well 
			//as the submit/save buttons
			eraseAllDamageAndInjuryDetails();
			eraseAllOtherDetails();
			eraseSubmitAndSaveGraphics();

			//Remove the weather type from the array list
			weatherTypes.remove(0);

			type = (TextView) weatherType1.findViewById(R.id.typeblock_weatherType);
			type.setText(R.string.genericAddWeatherType);
			type.setVisibility(View.VISIBLE);

			//Make the first weathertype add button visible again
			b = (Button) weatherType1.findViewById(R.id.typeblock_buttonAddWeather);
			b.setVisibility(View.VISIBLE);
			b.setText(R.string.add);
			b.setClickable(true);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					addWeatherType(v);
				}
			});

			//remove the blank addition button
			weatherType2.setVisibility(View.GONE);
			break;
		case 2:
			//If this is called, the user wanted to remove the second displayed weather type
			//so remove it from the arraylist but first check to see if was the "other" weather type
			if(weatherTypes.get(1).equalsIgnoreCase("other")){
				removeOtherWeatherType = true;
			}
			weatherTypes.remove(1);

			type = (TextView) weatherType2.findViewById(R.id.typeblock_weatherType);
			type.setText(R.string.genericAddWeatherType);
			type.setVisibility(View.VISIBLE);
			//Make the second weathertype add button visible again
			b = (Button) weatherType2.findViewById(R.id.typeblock_buttonAddWeather);
			b.setVisibility(View.VISIBLE);
			b.setText(R.string.add);
			b.setClickable(true);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					addWeatherType(v);
				}
			});
			//Put the subtract button back on the first weather type
			b = (Button) weatherType1.findViewById(R.id.typeblock_buttonAddWeather);
			b.setText(R.string.erase);
			b.setVisibility(View.VISIBLE);
			b.setClickable(true);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					removeWeatherType(v);
				}
			});

			//remove the blank addition button
			weatherType3.setVisibility(View.GONE);
			break;
		case 3:
			//If this is called, the user wanted to remove the third displayed weather type
			//so remove it from the arraylist but first check to see if was the "other" weather type
			if(weatherTypes.get(2).equalsIgnoreCase("other")){
				removeOtherWeatherType = true;
			}
			weatherTypes.remove(2);

			type = (TextView) weatherType3.findViewById(R.id.typeblock_weatherType);
			type.setText(R.string.genericAddWeatherType);
			type.setVisibility(View.VISIBLE);
			//Make the third weathertype add button visible again
			b = (Button) weatherType3.findViewById(R.id.typeblock_buttonAddWeather);
			b.setVisibility(View.VISIBLE);
			b.setText(R.string.add);
			b.setClickable(true);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					addWeatherType(v);
				}
			});
			//Put the subtract button back on the second weather type
			b = (Button) weatherType2.findViewById(R.id.typeblock_buttonAddWeather);
			b.setText(R.string.erase);
			b.setVisibility(View.VISIBLE);
			b.setClickable(true);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					removeWeatherType(v);
				}
			});

			//remove the blank addition button
			//weatherType4.setVisibility(View.GONE);
			break;
			/*case 4:
			//If this is called, the user wanted to remove the third displayed weather type
			//so remove it from the arraylist
			weatherTypes.remove(3);

			type = (TextView) weatherType4.findViewById(R.id.typeblock_weatherType);
			type.setText(R.string.genericAddWeatherType);

			//Make the fourth weathertype add button visible again
			b = (Button) weatherType4.findViewById(R.id.typeblock_buttonAddWeather);
			b.setVisibility(View.VISIBLE);
			b.setText(R.string.add);
			b.setClickable(true);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addWeatherType(v);
				}
			});

			//Put the subtract button back on the third weather type
			b = (Button) weatherType3.findViewById(R.id.typeblock_buttonAddWeather);
			b.setText(R.string.erase);
			b.setVisibility(View.VISIBLE);
			b.setClickable(true);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					removeWeatherType(v);
				}
			});
			break;*/


		}
		//Now evaluate whether or not the other weather type that was originally chosen was the one the user wanted to delete
		//so that we can remove it from the screen
		if(removeOtherWeatherType){
			//Reset the otherWEatherTypeTExt string to be blank
			otherWeatherTypeText = "";
			//Remove the other weather type block from the view
			otherWeatherType.setVisibility(View.GONE);
			removeOtherWeatherType(button);
		}

		numberOfWeatherTypes--;
		/*int temp = weatherTypes.size();
		if(temp == 0){
			Toast.makeText(this,"nothing", 1000).show();
		}else if (temp == 1){
			Toast.makeText(this,"1: "+weatherTypes.get(0), 1000).show();
		}else if (temp == 2){
			Toast.makeText(this,"1: "+weatherTypes.get(0)+", 2: "+weatherTypes.get(1), 1000).show();
		}else if (temp == 3){
			Toast.makeText(this,"1: "+weatherTypes.get(0)+", 2: "+weatherTypes.get(1) + ", 3: " +weatherTypes.get(2), 1000).show();
		}else if (temp ==4){
			Toast.makeText(this,"1: "+weatherTypes.get(0)+", 2: "+weatherTypes.get(1) + ", 3: " +weatherTypes.get(2)+", 4: "+weatherTypes.get(3), 1000).show();
		}*/
	}

	public void addWeatherType(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.weatherType);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("What type of weather are you reporting?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currChosenWeatherType = (String) items[item]; //add selected weather report
				displayWeatherType(currView);
			}
		});
		weatherTypeDialog = builder.create();
		weatherTypeDialog.show();
	}


	public void addOtherWeatherType(View v){
		//Set the otherWeatherTypeText value to equal what was entered into the text box
		otherWeatherTypeText = String.valueOf(otherWeatherTypeTextBox.getText());
		//Change the button
		Button otherWeatherTypeButton = (Button) otherWeatherType.findViewById(R.id.buttonAddOtherWeatherType);
		otherWeatherTypeButton.setText("Remove");
		otherWeatherTypeButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				removeOtherWeatherType(arg0);	
			}

		});
		//make the edit button visible
		otherWeatherType.findViewById(R.id.buttonEditOtherWeatherType).setVisibility(View.VISIBLE);
		otherWeatherType.findViewById(R.id.buttonEditOtherWeatherType).setClickable(true);
		//Remove the edit text box
		otherWeatherTypeTextBox.setVisibility(View.GONE);
		//Add the text view to the screen
		TextView otherWeatherTypeTextView = (TextView) otherWeatherType.findViewById(R.id.otherWeatherTypeTextView); 
		otherWeatherTypeTextView.setText(otherWeatherTypeText);
		otherWeatherTypeTextView.setVisibility(View.VISIBLE);
		//Add the added other weather type to the weatherTypes array list
		//weatherTypes.add(otherWeatherTypeText);

	}

	public void removeOtherWeatherType(View v){
		//Change the button
		Button otherWeatherTypeButton = (Button) otherWeatherType.findViewById(R.id.buttonAddOtherWeatherType);
		otherWeatherTypeButton.setText("Save");
		otherWeatherTypeButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				addOtherWeatherType(arg0);	
			}

		});
		//Remove the edit button
		otherWeatherType.findViewById(R.id.buttonEditOtherWeatherType).setClickable(false);
		otherWeatherType.findViewById(R.id.buttonEditOtherWeatherType).setVisibility(View.GONE);
		//Remove the text view from the screen
		otherWeatherType.findViewById(R.id.otherWeatherTypeTextView).setVisibility(View.GONE);
		//Bring up the edit text box
		otherWeatherTypeTextBox.setText("");
		otherWeatherTypeTextBox.setHint(R.string.weatherTypeTextEditHint);
		otherWeatherTypeTextBox.setVisibility(View.VISIBLE);
		//Remove the other weather type from the weather type array list
		//weatherTypes.remove(weatherTypes.indexOf(otherWeatherTypeText));
		otherWeatherType.setVisibility(View.GONE);

	}

	public void editOtherWeatherType(View v){
		//Remove the edit button from the screen
		otherWeatherType.findViewById(R.id.buttonEditOtherWeatherType).setClickable(false);
		otherWeatherType.findViewById(R.id.buttonEditOtherWeatherType).setVisibility(View.GONE);
		//Change the button that says remove to add
		Button otherWeatherTypeButton = (Button) otherWeatherType.findViewById(R.id.buttonAddOtherWeatherType);
		otherWeatherTypeButton.setText("Save");
		otherWeatherTypeButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				addOtherWeatherType(arg0);	
			}

		});
		//Remove the text view from the screen
		otherWeatherType.findViewById(R.id.otherWeatherTypeTextView).setVisibility(View.GONE);
		//Bring up the edit text box
		otherWeatherTypeTextBox.setText(otherWeatherTypeText);
		//otherWeatherTypeTextBox.setHint(R.string.weatherTypeTextEditHint);
		otherWeatherTypeTextBox.setVisibility(View.VISIBLE);
		//Remove the other weather type from the weather type array list
		//weatherTypes.remove(weatherTypes.indexOf(otherWeatherTypeText));
	}




	/*************************************************************************************************
	 * TORNADO DETAIL METHODS
	 *************************************************************************************************/
	public void tornadoDetailLayoutSetup(){
		tornadoDetail = (View) findViewById(R.id.tornadoDetailBlock);
		tornadoAcrossCountyLineCheckBox = (CheckBox) tornadoDetail.findViewById(R.id.tornadoAcrossCountyLineCheckBox);
		//Hide the layout and initialize all values to blanks
		eraseAllTornadoDetails();
	}

	public void eraseAllTornadoDetails(){
		//Remove the graphics
		tornadoDetail.setVisibility(View.GONE);
		//Reset the primitive data types for the tornado details
		currTornadoLocation="";
		currTornadoHeading="";
		tornadoAcrossCountyLine = false;
		//Reset the checkbox and button graphics including the onClick listeners
		tornadoAcrossCountyLineCheckBox.setChecked(false);
		TextView tHeading = (TextView) tornadoDetail.findViewById(R.id.tornadoHeading);
		tHeading.setText(R.string.genericTornadoDirectionText);
		TextView tLocation = (TextView) tornadoDetail.findViewById(R.id.tornadoLocation);
		tLocation.setText(R.string.genericTornadoObservationText);
		Button headingButton = (Button) tornadoDetail.findViewById(R.id.addTornadoHeading);
		headingButton.setText(R.string.add);
		headingButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addTornadoHeading(v);
			}

		});
		Button locationButton = (Button) tornadoDetail.findViewById(R.id.addTornadoLocation);
		locationButton.setText(R.string.add);
		locationButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addTornadoLocation(v);
			}

		});
	}


	public void addTornadoLocation(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.tornadoPositionDetail);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("Where is the tornado relative to you?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currTornadoLocation = (String) items[item]; //add selected weather report
				displayTornadoLocation(currView);
			}
		});
		tornadoLocationDialog = builder.create();
		tornadoLocationDialog.show();
	}

	public void displayTornadoLocation(View v){

		tornadoLocationDialog.dismiss();

		//Change the tornado position text view to show the chosen position
		TextView position = (TextView) tornadoDetail.findViewById(R.id.tornadoLocation);
		position.setText(currTornadoLocation);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removeWeatherType
		Button b = (Button) v;
		b.setText(R.string.erase);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				removeTornadoLocation(v);
			}

		});
	}

	public void removeTornadoLocation(View v){
		//Change the tornado position text view to show the chosen position
		TextView position = (TextView) tornadoDetail.findViewById(R.id.tornadoLocation);
		position.setText(R.string.genericTornadoObservationText);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removeWeatherType
		Button b = (Button) v;
		b.setText(R.string.add);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addTornadoLocation(v);
			}

		});
		currTornadoLocation = "";
	}

	public void addTornadoHeading(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.tornadoMotionDetail);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("Which direction does it seem to be heading?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currTornadoHeading = (String) items[item]; //add selected weather report
				displayTornadoHeading(currView);
			}
		});
		tornadoHeadingDialog = builder.create();
		tornadoHeadingDialog.show();
	}

	public void displayTornadoHeading(View v){

		tornadoHeadingDialog.dismiss();

		//Change the tornado position text view to show the chosen position
		TextView position = (TextView) tornadoDetail.findViewById(R.id.tornadoHeading);
		position.setText(currTornadoHeading);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removeWeatherType
		Button b = (Button) v;
		b.setText(R.string.erase);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				removeTornadoHeading(v);
			}

		});
	}

	public void removeTornadoHeading(View v){
		//Change the tornado position text view to show the chosen position
		TextView position = (TextView) tornadoDetail.findViewById(R.id.tornadoHeading);
		position.setText(R.string.genericTornadoDirectionText);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removeWeatherType
		Button b = (Button) v;
		b.setText(R.string.add);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addTornadoHeading(v);
			}

		});
		currTornadoHeading = "";
	}

	public void acrossCountyLineCheck(View v){
		tornadoAcrossCountyLineCheckBox = (CheckBox) v;
		if(tornadoAcrossCountyLineCheckBox.isChecked()){
			tornadoAcrossCountyLine = true;
		}
		else{
			tornadoAcrossCountyLine = false;
		}
		//Toast.makeText(this, String.valueOf(tornadoAcrossCountyLine), 1000).show();
	}



	/*************************************************************************************************
	 * FUNNEL CLOUD DETAIL METHODS
	 *************************************************************************************************/
	public void funnelCloudDetailLayoutSetup(){
		funnelCloudDetail = (View) findViewById(R.id.funnelCloudDetailBlock);
		funnelCloudAcrossCountyLineCheckBox = (CheckBox) funnelCloudDetail.findViewById(R.id.funnelCloudAcrossCountyLineCheckBox);
		//Hide the layout and initialize all values to blanks
		eraseAllFunnelCloudDetails();
	}

	public void eraseAllFunnelCloudDetails(){
		//Erase the grpahics
		funnelCloudDetail.setVisibility(View.GONE);

		//Reset the primitive detail variables
		currFunnelCloudLocation="";
		currFunnelCloudHeading="";
		funnelCloudAcrossCountyLine = false;
		//Reset the checkbox and button graphics including the button onClick listeners
		funnelCloudAcrossCountyLineCheckBox.setChecked(false);
		TextView heading = (TextView) funnelCloudDetail.findViewById(R.id.funnelCloudHeading);
		heading.setText(R.string.genericTornadoDirectionText);
		TextView location = (TextView) funnelCloudDetail.findViewById(R.id.funnelCloudLocation);
		location.setText(R.string.genericTornadoObservationText);
		Button headingButton = (Button) funnelCloudDetail.findViewById(R.id.addFunnelCloudHeading);
		headingButton.setText(R.string.add);
		headingButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addFunnelCloudHeading(v);
			}

		});
		Button locationButton = (Button) funnelCloudDetail.findViewById(R.id.addFunnelCloudLocation);
		locationButton.setText(R.string.add);
		locationButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addFunnelCloudLocation(v);
			}

		});

	}


	public void addFunnelCloudLocation(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.tornadoPositionDetail);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("Where is the funnel cloud relative to you?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currFunnelCloudLocation = (String) items[item]; //add selected weather report
				displayFunnelCloudLocation(currView);
			}
		});
		funnelCloudLocationDialog = builder.create();
		funnelCloudLocationDialog.show();
	}

	public void displayFunnelCloudLocation(View v){

		funnelCloudLocationDialog.dismiss();

		//Change the funel position text view to show the chosen position
		TextView position = (TextView) funnelCloudDetail.findViewById(R.id.funnelCloudLocation);
		position.setText(currFunnelCloudLocation);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removefunnel
		Button b = (Button) v;
		b.setText(R.string.erase);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				removeFunnelCloudLocation(v);
			}

		});
	}

	public void removeFunnelCloudLocation(View v){
		//Change the funnel position text view to show the chosen position
		TextView position = (TextView) funnelCloudDetail.findViewById(R.id.funnelCloudLocation);
		position.setText(R.string.genericTornadoObservationText);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removefunnel
		Button b = (Button) v;
		b.setText(R.string.add);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addFunnelCloudLocation(v);
			}

		});
		currFunnelCloudLocation = "";
	}

	public void addFunnelCloudHeading(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.tornadoMotionDetail);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("Which direction does it seem to be heading?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currFunnelCloudHeading = (String) items[item]; //add selected weather report
				displayFunnelCloudHeading(currView);
			}
		});
		funnelCloudHeadingDialog = builder.create();
		funnelCloudHeadingDialog.show();
	}

	public void displayFunnelCloudHeading(View v){

		funnelCloudHeadingDialog.dismiss();

		//Change the funel position text view to show the chosen position
		TextView position = (TextView) funnelCloudDetail.findViewById(R.id.funnelCloudHeading);
		position.setText(currFunnelCloudHeading);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removefunel
		Button b = (Button) v;
		b.setText(R.string.erase);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				removeFunnelCloudHeading(v);
			}

		});

	}

	public void removeFunnelCloudHeading(View v){
		//Change the funnel position text view to show the chosen position
		TextView position = (TextView) funnelCloudDetail.findViewById(R.id.funnelCloudHeading);
		position.setText(R.string.genericTornadoDirectionText);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removefunnel
		Button b = (Button) v;
		b.setText(R.string.add);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addFunnelCloudHeading(v);
			}

		});
		currFunnelCloudHeading = "";
	}

	public void funnelCloudAcrossCountyLineCheck(View v){
		funnelCloudAcrossCountyLineCheckBox = (CheckBox) v;
		if(funnelCloudAcrossCountyLineCheckBox.isChecked()){
			funnelCloudAcrossCountyLine = true;
		}
		else{
			funnelCloudAcrossCountyLine = false;
		}
		//Toast.makeText(this, String.valueOf(tornadoAcrossCountyLine), 1000).show();
	}


	/*************************************************************************************************
	 * WALL CLOUD DETAIL METHODS
	 *************************************************************************************************/
	public void wallCloudDetailLayoutSetup(){
		wallCloudDetail = (View) findViewById(R.id.wallCloudDetailBlock);
		wallCloudAcrossCountyLineCheckBox = (CheckBox) wallCloudDetail.findViewById(R.id.wallCloudAcrossCountyLineCheckBox);
		wallCloudRotatingCheckBox = (CheckBox) wallCloudDetail.findViewById(R.id.wallCloudRotatingCheckBox);

		//Hide the layout and initialize all values to blanks
		eraseAllWallCloudDetails();
	}

	public void eraseAllWallCloudDetails(){
		//Erase the grpahics
		wallCloudDetail.setVisibility(View.GONE);

		//Reset the primitive detail variables
		currWallCloudLocation="";
		currWallCloudHeading="";
		wallCloudAcrossCountyLine = false;
		wallCloudRotating = false;
		//Reset the checkbox and button graphics including the button onClick listeners
		wallCloudAcrossCountyLineCheckBox.setChecked(false);
		wallCloudRotatingCheckBox.setChecked(false);
		TextView heading = (TextView) wallCloudDetail.findViewById(R.id.wallCloudHeading);
		heading.setText(R.string.genericTornadoDirectionText);
		TextView location = (TextView) wallCloudDetail.findViewById(R.id.wallCloudLocation);
		location.setText(R.string.genericTornadoObservationText);
		Button headingButton = (Button) wallCloudDetail.findViewById(R.id.addWallCloudHeading);
		headingButton.setText(R.string.add);
		headingButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addWallCloudHeading(v);
			}

		});
		Button locationButton = (Button) wallCloudDetail.findViewById(R.id.addWallCloudLocation);
		locationButton.setText(R.string.add);
		locationButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addWallCloudLocation(v);
			}

		});

	}

	public void addWallCloudLocation(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.tornadoPositionDetail);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("Where is the wall cloud relative to you?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currWallCloudLocation = (String) items[item]; //add selected weather report
				displayWallCloudLocation(currView);
			}
		});
		wallCloudLocationDialog = builder.create();
		wallCloudLocationDialog.show();
	}

	public void displayWallCloudLocation(View v){

		wallCloudLocationDialog.dismiss();

		//Change the funel position text view to show the chosen position
		TextView position = (TextView) wallCloudDetail.findViewById(R.id.wallCloudLocation);
		position.setText(currWallCloudLocation);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removefunnel
		Button b = (Button) v;
		b.setText(R.string.erase);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				removeWallCloudLocation(v);
			}

		});
	}

	public void removeWallCloudLocation(View v){
		//Change the funnel position text view to show the chosen position
		TextView position = (TextView) wallCloudDetail.findViewById(R.id.wallCloudLocation);
		position.setText(R.string.genericTornadoObservationText);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removefunnel
		Button b = (Button) v;
		b.setText(R.string.add);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addWallCloudLocation(v);
			}

		});
		currWallCloudLocation = "";
	}

	public void addWallCloudHeading(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.tornadoMotionDetail);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("Which direction does it seem to be heading?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currWallCloudHeading = (String) items[item]; //add selected weather report
				displayWallCloudHeading(currView);
			}
		});
		wallCloudHeadingDialog = builder.create();
		wallCloudHeadingDialog.show();
	}

	public void displayWallCloudHeading(View v){

		wallCloudHeadingDialog.dismiss();

		//Change the funel position text view to show the chosen position
		TextView position = (TextView) wallCloudDetail.findViewById(R.id.wallCloudHeading);
		position.setText(currWallCloudHeading);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removefunel
		Button b = (Button) v;
		b.setText(R.string.erase);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				removeWallCloudHeading(v);
			}

		});


	}

	public void removeWallCloudHeading(View v){
		//Change the funnel position text view to show the chosen position
		TextView position = (TextView) wallCloudDetail.findViewById(R.id.wallCloudHeading);
		position.setText(R.string.genericTornadoDirectionText);
		//Change the button (which would be the parameter v in this case to show a negative
		//and change the onClick for that button to removefunnel
		Button b = (Button) v;
		b.setText(R.string.add);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addWallCloudHeading(v);
			}

		});
		currWallCloudHeading ="";
	}

	public void wallCloudAcrossCountyLineCheck(View v){
		wallCloudAcrossCountyLineCheckBox = (CheckBox) v;
		if(wallCloudAcrossCountyLineCheckBox.isChecked()){
			wallCloudAcrossCountyLine = true;
		}
		else{
			wallCloudAcrossCountyLine = false;
		}
		//Toast.makeText(this, String.valueOf(tornadoAcrossCountyLine), 1000).show();
	}

	public void wallCloudIsRotating(View v){
		wallCloudRotatingCheckBox = (CheckBox) v;
		if(wallCloudRotatingCheckBox.isChecked()){
			wallCloudRotating = true;
		}
		else{
			wallCloudRotating = false;
		}
		//Toast.makeText(this, String.valueOf(tornadoAcrossCountyLine), 1000).show();
	}


	/*************************************************************************************************
	 * HAIL DETAIL METHODS
	 *************************************************************************************************/
	public void hailDetailLayoutSetup(){
		hailDetail = (View) findViewById(R.id.hailDetailBlock);
		hailSizeMeasuredOrEstimatedTextView = (TextView) hailDetail.findViewById(R.id.hailMeasurementTextView);
		hailEstimatedRadioButton = (RadioButton) hailDetail.findViewById(R.id.hailEstimatedRadioButton);
		hailMeasuredRadioButton = (RadioButton) hailDetail.findViewById(R.id.hailMeasuredRadioButton);
		hailSize1=(TextView) hailDetail.findViewById(R.id.hailSize1TextView);
		hailSize2=(TextView) hailDetail.findViewById(R.id.hailSize2TextView);
		hailSize3=(TextView) hailDetail.findViewById(R.id.hailSize3TextView);
		addHailSize1 = (Button) hailDetail.findViewById(R.id.addHailSize1);
		addHailSize2 = (Button) hailDetail.findViewById(R.id.addHailSize2);
		addHailSize3 = (Button) hailDetail.findViewById(R.id.addHailSize3);
		hailSizes= new ArrayList<String>();
		//Hide the layout and initialize all values to blanks
		eraseAllHailDetails();
	}

	public void eraseAllHailDetails(){
		hailDetail.setVisibility(View.GONE);

		//Reset primitive data types
		hailSizeEstimated = true;
		hailSizeMeasured = false;
		currChosenHailSize = "";
		numberOfHailSizes=0;

		//erase all hail sizes from the hail sizes array list
		for (int i = 0; i < hailSizes.size(); i++){
			hailSizes.remove(i);
		}

		//Erase and reset all hail detail graphics
		//hail size measurement view:
		hailSizeMeasuredOrEstimatedTextView.setVisibility(View.GONE);
		hailEstimatedRadioButton.setVisibility(View.GONE);
		hailMeasuredRadioButton.setVisibility(View.GONE);

		//the third hail size type
		hailSize3.setText(R.string.genericHailSizeText);
		hailSize3.setVisibility(View.GONE);
		addHailSize3.setText(R.string.add);
		addHailSize3.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				addHailSize(arg0);
			}
		});
		addHailSize3.setVisibility(View.GONE);

		//the second hail size type
		hailSize2.setText(R.string.genericHailSizeText);
		hailSize2.setVisibility(View.GONE);
		addHailSize2.setText(R.string.add);
		addHailSize2.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				addHailSize(arg0);
			}
		});
		addHailSize2.setVisibility(View.GONE);

		//the first hail size type
		hailSize1.setText(R.string.genericHailSizeText);
		hailSize1.setVisibility(View.VISIBLE);
		addHailSize1.setText(R.string.add);
		addHailSize1.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				addHailSize(arg0);
			}
		});
		addHailSize1.setVisibility(View.VISIBLE);

	}
	public void addHailSize(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.hailDetail);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("What size hail are you reporting?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currChosenHailSize = (String) items[item]; //add selected weather report
				displayHailSize(currView);
			}
		});
		hailSizeDialog = builder.create();
		hailSizeDialog.show();
	}

	public void displayHailSize(View v){
		//displayWeatherTypeDialog();
		hailSizeDialog.dismiss();

		if (hailSizes.indexOf(currChosenHailSize) == -1){

			if (numberOfHailSizes < 3){
				//Change the button (which would be the parameter v in this case to show a negative
				//and change the onClick for that button to removeWeatherType
				Button b = (Button) v;
				b.setText(R.string.erase);
				b.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						removeHailSize(v);
					}

				});

				//If first inputed weather type
				if(numberOfHailSizes == 0){
					//Since this is the first added hail size...display the measurement type radio buttons
					hailSizeMeasuredOrEstimatedTextView.setVisibility(View.VISIBLE);
					hailMeasuredRadioButton.setChecked(false);
					hailMeasuredRadioButton.setVisibility(View.VISIBLE);
					hailEstimatedRadioButton.setChecked(true);
					hailEstimatedRadioButton.setVisibility(View.VISIBLE);
					hailSizeEstimated = true;
					hailSizeMeasured = false;

					//Show the chosen hail size in the text view
					hailSize1.setText(currChosenHailSize);
					//Show the next hail size block
					hailSize2.setVisibility(View.VISIBLE);

					addHailSize2.setText(R.string.add);
					addHailSize2.setOnClickListener(new OnClickListener(){
						public void onClick(View v) {
							addHailSize(v);
						}
					});
					addHailSize2.setVisibility(View.VISIBLE);
				}
				if(numberOfHailSizes == 1){

					//Show the chosen hail size in the text view
					hailSize2.setText(currChosenHailSize);
					//Show the next hail size block
					hailSize3.setVisibility(View.VISIBLE);

					addHailSize3.setText(R.string.add);
					addHailSize3.setOnClickListener(new OnClickListener(){
						public void onClick(View v) {
							addHailSize(v);
						}
					});
					addHailSize3.setVisibility(View.VISIBLE);
					//Make the very first add button disappear
					addHailSize1.setVisibility(View.INVISIBLE);
				}
				if(numberOfHailSizes == 2){
					//Show the chosen hail size in the text view
					hailSize3.setText(currChosenHailSize);
					//Show the button as a negative sign now
					addHailSize3.setText(R.string.erase);
					addHailSize3.setOnClickListener(new OnClickListener(){
						public void onClick(View v) {
							removeHailSize(v);
						}
					});
					//Make the very first add button disappear
					addHailSize2.setVisibility(View.INVISIBLE);
				}

				//finally...add the currChosenWeatherType to the weatherTypes array list and update
				//the number of weather types reported so far
				hailSizes.add(numberOfHailSizes, currChosenHailSize);

				numberOfHailSizes++;

			}

		}else{
			Toast.makeText(this, "Already submitted hail size: "+currChosenHailSize, 1000).show();
		}

	}

	public void removeHailSize(View button){

		switch(numberOfHailSizes){
		case 0:
			//weatherType1.setVisibility(View.GONE);
			break;
		case 1:
			//If this is called, the user wanted to remove the first displayed hail size
			//so remove it from the arraylist
			hailSizes.remove(0);

			hailSize1.setText(R.string.genericHailSizeText);

			//Make the first hail size add button visible again

			addHailSize1.setVisibility(View.VISIBLE);
			addHailSize1.setText(R.string.add);
			addHailSize1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					addHailSize(v);
				}
			});

			//remove the blank addition button
			hailSize2.setVisibility(View.GONE);
			addHailSize2.setVisibility(View.GONE);

			//Now we need to remove the radio buttons since there are no hail sizes being reported any more
			hailSizeMeasuredOrEstimatedTextView.setVisibility(View.GONE);
			hailSizeMeasured = false;
			hailMeasuredRadioButton.setVisibility(View.GONE);
			hailSizeEstimated=true;
			hailEstimatedRadioButton.setVisibility(View.GONE);
			break;
		case 2:
			//If this is called, the user wanted to remove the second displayed hail size
			//so remove it from the arraylist
			hailSizes.remove(1);

			hailSize2.setText(R.string.genericHailSizeText);

			//Make the second weathertype add button visible again
			addHailSize2.setVisibility(View.VISIBLE);
			addHailSize2.setText(R.string.add);
			addHailSize2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					addHailSize(v);
				}
			});
			//Put the subtract button back on the first weather type
			addHailSize1.setText(R.string.erase);
			addHailSize1.setVisibility(View.VISIBLE);
			addHailSize1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					removeHailSize(v);
				}
			});

			//remove the blank addition button
			hailSize3.setVisibility(View.GONE);
			addHailSize3.setVisibility(View.GONE);
			break;
		case 3:
			//If this is called, the user wanted to remove the third displayed hail size
			//so remove it from the arraylist 
			hailSizes.remove(2);

			hailSize3.setText(R.string.genericHailSizeText);

			//Make the third weathertype add button visible again
			addHailSize3.setVisibility(View.VISIBLE);
			addHailSize3.setText(R.string.add);
			addHailSize3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					addHailSize(v);
				}
			});
			//Put the subtract button back on the second weather type
			addHailSize2.setText(R.string.erase);
			addHailSize2.setVisibility(View.VISIBLE);
			addHailSize2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					removeHailSize(v);
				}
			});

			break;


		}
		numberOfHailSizes--;

	}

	public void hailEstimated(View v){
		RadioButton rb = (RadioButton) v;
		if (rb.isChecked()){
			//Set the boolean flags
			hailSizeEstimated = true;
			hailSizeMeasured = false;
			//Make sure the hail measured radio button isnt showing it is checked as well
			hailMeasuredRadioButton.setChecked(false);
		}else{
		}
	}

	public void hailMeasured(View v){
		RadioButton rb = (RadioButton) v;
		if (rb.isChecked()){
			//Set the boolean flags
			hailSizeMeasured = true;
			hailSizeEstimated = false;
			//Make sure the hail measured radio button isnt showing it is checked as well
			hailEstimatedRadioButton.setChecked(false);
		}else{
		}

	}




	/*************************************************************************************************
	 * HIGH WIND DETAIL METHODS
	 *************************************************************************************************/
	public void highWindDetailLayoutSetup(){
		highWindDetail = (View) findViewById(R.id.highWindDetailBlock);
		windSpeedMeasuredOrEstimatedTextView = (TextView) highWindDetail.findViewById(R.id.highWindMeasurementTextView);
		windSpeedEstimatedRadioButton = (RadioButton) highWindDetail.findViewById(R.id.windEstimatedRadioButton);
		windSpeedMeasuredRadioButton = (RadioButton) highWindDetail.findViewById(R.id.windMeasuredRadioButton);
		windSpeedTextView=(TextView) highWindDetail.findViewById(R.id.windSpeedTextView);
		addWindSpeed = (Button) highWindDetail.findViewById(R.id.addWindSpeed);

		//Hide the layout and initialize all values to blanks
		eraseAllHighWindDetails();
	}

	public void eraseAllHighWindDetails(){
		//Hide parent graphic
		highWindDetail.setVisibility(View.GONE);

		//Reset primitive variables and flags
		currChosenWindSpeed = "";
		windSpeedEstimated = true;
		windSpeedMeasured = false;

		//Reset inner graphics to primal state
		windSpeedMeasuredOrEstimatedTextView.setVisibility(View.GONE);
		windSpeedEstimatedRadioButton.setVisibility(View.GONE);
		windSpeedMeasuredRadioButton.setVisibility(View.GONE);
		windSpeedTextView.setText(R.string.genericWindSpeedText);
		addWindSpeed.setText(R.string.add);
		addWindSpeed.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				addWindSpeed(v);
			}
		});
		windSpeedTextView.setVisibility(View.VISIBLE);
		addWindSpeed.setVisibility(View.VISIBLE);
	}

	public void addWindSpeed(View v){
		final View currView = v;
		Resources res = getResources();
		final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.windSpeedDetail);

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity_new.this);
		builder.setTitle("What wind speed are you reporting?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				currChosenWindSpeed = (String) items[item]; //add selected weather report
				displayWindSpeed(currView);
			}
		});
		windSpeedDialog = builder.create();
		windSpeedDialog.show();
	}

	public void displayWindSpeed(View v){
		//dismiss the dialog
		windSpeedDialog.dismiss();

		//display the measurement type radio buttons
		windSpeedMeasuredOrEstimatedTextView.setVisibility(View.VISIBLE);
		windSpeedEstimatedRadioButton.setChecked(true);
		windSpeedMeasuredRadioButton.setChecked(false);
		windSpeedEstimatedRadioButton.setVisibility(View.VISIBLE);
		windSpeedMeasuredRadioButton.setVisibility(View.VISIBLE);
		windSpeedEstimated = true;
		windSpeedMeasured = false;


		//Update the graphics
		windSpeedTextView.setText(currChosenWindSpeed);
		addWindSpeed.setText(R.string.erase);
		addWindSpeed.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				removeWindSpeed(v);
			}
		});

	}

	public void removeWindSpeed(View v){
		//Reset the primitive wind speed string variable
		currChosenWindSpeed = "";
		//reset the radio buttons
		windSpeedMeasuredOrEstimatedTextView.setVisibility(View.GONE);
		windSpeedEstimatedRadioButton.setChecked(true);
		windSpeedMeasuredRadioButton.setChecked(false);
		windSpeedEstimatedRadioButton.setVisibility(View.GONE);
		windSpeedMeasuredRadioButton.setVisibility(View.GONE);
		windSpeedEstimated = true;
		windSpeedMeasured = false;

		//Update the graphics
		windSpeedTextView.setText(R.string.genericWindSpeedText);
		addWindSpeed.setText(R.string.add);
		addWindSpeed.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addWindSpeed(v);
			}
		});
	}
	public void windEstimated(View v){
		RadioButton rb = (RadioButton) v;
		if (rb.isChecked()){
			//Set the boolean flags
			windSpeedEstimated = true;
			windSpeedMeasured = false;
			//Make sure the wind speed measured radio button isnt showing it is checked as well
			windSpeedMeasuredRadioButton.setChecked(false);
		}else{
		}
	}

	public void windMeasured(View v){
		RadioButton rb = (RadioButton) v;
		if (rb.isChecked()){
			//Set the boolean flags
			windSpeedMeasured = true;
			windSpeedEstimated = false;
			//Make sure the wind speed measured radio button isnt showing it is checked as well
			windSpeedEstimatedRadioButton.setChecked(false);
		}else{
		}

	}



	/*************************************************************************************************
	 * DAMAGE/INJURY/FATALITY DETAIL METHODS
	 *************************************************************************************************/
	public void damageAndInjuryDetailLayoutSetup(){
		damageAndInjuries = (View) findViewById(R.id.damageAndInjuriesBlock);
		damageCheckBox = (CheckBox) damageAndInjuries.findViewById(R.id.damageCheckBox);
		injuriesCheckBox = (CheckBox) damageAndInjuries.findViewById(R.id.injuryCheckBox);
		fatalitiesCheckBox = (CheckBox) damageAndInjuries.findViewById(R.id.fatalityCheckBox);

		//Hide the layout and initialize all values to blanks
		eraseAllDamageAndInjuryDetails();
	}

	public void eraseAllDamageAndInjuryDetails(){
		//Hide the whole block
		damageAndInjuries.setVisibility(View.GONE);

		//Reset the check boxes to show unchecked
		damageCheckBox.setChecked(false);
		injuriesCheckBox.setChecked(false);
		fatalitiesCheckBox.setChecked(false);

		//reset the primitive boolean flags for each check box
		DAMAGE = false;
		INJURIES = false;
		FATALITIES = false;
	}

	public void damageCheck(View v){
		if (damageCheckBox.isChecked()){
			DAMAGE = true;
		}else{
			DAMAGE = false;
		}
	}

	public void injuryCheck(View v){
		if (injuriesCheckBox.isChecked()){
			INJURIES = true;
		}else{
			INJURIES = false;
		}
	}

	public void fatalityCheck(View v){
		if (fatalitiesCheckBox.isChecked()){
			FATALITIES = true;
		}else{
			FATALITIES = false;
		}
	}


	/*************************************************************************************************
	 * OTHER DETAILS BLOCK METHODS
	 *************************************************************************************************/
	public void otherDetailsLayoutSetup(){
		otherDetails = (View) findViewById(R.id.otherDetailsBlock);
		otherDetailsTextBox = (EditText) otherDetails.findViewById(R.id.otherDetailsTextBox);
		otherDetailsTextView = (TextView) otherDetails.findViewById(R.id.otherDetailsTextView);
		otherDetailsButton = (Button) otherDetails.findViewById(R.id.buttonAddOtherDetails);
		editOtherDetailsButton = (Button) otherDetails.findViewById(R.id.buttonEditOtherDetails);

		//Hide the layout and initialize all values to blanks
		eraseAllOtherDetails();
	}

	public void eraseAllOtherDetails(){
		//Remove the other details graphic from the form
		otherDetails.setVisibility(View.GONE);

		//Reset the graphics to their primal state (remember you are initializing as well
		//so need the onclicklisteners set up
		otherDetailsTextBox.setText("");
		otherDetailsTextBox.setHint(R.string.otherDetailsHint);
		otherDetailsTextView.setText("");
		otherDetailsButton.setText(R.string.saveLocationButton);
		otherDetailsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				saveOtherDetails(arg0);
			}
		});
		editOtherDetailsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				editOtherDetails(v);
			}
		});

		//Reset the graphics to their primal dispaly
		otherDetailsTextBox.setVisibility(View.VISIBLE);
		otherDetailsButton.setVisibility(View.VISIBLE);
		otherDetailsTextView.setVisibility(View.GONE);
		editOtherDetailsButton.setVisibility(View.GONE);

		//Reset the primitive other details variable
		currOtherDetails = "";
	}

	public void saveOtherDetails(View v){
		//Save the edit text box's text to the primitive string variable to store the other details
		currOtherDetails = String.valueOf(otherDetailsTextBox.getText());

		//Update the text view to show the inputed text
		otherDetailsTextView.setText(currOtherDetails);

		//Set up the graphics appropriately
		otherDetailsButton.setText("Remove");
		otherDetailsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				removeOtherDetails(v);	
			}

		});
		editOtherDetailsButton.setVisibility(View.VISIBLE);
		otherDetailsTextBox.setVisibility(View.GONE);
		otherDetailsTextView.setVisibility(View.VISIBLE);
	}

	public void editOtherDetails(View v){
		//Setup the appropriate graphics
		otherDetailsTextBox.setText(currOtherDetails);
		otherDetailsTextView.setText("");
		otherDetailsButton.setText(R.string.saveLocationButton);
		otherDetailsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				saveOtherDetails(arg0);
			}
		});
		editOtherDetailsButton.setVisibility(View.GONE);
		otherDetailsTextView.setVisibility(View.GONE);
		otherDetailsTextBox.setVisibility(View.VISIBLE);

		//Reset the primitive variable that stores the current other details
		currOtherDetails = "";

	}

	public void removeOtherDetails(View v){
		//Reset the graphics to their primal state (remember you are initializing as well
		//so need the onclicklisteners set up
		otherDetailsTextBox.setText("");
		otherDetailsTextBox.setHint(R.string.otherDetailsHint);
		otherDetailsTextView.setText("");
		otherDetailsButton.setText(R.string.saveLocationButton);
		otherDetailsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				saveOtherDetails(arg0);
			}
		});
		editOtherDetailsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				editOtherDetails(v);
			}
		});

		//Reset the graphics to their primal dispaly
		otherDetailsTextBox.setVisibility(View.VISIBLE);
		otherDetailsTextView.setVisibility(View.GONE);
		editOtherDetailsButton.setVisibility(View.GONE);

		//Reset the primitive other details variable
		currOtherDetails = "";
	}

	/*************************************************************************************************
	 * SIBMIT AND SAVE BLOCK METHODS
	 *************************************************************************************************/
	public void submitAndSaveLayoutSetup(){
		submitAndSaveBlock = (View) findViewById(R.id.submitAndSaveBlock);
		submitButton = (Button) submitAndSaveBlock.findViewById(R.id.submitReportButton);

		//Hide the layout and initialize all values to blanks
		eraseSubmitAndSaveGraphics();
	}

	public void eraseSubmitAndSaveGraphics(){
		submitAndSaveBlock.setVisibility(View.GONE);
	}

	public void submitReport(View v){
		//Toast.makeText(this, "Yay, you submitted the report...NOT", 1000).show();
		/*numberOfWeatherTypes;
		weatherTypes;
		otherWeatherTypeText;
		currTornadoLocation;
		currTornadoHeading;
		tornadoAcrossCountyLine;
		funnelCloudLocation;
		funnelCloudHeading;
		funnelCloudAcrossCountyLine;
		wallCloudLocation;
		wallCloudHeading;
		wallCloudAcrossCountyLine;
		numberOfHailSizes;
		hailSizes;
		hailSizeMeasured;
		hailSizeEstimated;
		currChosenWindSpeed;
		windSpeedEstimated;
		windSpeedMeasured;
		DAMAGE;
		INJURIES;
		FATALITIES;
		currOtherDetails;*/

		ArrayList<String> finalWeatherTypes = new ArrayList<String>();
		for (int i = 0; i < weatherTypes.size(); i++){
			finalWeatherTypes.add(weatherTypes.get(i));
		}
		//Start building the report, starting with weather types;
		int tornado = weatherTypes.indexOf("Tornado");
		int fc = weatherTypes.indexOf("Funnel Cloud");
		int wc = weatherTypes.indexOf("Wall Cloud");
		int hail = weatherTypes.indexOf("Hail");
		int hw = weatherTypes.indexOf("High Wind");
		int other = weatherTypes.indexOf("Other");

		if(tornado != -1){
			report.setTornadoDetails(this.currTornadoLocation, this.currTornadoHeading, this.tornadoAcrossCountyLine);
		}
		if(fc != -1){
			report.setTornadoDetails(this.currFunnelCloudLocation, this.currFunnelCloudHeading, this.funnelCloudAcrossCountyLine);
		}
		if(wc != -1){
			report.setWallCloudDetails(this.currWallCloudLocation, this.currWallCloudHeading, this.wallCloudAcrossCountyLine, this.wallCloudRotating);
		}
		if(hail != -1){
			report.setHailDetails(this.hailSizes, this.hailSizeMeasured);
		}
		if(hw != -1){
			report.setWindSpeedDetails(this.currChosenWindSpeed, this.windSpeedMeasured);
		}
		if(other != -1){
			finalWeatherTypes.set(other, otherWeatherTypeText);
		}
		if(currOtherDetails.length() != 0){
			report.setOtherDetails(this.currOtherDetails);
		}

		String dateAndTime = (String) timeTextView.getText();
		dateAndTime = dateAndTime.substring(dateAndTime.indexOf(":")+2);
		String location = (String) locationTextView.getText();
		location = location.substring(location.indexOf(":")+2);
		report.setDateAndTime(dateAndTime);
		report.setLocation(location);
		report.setWeatherTypes(finalWeatherTypes);
		report.setDamage(this.DAMAGE);
		report.setInjuries(this.INJURIES);
		report.setFatalities(this.FATALITIES);
		Log.i("Test location before sending", USER_STATE_LOC + " " + USER_COUNTY_LOC);
		if(report.addStationRecipient(USER_STATE_LOC, USER_COUNTY_LOC)){
			//Call the test report view activity
			Intent i = new Intent(this, TestActivity.class);
			i.putExtra("body", report.compile());
			i.putExtra("data", report.getStationData());
			if (photoToSend != null){
				i.putExtra("image", photoToSend);
			}
			startActivity(i);
		}else{
			Toast.makeText(this, "No NWS station data found for your location", 1000).show();
		}



	}


	public void clearAllReportData(){
		Log.e("In clear all report data","check");
		eraseAllTornadoDetails();
		eraseAllFunnelCloudDetails();
		eraseAllWallCloudDetails();
		eraseAllHailDetails();
		eraseAllHighWindDetails();
		this.eraseSubmitAndSaveGraphics();
		this.eraseAllOtherDetails();
		for(int i = 0; i < weatherTypes.size(); i ++){
			weatherTypes.remove(i);
		}
		report = new StormReport(this);
	}


	/**
	 * 
	 * This method is used to warn the user of backing out of the application since the main activity
	 * is a swiping list of views
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		//Handle the back button
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			//Ask the user if they want to quit
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Return to main screen?")
			.setMessage("Any progress will be lost...")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//Stop the activity
					finish();    
				}
			})
			.setNegativeButton("No", null)
			.show();

			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
