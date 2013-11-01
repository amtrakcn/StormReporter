package com.wisc.StormReporter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import static com.wisc.StormReporter.Constants.STATE;
import static com.wisc.StormReporter.Constants.COUNTY;
import static com.wisc.StormReporter.Constants.NAME;

public class locationActivity extends Activity {
	RelativeLayout layout;
	String tempState;
	String tempCounty;
	String details;
	LocationData locations;
	AlertDialog stateDialog;
	AlertDialog countyDialog;
	String chosenStateAbr;
	String tempSpecificLocation;
	TextView countyTextView;
	TextView stateTextView;
	Button addStateButton;
	Button addCountyButton;
	Button cancelButton;
	EditText specificLocationEditText;
	String[] abrvs;
	ArrayList<String> states;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_layout);

		tempState="";
		tempCounty="";
		details="";
		locations = new LocationData(this);
		chosenStateAbr ="";

		countyTextView = (TextView) findViewById(R.id.addCountyTextView);
		addCountyButton = (Button) findViewById(R.id.addCountyButton);
		specificLocationEditText = (EditText) findViewById(R.id.specificLocationTextBox);
		tempSpecificLocation = "";
		cancelButton = (Button) findViewById(R.id.cancelButton);	
		stateTextView = (TextView) findViewById(R.id.addStateTextView);
		addStateButton = (Button) findViewById(R.id.addStateButton);
	}


	public void addState(View v){
		final View currview = (View) v;
		states= new ArrayList<String>();
		abrvs= new String[50];
		try{
			//Log.e("IN TESTING LOCATIONS","SHOULD BE WORKING");
			String[] choose = { STATE, COUNTY };
			String ORDER_BY = STATE + " ASC";
			Cursor cur = locations.getStationFromLocation(choose, null, null, null, ORDER_BY);
			int i = 0;
			String previous ="";
			while(cur.moveToNext()){
				//Log.e("Locs Output Test: ",cur.getString(0) + ", " + cur.getString(1) + ", " + cur.getString(2));
				String s = cur.getString(0);
				if(!s.equalsIgnoreCase(previous)){
					String ts = getState(s);
					if(ts.length()!=0){
						states.add(i,ts);
						abrvs[i]=s;
						i++;
					}
				}
				previous = s;
			}
			cur.close();
		} catch (Exception e) {
			Log.i("Locations database output test in locationsActivity", "Error");

		}

		final CharSequence[] items = getArray(states);
		AlertDialog.Builder builder = new AlertDialog.Builder(locationActivity.this);
		builder.setTitle("What state are you in?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				tempState = (String) items[item]; //add selected weather report
				chosenStateAbr=(String) abrvs[item];
				displayState(currview);
			}
		});
		stateDialog = builder.create();
		stateDialog.show();
	}

	public void displayState(View v){
		stateDialog.dismiss();

		stateTextView.setText(tempState);
		addStateButton.setText(R.string.erase);
		addStateButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				eraseState(v);
			}

		});

		//Load county graphics
		countyTextView.setVisibility(View.VISIBLE);
		addCountyButton.setVisibility(View.VISIBLE);

		//Change the cancel button to the clear button
		cancelButton.setText(R.string.clear);

		cancelButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				clearAll(v);

			}

		});
	}

	public void eraseState(View v){

		addStateButton.setText(R.string.add);
		addStateButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addState(v);
			}

		});
		stateTextView.setText(R.string.genericAddState);

		//Get rid of county and specific location graphics
		if(countyTextView.getVisibility() == View.VISIBLE){
			eraseCounty(v);
			countyTextView.setVisibility(View.GONE);
			addCountyButton.setVisibility(View.GONE);
		}

		tempState="";
		chosenStateAbr="";

	}

	public void addCounty(View v){
		final View currview = (View) v;
		ArrayList<String> counties = new ArrayList<String>();
		try{
			//Log.e("IN TESTING LOCATIONS","SHOULD BE WORKING");
			String[] choose = { STATE, COUNTY };
			String ORDER_BY = COUNTY + " ASC";
			String where = STATE + "='" +abrvs[states.indexOf(tempState)] +"'";
			Cursor cur = locations.getStationFromLocation(choose, where, null, null, ORDER_BY);
			while(cur.moveToNext()){
				counties.add(cur.getString(1));
			}
			cur.close();
		} catch (Exception e) {
			Log.i("Locations database output test in locationsActivity", "Error");

		}

		final CharSequence[] items = getArray(counties);
		AlertDialog.Builder builder = new AlertDialog.Builder(locationActivity.this);
		builder.setTitle("What county are you in?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				tempCounty = (String) items[item]; //add selected weather report
				displayCounty(currview);
			}
		});
		countyDialog = builder.create();
		countyDialog.show();
		Log.i("test",abrvs[states.indexOf(tempState)].toString());
	}

	public void displayCounty(View v){
		countyDialog.dismiss();
		Button b = (Button) v;
		countyTextView.setText(tempCounty + " County");
		b.setText(R.string.erase);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				eraseCounty(v);
			}

		});

		//display extra details graphic
		specificLocationEditText.setVisibility(View.VISIBLE);
	}

	public void eraseCounty(View v){
		Button b = (Button) findViewById(R.id.addCountyButton);
		b.setText(R.string.add);
		b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addCounty(v);
			}

		});
		countyTextView.setText(R.string.genericAddCounty);

		tempCounty="";

		//remove extra details graphic and data
		tempSpecificLocation = "";
		specificLocationEditText.setText("");
		specificLocationEditText.setHint(R.string.specificLocationHint);
		specificLocationEditText.setVisibility(View.GONE);
	}

	public CharSequence[] getArray(ArrayList<String> a){
		CharSequence[] b = new CharSequence[a.size()];

		for (int i =0; i < a.size(); i++){
			b[i] = a.get(i).toString();
		}

		return b;
	}

	public void cancel(View v){
		setResult(Activity.RESULT_CANCELED);
		onDestroy();
		finish();

	}

	public void save(View v){
		tempSpecificLocation = specificLocationEditText.getText().toString();
		if(chosenStateAbr.length() == 0 || tempCounty.length() == 0){
			Toast.makeText(this, "Cannot save without state and county information", 1000).show();
		}
		else{
			Intent resultIntent = new Intent();
			resultIntent.putExtra("STATE", chosenStateAbr);
			resultIntent.putExtra("COUNTY", tempCounty);
			resultIntent.putExtra("SPECIFIC_LOC", tempSpecificLocation);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
	}

	public void clearAll(View v){
		tempState="";
		tempCounty="";
		tempSpecificLocation="";

		eraseState(v);

		cancelButton.setText(R.string.cancel);
		cancelButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				cancel(v);	
			}
		});
	}

	public String getState(String s){
		if (s.equalsIgnoreCase("ak")){
			return "Alaska";
		}
		else if (s.equalsIgnoreCase("al")){
			return "Alabama";
		}
		else if (s.equalsIgnoreCase("az")){
			return "Arizona";
		}
		else if (s.equalsIgnoreCase("ar")){
			return "Arkansas";
		}
		else if (s.equalsIgnoreCase("ca")){
			return "California";
		}
		else if (s.equalsIgnoreCase("co")){
			return "Colorado";
		}
		else if (s.equalsIgnoreCase("ct")){
			return "Connecticut";
		}
		else if (s.equalsIgnoreCase("de")){
			return "Delaware";
		}
		else if (s.equalsIgnoreCase("fl")){
			return "Florida";
		}
		else if (s.equalsIgnoreCase("ga")){
			return "Georgia";
		}
		else if (s.equalsIgnoreCase("hi")){
			return "Hawaii";
		}
		else if (s.equalsIgnoreCase("id")){
			return "Idaho";
		}
		else if (s.equalsIgnoreCase("il")){
			return "Illinois";
		}
		else if (s.equalsIgnoreCase("in")){
			return "Indiana";
		}
		else if (s.equalsIgnoreCase("ia")){
			return "Iowa";
		}
		else if (s.equalsIgnoreCase("ky")){
			return "Kentucky";
		}
		else if (s.equalsIgnoreCase("ks")){
			return "Kansas";
		}
		else if (s.equalsIgnoreCase("la")){
			return "Louisiana";
		}
		else if (s.equalsIgnoreCase("me")){
			return "Maine";
		}
		else if (s.equalsIgnoreCase("md")){
			return "Maryland";
		}
		else if (s.equalsIgnoreCase("ma")){
			return "Massachusetts";
		}
		else if (s.equalsIgnoreCase("mn")){
			return "Minnesota";
		}
		else if (s.equalsIgnoreCase("mi")){
			return "Michigan";
		}
		else if (s.equalsIgnoreCase("ms")){
			return "Mississippi";
		}
		else if (s.equalsIgnoreCase("mo")){
			return "Missouri";
		}
		else if (s.equalsIgnoreCase("mt")){
			return "Montana";
		}
		else if (s.equalsIgnoreCase("ne")){
			return "Nebraska";
		}
		else if (s.equalsIgnoreCase("nv")){
			return "Nevada";
		}
		else if (s.equalsIgnoreCase("nh")){
			return "New Hampshire";
		}
		else if (s.equalsIgnoreCase("nj")){
			return "New Jersey";
		}
		else if (s.equalsIgnoreCase("nm")){
			return "New Mexico";
		}
		else if (s.equalsIgnoreCase("ny")){
			return "New York";
		}
		else if (s.equalsIgnoreCase("nc")){
			return "North Carolina";
		}
		else if (s.equalsIgnoreCase("nd")){
			return "North Dakota";
		}
		else if (s.equalsIgnoreCase("oh")){
			return "Ohio";
		}
		else if (s.equalsIgnoreCase("ok")){
			return "Oklahoma";
		}
		else if (s.equalsIgnoreCase("or")){
			return "Oregon";
		}
		else if (s.equalsIgnoreCase("pa")){
			return "Pennsylvania";
		}
		else if (s.equalsIgnoreCase("ri")){
			return "Rhode Island";
		}
		else if (s.equalsIgnoreCase("sc")){
			return "South Carolina";
		}
		else if (s.equalsIgnoreCase("sd")){
			return "South Dakota";
		}
		else if (s.equalsIgnoreCase("tn")){
			return "Tennessee";
		}
		else if (s.equalsIgnoreCase("tx")){
			return "Texas";
		}
		else if (s.equalsIgnoreCase("ut")){
			return "Utah";
		}
		else if (s.equalsIgnoreCase("vt")){
			return "Vermont";
		}
		else if (s.equalsIgnoreCase("va")){
			return "Virginia";
		}
		else if (s.equalsIgnoreCase("wa")){
			return "Washington";
		}
		else if (s.equalsIgnoreCase("wv")){
			return "West Virginia";
		}
		else if (s.equalsIgnoreCase("wi")){
			return "Wisconsin";
		}
		else if (s.equalsIgnoreCase("wy")){
			return "Wyoming";
		}
		else{
			//return s +": UNKOWN STATE";
			return "";
		}
	}

	@Override
	public void onDestroy(){
		locations.close();

		super.onDestroy();
	}
}
