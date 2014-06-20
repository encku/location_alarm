package com.dronexsoft.konumalarmi;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class AddMarkerActivity extends Activity {
	
	SQLite SaveData=new SQLite(this);
	SQLiteDatabase db;
	static double LocationX;
	static double LocationY;
	String title;
	String content;
	int Radius;
	boolean Status;
	String StrRadius;
	boolean Inner=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_marker);
		
		final EditText Title = (EditText) findViewById(R.id.titleText); 
		final EditText Content = (EditText) findViewById(R.id.cotentText);
		final ToggleButton TButton = (ToggleButton) findViewById(R.id.Status);
		final SeekBar mSeekbar = (SeekBar) findViewById(R.id.setRange);
		final TextView textRange = (TextView) findViewById(R.id.textRange);
		Status=TButton.isChecked();		
		
		
		//if click seek bar hide keyboard and text range
	    mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
	    	
	    	//text range
	    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	    	   textRange.setText(Integer.toString(progress+50));
	    	   Radius=progress;
	    	}
	    	
	    	//hide keyboard
	    	public void onStartTrackingTouch(SeekBar seekBar){
	    	   InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    	   imm.hideSoftInputFromWindow(mSeekbar.getWindowToken(), 0);
	    	}

	       public void onStopTrackingTouch(SeekBar seekBar) {}
	    });
	    //if click seek bar hide keyboard and text range
	        
		
		//cancel button
		Button Cancel=(Button) findViewById(R.id.Cancel);
		Cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
								
				finish();
			}
		});
		//cancel button
		
		
		//Add button
		Button Add=(Button) findViewById(R.id.Add);		
		Add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				StrRadius=String.valueOf(Radius);
				title=Title.getText().toString();
				content=Content.getText().toString();
				Status=TButton.isChecked();
				
				SaveDataSQLite();				
				
				// going to main screen and close this screen
				Intent MainScreen = new Intent();
				setResult(RESULT_OK,MainScreen);
				
				finish();
				
			}
		});
		//Add button end		
	}
	//OnCreate end
	
	
	//get latitude and longitude
	public static void LocationData(LatLng point){
		
		LocationX=point.latitude;
		LocationY=point.longitude;		
	}
	//get latitude and longitude
	
	
	//save data to database
	public void SaveDataSQLite(){		
		
		  db=SaveData.getWritableDatabase();
		  ContentValues cv=new ContentValues();
		  cv.put("Title", title);
		  cv.put("Content", content);
		  cv.put("LocationX", LocationX);
		  cv.put("LocationY", LocationY);
		  cv.put("Radius", Radius+50);
		  cv.put("Status", Status);
		  cv.put("Inner", Inner);
		  		  
		//save data
		  db.insertOrThrow("ALARMLIST",null,cv);	
	}
	//save data to database	
}
