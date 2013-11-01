package com.wisc.StormReporter;

import static com.wisc.StormReporter.Constants.EMAIL;
import static com.wisc.StormReporter.Constants.PHONE;
import static com.wisc.StormReporter.Constants.STATION_ID;
import static com.wisc.StormReporter.Constants.NAME;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity{
	String BODY;
	String stationName;
	StormReport report;
	String[] DATA;
	Button sendButton;
	StationData stations;
	Uri IMAGE;
	//StormReport report;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		//Use a try/catch block to avoid any crash when trying to retrieve data from the parent
		//activity
		try {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				BODY = extras.getString("body");
				DATA = extras.getStringArray("data");
				IMAGE = (Uri) extras.getParcelable("image");
				//USERPW = extras.getString("USERPW");
				//USERNAME = extras.getString("username");
			}
		} catch (Exception e) {
			Log.e("IntentCatch","Can't input bundle from intent");

		}
		stations = new StationData(this);
		try{
			String[] FROM = { STATION_ID, NAME };
			String where = STATION_ID;
			String[] findParam = { DATA[0] };
			Cursor cur = stations.getStation(FROM, where, findParam, null, null);
			if (cur.getCount() == 1){
				cur.moveToNext();
				stationName = cur.getString(1);
				cur.close();
				stations.close();
			}else{
				cur.close();
				stations.close();
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.e("Error loading station data in StormReport class", "error");
			stations.close();
		}
		Log.i("data size in test activity", String.valueOf(DATA.length));
		sendButton = (Button) findViewById(R.id.send_report_button);
		sendButton.setText("Send Report to: " + stationName);
		TextView text = (TextView) findViewById(R.id.test_text_view);
		text.setText(Html.fromHtml(BODY));
		/*report = new StormReport(getApplicationContext());
		report.addStationRecipient();
		ArrayList<String> details = new ArrayList<String>();
		ArrayList<String> damage = new ArrayList<String>();
		ArrayList<String> images = new ArrayList<String>();
		details.add("This is a test of the details arrayList");
		details.add("This is another test of the details arrayList. Once upon a time there was a large alligator who loved eating small children");
		images.add("Image 1");
		images.add("Image 2");
		damage.add("This is a test of the damage arrayList");
		report.setDamages(damage);
		report.setDetails(details);
		report.setImages(images);
		report.setReportType("TORNADO");
		String BODY = report.compile();
		Log.i("test compile",BODY);

		TextView text = (TextView) findViewById(R.id.test_text_view);
		text.setText(Html.fromHtml(BODY));

		setResult(999,null);*/

	}

	public void onSendReport(View v){

		Intent i = send();
		try {
			startActivity(Intent.createChooser(i, "Send report via..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(com.wisc.StormReporter.TestActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

	public Intent send(){

		String tempEmail = "testnwsstation@yahoo.com";

		String subject = "Test Storm Report";
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("text/html");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
				new String[]{tempEmail});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
				subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
				Html.fromHtml(BODY));
		if(IMAGE != null){
			try{
			emailIntent.putExtra(Intent.EXTRA_STREAM, IMAGE);
			}catch(Exception e){
				Log.e("Error loading image in email", "not able to attsach into intent");
			}
		}


		return emailIntent;
	}

}
