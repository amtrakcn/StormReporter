package com.wisc.StormReporter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class dateAndTimeActivity extends Activity{

	DatePicker date;
	TimePicker time;
	Button save;
	Button clear;
	String day;
	String month;
	String year;
	String hour;
	String minute;
	Boolean pm;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datetimelayout_new);

		date = (DatePicker) findViewById(R.id.datePicker);
		time = (TimePicker) findViewById(R.id.timePicker);
		save = (Button) findViewById(R.id.saveAllButton);
		clear = (Button) findViewById(R.id.cancelAllButton);
		day = "";
		month="";
		year="";
		hour="";
		minute="";
		pm = false;
	}
	
	public void setDate(View v){
		day = String.valueOf(date.getDayOfMonth());
		year = String.valueOf(date.getYear());
		switch (date.getMonth()){
		case 0:
			month="January";
			break;
		case 1:
			month="February";
			break;
		case 2:
			month="March";
			break;
		case 3:
			month="April";
			break;
		case 4:
			month="May";
			break;
		case 5:
			month="June";
			break;
		case 6:
			month="July";
			break;
		case 7:
			month="August";
			break;
		case 8:
			month="September";
			break;
		case 9:
			month="October";
			break;
		case 10:
			month="November";
			break;
		case 11:
			month="December";
			break;
		}
	}
	
	public void setTime(View v){
		int h = time.getCurrentHour();
		if (h > 12){
			pm = true;
			h = h-12;
		}
		
		hour = String.valueOf(h);
		int m = time.getCurrentMinute();
		if(m < 10){
			minute = "0"+String.valueOf(m);
		}else{
			minute = String.valueOf(m);
		}
	}
	
	public void save (View v){
		setDate(v);
		setTime(v);
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra("YEAR", year);
		resultIntent.putExtra("MONTH", month);
		resultIntent.putExtra("DAY", day);
		resultIntent.putExtra("HOUR", hour);
		resultIntent.putExtra("MINUTE", minute);
		resultIntent.putExtra("PM", pm);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
	
	public void cancel(View v){
		setResult(Activity.RESULT_CANCELED);
		finish();
	}
}
