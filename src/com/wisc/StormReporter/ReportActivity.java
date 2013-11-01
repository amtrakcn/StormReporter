package com.wisc.StormReporter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;




/**
 * Note the name is StromReporter, not storm, to avoid conflict
 * This activity generates a new report or displays an existing report for edit and submit.
 * However, currently editing is not implemented and user cannot see what have been written into the current report.
 * All these funcionality will be added later.
 * @author Amtrak
 *
 */
public class ReportActivity extends Activity{
	
	//create a new report object
	Report currentReport = new Report();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.report_main);

        //The "add" button on main screen, used to add a weather type to report
        Button addWeather = (Button) findViewById(R.id.buttonAddWeather);
        addWeather.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
						    			    	
				selectWeather();
				
			}        	
        });
        
        //functionality of "save" button on main interface, used to save the user inputted lcation to report
        final EditText locationInput = (EditText) findViewById(R.id.editText1);
        final TextView currentSetLocation = (TextView) findViewById(R.id.textLocation);
        Button saveLocationButton = (Button) findViewById(R.id.buttonSaveLocation);
        saveLocationButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				currentReport.setLocation(locationInput.getText().toString());
				currentSetLocation.setText(locationInput.getText());
			}
        	
        });
        
        //functionnality of add photo button on main interface, used to add photo from camera
        //where Tyrell's camera code is needed
      /*  Button addPhotoButton = (Button) findViewById(R.id.buttonAddPhoto);
        addPhotoButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				addPhoto();
				
			}
        	
        });*/
        
        //the button "Submit"
        Button saveForLaterButton = (Button) findViewById(R.id.buttonSaveForLater);
        saveForLaterButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        //the button "Save for Later"
        Button sendButton = (Button) findViewById(R.id.buttonSubmit);       
        sendButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent sendReport = new Intent(Intent.ACTION_SEND_MULTIPLE); 
				sendReport.setType("image/jpg"); 
				sendReport.putExtra(Intent.EXTRA_TEXT, "I'm the report!"); 
				startActivity(Intent.createChooser(sendReport, "Share Email"));
			}      	
        });
        
       
        
    }
    
    //function to select weather type from pop up and add to report
    private void selectWeather() {
    	
    	Resources res = getResources();
        final CharSequence[] items = (CharSequence[]) res.getStringArray(R.array.weatherType);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
    	builder.setTitle("Pick Weather");
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
    	        currentReport.getWeatherType().add((String) items[item]); //add selected weather report
    	        dialog.dismiss();
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    	
    	//return new String[0];
    }
    
    //function to take photo from camera, not implemented
    private void addPhoto() {
    	
    }
    
    
}
