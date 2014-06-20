package com.dronexsoft.konumalarmi;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;


public class MainActivity extends Activity implements OnMapLongClickListener {
	
		
	static List <Alarm> AlarmList = new ArrayList <Alarm>();
	SQLiteDatabase db;
	SQLite SaveData=new SQLite(this);
	private static GoogleMap mMap;
	String [] Coloums={"Id", "Title", "Content", "LocationX", "LocationY", "Radius", "Status", "Inner"};
	Cursor Import;
	Vibrator mVibrator;
	MediaPlayer PlaySound;
	LocationManager locationManager;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
 
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();        
        ImportData();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        PlaySound = MediaPlayer.create(MainActivity.this,R.raw.alarm);  
    
        
        //alarm list button
        ImageButton AlarmListButton = (ImageButton) findViewById(R.id.AlarmList);
        AlarmListButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {								
				Intent AlarmListScreen = new Intent(MainActivity.this,AlarmListActivity.class);
				startActivityForResult(AlarmListScreen,1);
			}
		});
        //alarm list button
        
        
        //focus my location button
        ImageButton focusMyLocation=(ImageButton) findViewById(R.id.FocusMyLocation);
        focusMyLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (ControlMyLocAndMap()){
					double lat= mMap.getMyLocation().getLatitude();
					double lng = mMap.getMyLocation().getLongitude();
					LatLng myLoc = new LatLng(lat, lng);
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
				}
				else{
	          		Toast toast=Toast.makeText(MainActivity.this, getText(R.string.loc_not_be_found), Toast.LENGTH_SHORT);
	          		toast.setGravity(Gravity.CENTER, 0, 0);
	          		toast.show();
				}
			}
		});
        //focus my location button
        
        
        //zoom out button
        ImageButton zoomOut=(ImageButton) findViewById(R.id.zoomOut);
        zoomOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				mMap.animateCamera(CameraUpdateFactory.zoomOut());
			}
		});
        //zoom out button
        
        
        //zoom in button
        ImageButton zoomIn=(ImageButton) findViewById(R.id.zoomIn);
        zoomIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				mMap.animateCamera(CameraUpdateFactory.zoomIn()); 
			}
		});
        //zoom in button
                     
           
        //location chanced function start
        LocationManager lm  = (LocationManager)getSystemService(Context.LOCATION_SERVICE);      	 
    	LocationListener dinleyici = new LocationListener() {

			@Override
			public void onLocationChanged(Location MyLocation) {
				
				alarmControl(MyLocation);
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}	      
    	};
 
    	lm.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, dinleyici);
    	//location chanced function end
        
    }
    //on create end
    
    
    //GPS control with on resume
    protected void onResume() {
        super.onResume();
        
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  
        	
        	preferences = getSharedPreferences("mypref", MODE_PRIVATE);
        	editor = preferences.edit();
    		
        	if(preferences.getBoolean("firststart", true)){
          		editor.putBoolean("firststart", false);
          		editor.commit();
          		
          		Toast toast = new Toast(getApplicationContext());

          		toast=Toast.makeText(this, getString(R.string.message1), Toast.LENGTH_SHORT);
          		toast.setGravity(Gravity.CENTER, 0, 0);
          		toast.show();
          		
          		toast=Toast.makeText(this, getString(R.string.message2), Toast.LENGTH_LONG);
          		toast.setGravity(Gravity.CENTER, 0, 0);
          		toast.show();
          		
          		toast=Toast.makeText(this, getString(R.string.message3), Toast.LENGTH_LONG);
          		toast.setGravity(Gravity.CENTER, 0, 0);
          		toast.show();
          		}
        }
        else{
            	showGPSDisabledAlertToUser();
            	onPause();
        }
    }
    //GPS control with on resume
    
    
    //GSP alert dialog
    private void showGPSDisabledAlertToUser(){
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.gps_message)
        .setCancelable(false)
        .setPositiveButton(R.string.gps_button,
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();   
    }
    //GSP alert dialog
    
     
    //long click function
	@Override
	public void onMapLongClick(LatLng point) {		
		// going to add marker screen
		Intent AddMarkerScreen = new Intent(this,AddMarkerActivity.class);
		AddMarkerActivity.LocationData(point);
		startActivityForResult(AddMarkerScreen,1);					
	}
	//long click function
	
	
	//function of back any activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			
		     if(resultCode == RESULT_OK){      
		    	 mMap.clear();
		    	 ImportData();
		    	 if(ControlMyLocAndMap()){
		    		 alarmControl(mMap.getMyLocation());
		    	 }   	 
		     }
		     
		     if (resultCode == RESULT_CANCELED) {    
		    	 mMap.clear();
		    	 ImportData();
		    	 if(ControlMyLocAndMap()){
		    		 alarmControl(mMap.getMyLocation()); 
		    	 }		    	 
		     }
		     
		  }
	}
	//function of back any activity
	
	
	//import data, add list and add alarm flag on map
	public void ImportData(){
					
		AlarmList.clear();
		BitmapDescriptor MarkerIcon;
		
		db = SaveData.getReadableDatabase();
		Import = db.query("ALARMLIST", Coloums, null, null, null, null, null);
			
		while(Import.moveToNext()){
						
			Alarm alarm=new Alarm();
			
			alarm.Inner=Import.getInt(Import.getColumnIndex("Inner")) > 0;
			alarm.Id=Import.getInt(Import.getColumnIndex("Id"));				
			alarm.Status=Import.getInt(Import.getColumnIndex("Status")) > 0;
			
			if(alarm.Status==true){
				MarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.alarmon);
			}
			else{
				MarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.alarmoff);
			}
					  			
			// add new marker on map
			alarm.marker=mMap.addMarker(new MarkerOptions()
	        				.position(new LatLng(Import.getDouble(Import.getColumnIndex("LocationX")),Import.getDouble(Import.getColumnIndex("LocationY"))))
	        				.title(Import.getString(Import.getColumnIndex("Title")))
	        				.icon(MarkerIcon)
	        				.snippet(Import.getString(Import.getColumnIndex("Content"))));
			
			//add circle on new marker
			alarm.circle=mMap.addCircle(new CircleOptions()
		     				.center(new LatLng(Import.getDouble(Import.getColumnIndex("LocationX")),Import.getDouble(Import.getColumnIndex("LocationY"))))
		     				.radius(Import.getDouble(Import.getColumnIndex("Radius")))
		     				.strokeColor(0x400000ff)
		     				.strokeWidth(2)
		     				.fillColor(0x400000ff));		     	
		     	
			AlarmList.add(alarm);		
			
		}				
	}
	//import data, add list and add alarm flag on map
	
	
	//alarm control function
	public void alarmControl(Location location){      
    	for(Alarm alarm : AlarmList){
			
			Location alarmloc = new Location("LocAlarm");
			alarmloc.setLatitude(alarm.marker.getPosition().latitude);
			alarmloc.setLongitude(alarm.marker.getPosition().longitude);
			String StrId="Id="+alarm.Id;
					
			double distance = alarmloc.distanceTo(location);
			
			if(distance < alarm.circle.getRadius() && alarm.Status==true && alarm.Inner==false ){			
				
				alarm.Inner = true;		
				
				db=SaveData.getWritableDatabase();
				ContentValues cv=new ContentValues();
				cv.put("Inner", alarm.Inner);
				db.update("ALARMLIST", cv, StrId, null);			
			
				mVibrator.vibrate(1000);
				PlaySound.start();				
		
			}
				else if(distance >= alarm.circle.getRadius()){
					alarm.Inner = false;
				
					db=SaveData.getWritableDatabase();
					ContentValues cv=new ContentValues();
					cv.put("Inner", alarm.Inner);
					db.update("ALARMLIST", cv, StrId, null);		
				}						
		}
    }
	//alarm control function
	
	
	//control get my location and map
	public boolean ControlMyLocAndMap(){	
		return(mMap.isMyLocationEnabled() && mMap.getMyLocation() != null);	
	}
	//control get my location and map
		
	
}
