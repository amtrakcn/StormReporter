package com.wisc.StormReporter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

public class passwordActivity extends Activity {
	boolean PASSWORDALREADYSET = false;
	boolean oldPW = false;;
	boolean newPW = false;
	boolean repeatNewPW = false;
	boolean correctPassword = false;
	String oldPassword, newPassword, repeatNewPassword;
	String USERPW;
	FileInputStream OLDPW;
	FileOutputStream NEWPW;
	InputStreamReader reader;
	
	Vibrator vib;

	/**
	 * This method is called on initial creation of this activity.
	 * It handles and displays the passed data from the RSS feed
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);
		vib= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		//Use a try/catch block to avoid any crash when trying to retrieve data from the parent
		//activity
		try {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				//USERPW = extras.getString("USERPW");
			}
		} catch (Exception e) {
			Log.e("IntentCatch","Can't input bundle from intent");

		}

		//Check to see what the current password looks like
		try {
			DataInputStream in = 
					new DataInputStream(openFileInput("password"));
			String pw=in.readUTF();
			in.close();

			if (pw.length() == 4){
				USERPW = pw;
			}
			else{
				USERPW = "";
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Log.i("(passwordActivity) PW input check", "I/O Error or Password File Not Found");
			USERPW = "";
		}
		
		if (USERPW.length() == 0){
			//Don't have old password so need to make sure the user isn't asked for it.
			newPW = true;
			TextView title = (TextView) findViewById(R.id.make_pw_title);
			title.setText(R.string.new_pw);
			
		}else{
			/*//Get old PW from locally saved preferences file.
			try{
				// Read them back
			    DataInputStream in = new DataInputStream(openFileInput("password"));
			    try {
			         USERPW = in.readUTF();
			    } catch (EOFException e) {
			        Log.i("Data Input Sample", "End of file reached");
			    }
			    in.close();
				//USERPW = "7936"; // Need to set this equal to the password from the preferences file.
			}catch(Exception e){
				Log.e("local password file","can't get pw file in passwordActivity");
			}*/

