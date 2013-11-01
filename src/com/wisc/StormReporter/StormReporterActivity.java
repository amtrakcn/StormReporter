package com.wisc.StormReporter;


import java.io.BufferedReader;
import java.io.DataInputStream;

import java.io.InputStreamReader;



import static com.wisc.StormReporter.Constants.TABLE_NAME;
import static com.wisc.StormReporter.Constants.LOCATIONS_TABLE_NAME;
import static com.wisc.StormReporter.Constants.STATE;
import static com.wisc.StormReporter.Constants.COUNTY;
import static com.wisc.StormReporter.Constants.NAME;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StormReporterActivity extends Activity {
	private static final int DIALOG_NEW_USER = 0;
	private static final int DIALOG_SEMI_NEW_USER = 1;
	//private static final String DB_PATH = "/data/data/com.wisc.StormReporter/databases/stations.db";

	private StationData stations;
	private LocationData locations;

	SharedPreferences prefs;
	Editor prefsEditor;
	MyPagerAdapter adapter;
	ViewPager myPager;

	String password = "";
	String USERPW = "";

	int requestCode;

	boolean correctPassword = false;
	boolean newUser = false;
	private boolean permLogin = false;
	private boolean tempPermLogin = false;
	private boolean cameFromReportActivity = false;

	Button clickButtonOne;
	Button clickButtonTwo;

	Vibrator vib;

	//Initialization for database progress dialog
	ProgressThread progThread;
	ProgressDialog progDialog;
	int delay = 40;                  // Milliseconds of delay in the update loop
	int barValue = 0;           // initial progress bar value;



	/*******************************************************************************************************************************/
	/*******************************				LIFE CYCLE METHODS					********************************************/
	/*******************************************************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		vib= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		/********************************************************/
		//clearMemory();   //Use this method to clean local preference and password data to be considered a new user
		/*********************************************************/
		//Log.e("onActivityResult","in onCreate");

	}


	@Override
	public void onResume(){
		super.onResume();

		//Load the main activity and its contents
		startup();


		//Evaluate whether or not the user has enough preferences set to be able to start a report
		//if not he/she is called a newUser
		if(newUser(true)){
			newUser=true;
		}
		else{
			newUser = false;
		}
		//Log.e("onActivityResult","in onResume");

		//Evaluate whether or not databases need to be created for station data and location data.
		//Need to do this after checking if the user is new so that the progress dialog shows
		//up in front (in the UI) of the new user dialog
		checkDatabases();

		//		 BELOW IS OLD TEST CODE TO EVALUATE THE ELEMENTS OF THE LOCATIONS DATABASE
		/*try{
				Log.e("IN TESTING LOCATIONS","SHOULD BE WORKING");
				String[] choose = { STATE, COUNTY, NAME };
				String ORDER_BY = STATE + " ASC, "+ COUNTY + " ASC";
				Cursor cur = locations.getStationFromLocation(choose, null, null, null, ORDER_BY);
				while(cur.moveToNext()){
					Log.e("Locs Output Test: ",cur.getString(0) + ", " + cur.getString(1) + ", " + cur.getString(2));
				}
				cur.close();
			} catch (Exception e) {
				Log.i("Locations database output test", "Error");

			}*/
	}


	@Override
	protected void onPause() {
		//To conserve memory, unload all drawables and states when leaving the activity
		onDestroy();
		unbindDrawables(findViewById(R.id.title_parent));
		System.gc();
	}

	@Override
	public void onDestroy(){
		//Close the databases and threads when leaving
		stations.close();
		locations.close();
		vib.cancel();

		try{
			if(progThread.isAlive()){
				try{

					progThread.stop(new Throwable());
				}catch (Exception e){
					Log.e("Error Stopping makeDatabases thread in main-onDestroy()","error");
				}
			}
		}catch(Exception e){
			//Do nothing, progThread doesn't exist so don't need to stop it
		}
		
		//call super.onDestroy()
		super.onDestroy();
	}

	/**
	 * This method is responsible for setting up the state of the main activity when opened up (called from onResume() )
	 */
	public void startup(){
		//Log.e("onActivityResult","in startup");
		correctPassword = false;
		password = "";
		USERPW = "";
		permLogin = prefs.getBoolean("permLogin", false);


		adapter = new MyPagerAdapter();
		myPager = (ViewPager) findViewById(R.id.pager);
		myPager.setAdapter(adapter);
		myPager.setOnPageChangeListener(new OnPageChangeListener(){


			public void onPageSelected(int arg0) {
				if (arg0 ==2){
					if (correctPassword || permLogin){
						if (!newUser){
							allowReport();
						}
						else{
							resetPassword();
						}
					}
					else{
						resetPassword();
					}
				}
			}

			public void onPageScrollStateChanged(int arg0) {

			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}
		});

		//Set up the checkBox for permanaent login
		/*CheckBox repeatChkBx = ( CheckBox ) findViewById( R.id.perm_login);
		repeatChkBx.setOnCheckedChangeListener(new OnCheckedChangeListener(){
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if ( isChecked )
		        {
		        	if(!newUser){
		    			prefs.edit().putBoolean("permLogin", true).commit();
		    			findViewById(R.id.perm_login).setVisibility(View.GONE);
		    			findViewById(R.id.perm_login_text).setVisibility(View.GONE);
		    		}
		    		else{
		    			findViewById(R.id.perm_login).setVisibility(View.GONE);
		    			findViewById(R.id.perm_login).setVisibility(View.VISIBLE);
		    			Toast.makeText(getBaseContext(), "Need to set appropriate settings and preferences first", 1000).show();
		    		}
		        }

		    }
		});*/

		//Evaluate whether or not the user should start at the title view or successful login view
		if(!cameFromReportActivity){
			myPager.setCurrentItem(1);
		}
		else{
			//Set the viewpager to show the successful login view
			myPager.setCurrentItem(2);
			//Already successfully logged in, so don't make user do it again
			correctPassword=true;
			allowReport();
			//Need to change the cameFromReportActivity back to false otherwise will always be true
			cameFromReportActivity = false;
		}
		//onViewChange(myPager.getCurrentItem());
	}

	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/






	/*******************************************************************************************************************************/
	/*******************************				USER DATA METHODS					********************************************/
	/*******************************************************************************************************************************/	


	/**
	 * Method newUser: Evaluates the existence and values of required user preferences
	 * 1.) Username
	 * 2.) Password
	 * 3.) Home NWS Station
	 * 
	 * Based on the status of these three preference "checks" determines if the app should
	 * view the current user as new or not
	 * 
	 * @param showUser: If true, display appropriate AlertDialogs indcating
	 * to the user that they do not have certain required preferecnes set. If all
	 * required preferences are unset, display a welcome alert dialog since they are
	 * most likely first time users.
	 * @return TRUE if the user has any required preferences unset
	 * FALSE if all of the required preferences are set
	 */
	private boolean newUser(boolean showUser){
		//Call the checkPreferences method to return appropriate
		//preference check values
		boolean[] checks = checkPreferences(showUser);
		if (checks[0] && checks[1] && checks[2]){
			return false;
		}else{
			return true;
		}
	}


	private boolean[] checkPreferences(boolean showUser){
		boolean pwcheck = false;
		boolean uncheck = false;
		boolean homestationcheck = false;
		String pw = "";
		String un = "";
		String hs = "";
		boolean[] checks = new boolean[3];

		try{
			//Use the fileList() method to determine if there is any saved files because if none, new user
			if (fileList().length == 0){
				//If the user is new, activate the new user flag
				checks[0] = pwcheck;
			}

			//First check to see if there is a correct password
			try {
				DataInputStream in = 
						new DataInputStream(openFileInput("password"));
				pw=in.readUTF();
				in.close();

				//If there is in fact a password and it has an appropriate length of
				//4 characters, set the USERPW variable to the read password
				//Otherwise, do nothing and the check values will remain set to false
				if (pw.length() == 4){
					pwcheck = true;
					USERPW = pw;
				}
			} catch (Exception e) {
				Log.i("(Main) PW input check", "I/O Error or Password File Not Found");
			}

			//Set the first element of the checks array to the value of the password check
			checks[0] = pwcheck;



			//Now check to see if there is a username and home station set
			try{
				un = prefs.getString("username", null);
				//Log.e("test un",String.valueOf(un));
				if(un != null){
					if(un.length() != 0){
						uncheck = true; 
					}
				}
			}catch(Exception e){
				Log.i("Checking username preference","failed");
			}

			//Set the second element of the checks array to the value of the username check
			checks[1] = uncheck;

			//check homestation
			try{
				hs = prefs.getString("home_station", null);
				//Log.e("test hs",String.valueOf(hs));
				if(hs != null){
					homestationcheck = true;
				}
			}catch(Exception e){
				Log.i("Checking home station preference","failed");
			}

			//Set the third element of the checks array to the value of the home station check
			checks[2] = homestationcheck;


		}catch (Exception e){
			Log.e("loading preferences","error loading preferences on creation of main activity");
			//If there was some error checking the preferences (file not found, etc.) still
			//set the checks array to current values to continue app running.
			checks[0] = pwcheck;
			checks[1] = uncheck;
			checks[2] = homestationcheck;
		}

		//If the method was called such that the user should be indicated of unset preferences, 
		//call the onCreateDialog method to display the appropriate message
		if (showUser){
			Log.i("pwcheck",String.valueOf(pwcheck));
			Log.i("uncheck",String.valueOf(uncheck));
			Log.i("hscheck",String.valueOf(homestationcheck));

			//If all checks prove false, the user is most likely new so display a welcome message
			//(The type of alert dialog message displayed depends on the first onCreateDialog parameter,
			//see the onCreateDialog method for more details)
			if(!pwcheck && !uncheck && !homestationcheck){
				AlertDialog d = onCreateDialog(DIALOG_NEW_USER, pwcheck, uncheck, homestationcheck);
				d.show();
			}

			//If a user has set some, but not all, of the required parameters, alert them of the specific
			//preferences that need to be set.
			else if(!pwcheck || !uncheck || !homestationcheck){
				AlertDialog d = onCreateDialog(DIALOG_SEMI_NEW_USER, pwcheck, uncheck, homestationcheck);
				d.show();
			}
		}

		//Return the checks array
		return checks;
	}

	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/








	/*******************************************************************************************************************************/
	/*******************************			(MAIN ACTIVITY) ALL-VIEW METHODS		********************************************/
	/*******************************************************************************************************************************/	


	public void titleLeftClick(View v){
		myPager.setCurrentItem(0);
	}
	public void titleRightClick(View v){
		myPager.setCurrentItem(2);
	}
	public void emergencyRightClick(View v){
		myPager.setCurrentItem(1);
	}
	public void loginLeftClick(View v){
		myPager.setCurrentItem(1);
	}

	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/








	/*******************************************************************************************************************************/
	/*******************************			PASSWORD VIEW METHODS					********************************************/
	/*******************************************************************************************************************************/	
	public void onFirstCodeButtonClick(View v){
		buttonChange(1,"red");

		password=password + String.valueOf(1);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onSecondCodeButtonClick(View v){
		buttonChange(2,"red");

		password=password + String.valueOf(2);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onThirdCodeButtonClick(View v){
		buttonChange(3,"red");

		password=password + String.valueOf(3);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onFourthCodeButtonClick(View v){
		buttonChange(4,"red");

		password=password + String.valueOf(4);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onFifthCodeButtonClick(View v){
		buttonChange(5,"red");
		password=password + String.valueOf(5);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onSixthCodeButtonClick(View v){
		buttonChange(6,"red");

		password=password + String.valueOf(6);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onSeventhCodeButtonClick(View v){
		buttonChange(7,"red");

		password=password + String.valueOf(7);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onEighthCodeButtonClick(View v){
		buttonChange(8,"red");

		password=password + String.valueOf(8);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onNinthCodeButtonClick(View v){
		buttonChange(9,"red");

		password=password + String.valueOf(9);
		if (password.length() == 4 ){
			passwordCheck();
		}
	}
	public void onResetPasswordClick(View v){
		resetPassword();
		//vib.vibrate(200);
	}
	private void passwordCheck(){

		if (!newUser){

			if (!password.equals(USERPW)){
				Toast.makeText(this, "Password Denied", 1000).show();
				resetPassword();
			}
			else{
				correctPassword=true;
				allowReport();
			}
			try {
				synchronized(this){
					wait(200);
				}
			}catch(InterruptedException ex){ 
			}
		}else{
			Toast.makeText(com.wisc.StormReporter.StormReporterActivity.this, "Need to set your preferences" + '\n' + "before your account is activated...", 1000).show();
			resetPassword();
		}
	}
	private void buttonChange(int i, String color){
		ImageView view;

		if (i == 1){
			view = (ImageView) findViewById(R.id.one);
		}
		else if (i == 2){
			view = (ImageView) findViewById(R.id.two);
		}
		else if (i == 3){
			view = (ImageView) findViewById(R.id.three);
		}
		else if (i == 4){
			view = (ImageView) findViewById(R.id.four);
		}
		else if (i == 5){
			view = (ImageView) findViewById(R.id.five);
		}
		else if (i == 6){
			view = (ImageView) findViewById(R.id.six);
		}
		else if (i == 7){
			view = (ImageView) findViewById(R.id.seven);
		}
		else if (i == 8){
			view = (ImageView) findViewById(R.id.eight);
		}
		else{
			view = (ImageView) findViewById(R.id.nine);
		}

		if (color.equals("green")){
			view.setImageResource(R.drawable.blue_button);
			view.setVisibility(View.VISIBLE);
			view.setClickable(true);
		}
		else if (color.equals("red")){
			vib.vibrate(70);
			view.setImageResource(R.drawable.red_button);
			view.setClickable(false);
		}
	}

	private void resetPassword(){
		for (int i=0; i<=9; i++){
			buttonChange(i,"green");
		}

		//Make sure the new report button is not visible
		findViewById(R.id.new_report_button).setVisibility(View.GONE);

		findViewById(R.id.perm_login).setVisibility(View.VISIBLE);
		findViewById(R.id.perm_login_text).setVisibility(View.VISIBLE);
		findViewById(R.id.button_table).setVisibility(View.VISIBLE);
		findViewById(R.id.reset_password).setVisibility(View.VISIBLE);

		TextView title = (TextView) findViewById(R.id.login_title);
		title.setVisibility(View.VISIBLE);
		title.setText(R.string.login);
		password="";
	}



	private void allowReport(){


		//First evaluate whether or not the permanaent log in checkbox has been clicked
		if(tempPermLogin){
			//reset tempPermLogin so that we're not opening the preferences every time allowReport is called
			//and if permLogin every is changed to false (by changing the password), don't want
			//tremPermLogin to still be true
			tempPermLogin = false;

			//Set the permLogin preference to be true
			prefs.edit().putBoolean("permLogin", true).commit();
			Toast.makeText(this,"Remember login preference set", 1000);
		}

		findViewById(R.id.perm_login).setVisibility(View.GONE);
		findViewById(R.id.perm_login_text).setVisibility(View.GONE);
		//Destroy the table of buttons
		findViewById(R.id.button_table).setVisibility(View.GONE);
		//Destroy the reset button
		findViewById(R.id.reset_password).setVisibility(View.GONE);
		//Destroy the header text view
		findViewById(R.id.login_title).setVisibility(View.GONE);
		//Set background color to title background color
		LinearLayout layout = (LinearLayout) findViewById(R.id.login_layout);
		layout.setBackgroundResource(R.color.title_background);
		//Display the logo
		findViewById(R.id.login_logo).setVisibility(View.VISIBLE);


		//Make new report title, new report button and old report button visible
		//TextView title = (TextView) findViewById(R.id.logged_in_title);
		//title.setText("Hello, " + prefs.getString("username", "New User") +"!");
		/*if(prefs.getBoolean("isTrainedSpotter", false)){
			title.setText("Hello, " + prefs.getString("username", "New User") +"!" +'\n' +"You are a trained spotter!");
		}else{
			title.setText("Hello, " + prefs.getString("username", "New User") +"!");

		}*/
		//title.setVisibility(View.VISIBLE);
		Button newReport = (Button) findViewById(R.id.new_report_button);
		newReport.setVisibility(View.VISIBLE);

		//Test the StormReport class
		newReport.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent i = new Intent(com.wisc.StormReporter.StormReporterActivity.this, ReportActivity_new.class);
				startActivity(i);
				/*Intent i = report.send("trevorrow@wisc.edu");
				try {
				    startActivity(Intent.createChooser(i, "Send report via..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(com.wisc.StormReporter.StormReporterActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}*/

			}

		});



		findViewById(R.id.old_report_button).setVisibility(View.VISIBLE);
		/*new_report.setText("Hello " + prefs.getString("username", "New User") +"!" +
				" Your reports will go to: " + email);
		new_report.setTextSize(10);
		new_report.setVisibility(View.VISIBLE);*/

	}


	public void onClickPermLogin(View v){
		CheckBox box = (CheckBox) findViewById(R.id.perm_login);
		if(box.isChecked()){
			if(!newUser){
				tempPermLogin = true;
				Toast.makeText(getBaseContext(), "Enter correct password to set preference", 1000).show();
			}
			else{
				box.setChecked(false);
				Toast.makeText(getBaseContext(), "Need to set appropriate settings and preferences first", 1000).show();
			}
		}
		else{
			tempPermLogin = false;
		}
	}
	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/








	/*******************************************************************************************************************************/
	/*******************************			EMERGENCY CONTACTS VIEW METHODS			********************************************/
	/*******************************************************************************************************************************/	
	/**
	 * This method initiates the phone's call activity using the number
	 * associated with the button pressed.
	 * @param number A string representing the phone number to call.
	 */
	private void call (String number) {
		Intent i = new Intent(Intent.ACTION_DIAL);
		String p = "tel:" + number;
		i.setData(Uri.parse(p));
		startActivity(i);
	}

	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/









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
		inflater.inflate(R.menu.menu, menu);
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

		if (id == R.id.exit_label){
			onDestroy();
			finish();
		}
		else if (id == R.id.settings){
			Intent i = new Intent(this, Prefs.class);
			i.putExtra("newUser", newUser);
			//	i.putExtra("USERPW", USERPW);
			startActivityForResult(i,requestCode);
			return true;
		}
		return false;
	}


	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/










	/*******************************************************************************************************************************/
	/*******************************			ALEART DIALOG METHODS					********************************************/
	/*******************************************************************************************************************************/		
	protected AlertDialog onCreateDialog(int id, boolean pwcheck, boolean uncheck, boolean homestationcheck) {
		AlertDialog dialog;
		AlertDialog.Builder builder = null;

		if (id == DIALOG_NEW_USER){
			// do the work to define the pause Dialog
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Welcome to StormReporter!")
			.setMessage("Please set your preferences...")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent i = new Intent(com.wisc.StormReporter.StormReporterActivity.this, Prefs.class);
					i.putExtra("newUser", true);
					//i.putExtra("USERPW", USERPW);
					// preferencesChanged = true;
					startActivityForResult(i,requestCode);
					dialog.dismiss();
				}
			})
			.setNegativeButton("Later", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//com.wisc.StormReporter.StormReporterActivity.this.finish();
					dialog.dismiss();
				}
			});
			dialog = builder.create();
		}
		else if( id == DIALOG_SEMI_NEW_USER){
			String pw = "";
			String un = "";
			String hs = "";
			if(!pwcheck){
				pw = "Password";
			}
			if(!uncheck){
				un="Username";
			}
			if(!homestationcheck){
				hs="Home Station";
			}
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Your Account Is Not Activated")
			.setMessage("Need to set: " + '\n'  + '\n' + pw + '\n' + un + '\n' + hs + '\n' + '\n' 
					+ "Before you can begin sending storm reports...")
					.setCancelable(false)
					.setPositiveButton("Set Preferences", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent i = new Intent(com.wisc.StormReporter.StormReporterActivity.this, Prefs.class);
							//Can say the user is new since the only time this alert dialog will be created is if
							//the user hasn't set all of the required preferences yet
							i.putExtra("newUser", true);
							//i.putExtra("USERPW", USERPW);
							startActivityForResult(i,requestCode);
							dialog.dismiss();
						}
					})
					.setNegativeButton("Later", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//com.wisc.StormReporter.StormReporterActivity.this.finish();
							dialog.dismiss();
						}
					});
			dialog = builder.create();
		}
		else{
			dialog = null;
		}
		return dialog;
	}

	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/









	/*******************************************************************************************************************************/
	/*******************************			VIEW PAGER METHODS						********************************************/
	/*******************************************************************************************************************************/			
	private class MyPagerAdapter extends PagerAdapter {

		public int getCount() {
			return 3;
		}

		public Object instantiateItem(View collection, int position) {

			LayoutInflater inflater = (LayoutInflater) collection.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			int resId = 0;

			switch (position) {
			case 0:
				resId = R.layout.emergency;
				break;
			case 1:
				resId = R.layout.title;
				break;
			case 2:
				resId = R.layout.login;
				break;
			}

			View view = inflater.inflate(resId, null);

			((ViewPager) collection).addView(view, 0);

			/*
			 * These buttons are initiated when the view is changed to the 
			 * Emergency Contact Information page because they require
			 * the layout to be set to R.layout.emergency. ButtonOne represents
			 * the number for 911 and ButtonTwo holds the local NWS Station
			 */
			clickButtonOne = (Button) findViewById(R.id.phoneOneButton);
			clickButtonTwo = (Button) findViewById(R.id.phoneTwoButton);

			/*
			 * When either button is pressed, it passes its associated telephone
			 * number to the call function to initiate a phone call.
			 */
			clickButtonOne.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String number = getString(R.string.phoneOneNum);
					call(number);
				}
			});
			clickButtonTwo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String number = getString(R.string.phoneTwoNum);
					call(number);
				}
			});

			return view;
		}


		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

	}

	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/










	/*******************************************************************************************************************************/
	/*******************************			OTHER NEEDED METHODS					********************************************/
	/*******************************************************************************************************************************/	
	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}

		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			try{
				((ViewGroup) view).removeAllViews();
			}catch(Exception e){
				Log.e("clean-up","problem with cleanup");
			}
		}
	}

	/**
	 * This method is used to warn the user of backing out of the application since the main activity
	 * is a swiping list of views
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Handle the back button
		if(keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
			//Ask the user if they want to quit
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("         Exit?")
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

	/**
	 * This method evaluates the result of the activity called by this activity to evaluate whether or
	 * not the user wants to quit this app or start a new search
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//Only need to worry about if the user changed preferences---NOT TRUE ANYMORE
		/*if (requestCode == this.requestCode){
			if (resultCode == 10) {
				cameFromPreferences = true;
			}
		}*/
		if(requestCode == this.requestCode){
			if(resultCode == 999){
				Log.e("onActivityResult","came from test activity");
				cameFromReportActivity = true;
			}
			else{
				//Log.e("onActivityResult", "didn't work...resultCode=" + String.valueOf(resultCode));
			}
		}
	}


	private void makeStationDatabase(){
		stations = new StationData(this);
		Log.e("MakeNewStationDatabase","called");

		try{
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.stations)));

			String delim ="[|]";
			String[] line;
			String curr = "";

			String id = "";
			String name = "";
			String state = "";
			String email = "";
			String phone = "";
			String pw = "";
			boolean done = false;

			while (!done && reader.ready()) { 

				curr = reader.readLine();
				line = curr.split(delim);
				if(!curr.contains("!end!")){
					if(line.length == 6){

						id = line[0];
						name=line[1];
						state=line[2];
						email=line[3];
						phone=line[4];
						pw=line[5];

						stations.addStation(id, name, state, email, phone, pw);
					}
					else{
						Log.e("Error loading station data from raw file","improperly formatted station");
					}

				}
				else{
					//Log.e("TEST END", "HERE...: "+String.valueOf(testnum));
					done=true;
				}
			}

			reader.close();
		}catch (Exception e){
			e.printStackTrace();
			Log.e("Station Database Error", "Error parsing stations file into database");
		}

	}

	private void makeLocationDatabase(){

		locations = new LocationData(this);
		
		progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progDialog.setMax(100);
		progDialog.setTitle("Initializing Storm Reporter" + '\n' + "DO NOT CLOSE!");
		progDialog.setMessage("Building databases..." + '\n' + "(this may take a minute)");
		progDialog.show();
		progThread = new ProgressThread(handler);
		progThread.start();
	
	}

	// Handler on the main (UI) thread that will receive messages from the 
	// second thread and update the progress.

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// Get the current value of the variable total from the message data
			// and update the progress bar.
			int total = msg.getData().getInt("total");
			progDialog.setProgress(total);
			if (total >= 100){
				progDialog.dismiss();
				progThread.setState(ProgressThread.DONE);

			}
		}
	};
	// Inner class that performs progress calculations on a second thread.  Implement
	// the thread by subclassing Thread and overriding its run() method.  Also provide
	// a setState(state) method to stop the thread gracefully.
	private class ProgressThread extends Thread {	

		// Class constants defining state of the thread
		final static int DONE = 0;
		final static int RUNNING = 1;

		Handler mHandler;
		int mState;
		int total;

		// Constructor with an argument that specifies Handler on main thread
		// to which messages will be sent by this thread.

		ProgressThread(Handler h) {
			mHandler = h;
		}

		// Override the run() method that will be invoked automatically when 
		// the Thread starts.  Do the work required to update the progress bar on this
		// thread but send a message to the Handler on the main UI thread to actually
		// change the visual representation of the progress. In this example we count
		// the index total down to zero, so the horizontal progress bar will start full and
		// count down.

		@Override
		public void run() {
			mState = RUNNING;   
			total = 0;
			while (mState == RUNNING && total < 100) {
				// The method Thread.sleep throws an InterruptedException if Thread.interrupt() 
				// were to be issued while thread is sleeping; the exception must be caught.
				try {
					// Control speed of update (but precision of delay not guaranteed)
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					Log.e("ERROR", "Thread was Interrupted");
				}

					//Process the database///////////////////////
					BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.locations)));
					String name = "";
					String state = "";
					String county="";
					String delim ="[|]";
					String[] line;
					String curr = "";

					boolean done = false;

					try{
						while (!done && reader.ready()) { 

							//Get next line
							curr = reader.readLine();
							if(!curr.contains("!end!") && !curr.contains("break")){
								line = curr.split(delim);

								if(line.length == 11){
									name = "K" + line[2];
									state = line[0];
									county = line[5];

									//Add the data to the locations database
									locations.addLocation(name, state, county);
								}

							}
							else if(curr.contains("!end!")){
								//Log.e("TEST END", "HERE...: "+String.valueOf(testnum));
								done=true;
							}
							else{
								//If reach this statement, we are at a point to update the progress bar another 10 percent

								total=total+10;    // Count up...10 indicates percent so there are ten "breaks in the database corresponding to times
								// when the progress bar should show progress in the dialog
								
								// Send message (with current value of  total as data) to Handler on UI thread
								// so that it can update the progress bar.
								Message msg = mHandler.obtainMessage();
								Bundle b = new Bundle();
								b.putInt("total", total);
								msg.setData(b);
								mHandler.sendMessage(msg);

							}
						}

						reader.close();
					}catch (Exception e){
						e.printStackTrace();
						Log.e("Location Database Error in main", "Error parsing locations file into database");
					}
				}
		}

		// Set current state of thread (use state=ProgressThread.DONE to stop thread)
		public void setState(int state) {
			mState = state;
		}
	}


	private void checkDatabases(){

		//Evaluate whether or not a stations database needs to be created,
		//or locations database needs to be created.
		boolean stationDataExists = this.getDatabasePath(TABLE_NAME+".db").exists();
		boolean locationDataExists = this.getDatabasePath(LOCATIONS_TABLE_NAME+".db").exists();
		//Log.e("Check Databases check:","station: " + stationDataExists + " location: " + locationDataExists);
		
		//check stations database
		if (!stationDataExists){
			makeStationDatabase();
		}
		else{
			stations = new StationData(this);
		}
		
		//Check locations database
		if(!locationDataExists){
			makeLocationDatabase();
		}
		else{
			locations = new LocationData(this);
		}
		
	}

	private void clearMemory(){
		String[] currFiles;
		prefsEditor = prefs.edit();
		prefsEditor.clear();
		prefsEditor.commit();

		//Evaluate whether or not there is a password file to determine if this is a new user
		try{
			/*File dir = getFilesDir();
			File[] dirs = dir.listFiles();
			Log.i("dirs length",StrinakeStationDatabase();
			g.valueOf(dirs.length));

			for (int i =0; i < dirs.length; i++){
				Log.i("listing dirs,",dirs[i].getName());
			}*/

			currFiles = fileList();
			for (int i = 0; i < currFiles.length; i++){
				deleteFile(currFiles[i]); //Use this method to delete locally saved files
			}
		}catch(Exception e){
			Log.e("(Main)","issue getting original files");

		}
	}



}