			oldPW = true;
		}

		oldPassword = "";
		newPassword = "";
		repeatNewPassword = "";

	}

	public void onFirstCodeButtonClick(View v){

		buttonChange(1,"red");
		
		if (oldPW){
			oldPassword = oldPassword + String.valueOf(1);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(1);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(1);
		}

		passwordCheck();
	}
	public void onSecondCodeButtonClick(View v){
		
		buttonChange(2,"red");
		
		if (oldPW){
			oldPassword = oldPassword + String.valueOf(2);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(2);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(2);
		}

		passwordCheck();
	}
	public void onThirdCodeButtonClick(View v){
		
		buttonChange(3,"red");

		if (oldPW){
			oldPassword = oldPassword + String.valueOf(3);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(3);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(3);
		}

		passwordCheck();
	}
	public void onFourthCodeButtonClick(View v){
		buttonChange(4,"red");

		if (oldPW){
			oldPassword = oldPassword + String.valueOf(4);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(4);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(4);
		}

		passwordCheck();
	}
	public void onFifthCodeButtonClick(View v){
		buttonChange(5, "red");

		if (oldPW){
			oldPassword = oldPassword + String.valueOf(5);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(5);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(5);
		}

		passwordCheck();
	}
	public void onSixthCodeButtonClick(View v){
		buttonChange(6,"red");

		if (oldPW){
			oldPassword = oldPassword + String.valueOf(6);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(6);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(6);
		}

		passwordCheck();
	}
	public void onSeventhCodeButtonClick(View v){
		buttonChange(7,"red");

		if (oldPW){
			oldPassword = oldPassword + String.valueOf(7);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(7);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(7);
		}

		passwordCheck();
	}
	public void onEighthCodeButtonClick(View v){
		buttonChange(8,"red");

		if (oldPW){
			oldPassword = oldPassword + String.valueOf(8);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(8);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(8);
		}

		passwordCheck();
	}
	public void onNinthCodeButtonClick(View v){
		buttonChange(9,"red");

		if (oldPW){
			oldPassword = oldPassword + String.valueOf(9);
		}
		else if (newPW){
			newPassword = newPassword + String.valueOf(9);
		}
		else if (repeatNewPW){
			repeatNewPassword = repeatNewPassword + String.valueOf(9);
		}

		passwordCheck();
	}

	public void onResetPasswordClick(View v){
		resetPassword();
		//vib.vibrate(200);

	}
	
	private void buttonChange(int i, String color){
		ImageView view;

		if (i == 1){
			view = (ImageView) findViewById(R.id.pref_one);
		}
		else if (i == 2){
			view = (ImageView) findViewById(R.id.pref_two);
		}
		else if (i == 3){
			view = (ImageView) findViewById(R.id.pref_three);
		}
		else if (i == 4){
			view = (ImageView) findViewById(R.id.pref_four);
		}
		else if (i == 5){
			view = (ImageView) findViewById(R.id.pref_five);
		}
		else if (i == 6){
			view = (ImageView) findViewById(R.id.pref_six);
		}
		else if (i == 7){
			view = (ImageView) findViewById(R.id.pref_seven);
		}
		else if (i == 8){
			view = (ImageView) findViewById(R.id.pref_eight);
		}
		else{
			view = (ImageView) findViewById(R.id.pref_nine);
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
		else if (color.equals("invisible")){
			view.setVisibility(View.GONE);
			view.setClickable(false);
		}
	}

	private void passwordCheck(){
		if (oldPW){
			if (oldPassword.length() == 4){

				if (!oldPassword.equals(USERPW)){
					Toast.makeText(this, "Incorrect Password. Please Try Again", 1000).show();
					resetPassword();
				}
				else{
					correctPassword=true;
					oldPW = false;
					newPW = true;
					resetPassword();
					correctPassword=false;

				}
				oldPassword="";
			}


		}else if (newPW){
			if (newPassword.length() == 4){

				correctPassword=true;
				newPW = false;
				repeatNewPW = true;
				resetPassword();
				correctPassword=false;

			}
		}else if (repeatNewPW){
			if (repeatNewPassword.length() == 4){
				if (!repeatNewPassword.equals(newPassword)){
					Toast.makeText(this, "Passwords Do Not Match. Please Try Again", 1000).show();
					resetPassword();
				}
				else{
					correctPassword = true;
					
					
					
					DataOutputStream out = null;
				    try {
				    	deleteFile("password");
				   // 	DataOutputStream out = 
					//			new DataOutputStream(openFileOutput("password", Context.MODE_PRIVATE));
				    	out = 
								new DataOutputStream(openFileOutput("password", Context.MODE_PRIVATE));
				    	try{
							out.writeUTF(repeatNewPassword);
						}catch(Exception e){
							Log.e("writing error","error writing password to file");
						}
				    
				    	out.close();
				    	Toast.makeText(this, "Password Successfully Changed!", 1000).show();
				    	setResult(999,null);
					} catch (Exception e) {
						Toast.makeText(this, "Error: Password not Changed!", 1000).show();
						Log.e("writing error","error openeing file to write password in");
						e.printStackTrace();
					}
				    
					onDestroy();
					finish();
				}
			}
		}
	}

	private void resetPassword(){
		//Regardless of what password needs to be entered next, we want all the buttons to be reset
		for (int i=0; i<=9; i++){
			buttonChange(i,"green");
		}

		//The variability of this method is based on what password flag is correctly activitated:

		if (oldPW){
			oldPassword="";
		}else if (newPW){
			if (correctPassword){	
				TextView title = (TextView) findViewById(R.id.make_pw_title);
				title.setText(R.string.new_pw);
			}
			else{
				newPassword = "";
			}
		}else if(repeatNewPW){
			if (correctPassword){
				TextView title = (TextView) findViewById(R.id.make_pw_title);
				title.setText(R.string.repeat_new_pw);
			}
			else{
				repeatNewPassword="";
			}
		}
	}
	
	@Override
	public void onDestroy(){
		vib.cancel();
		super.onDestroy();
		}
}
