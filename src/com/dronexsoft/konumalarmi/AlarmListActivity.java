package com.dronexsoft.konumalarmi;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlarmListActivity extends Activity {
	
	SQLiteDatabase db;
	SQLite SaveData=new SQLite(this);
	AlertDialog.Builder alertDialogBuilder;
	ArrayAdapter <Alarm> Adapter;
	ListView List;
	Alarm alarm;
	String [] Coloums={"Id", "Title", "Content", "LocationX", "LocationY", "Radius", "Status", "Inner"};
	Cursor Import;
	String StrId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_list);
		
		alertDialogBuilder = new AlertDialog.Builder(this);
		Adapter = new MyListAdapter();
		List=(ListView) findViewById(R.id.AlarmList);
		List.setAdapter(Adapter);
		
		ClickListforEdit();
	}	
	
	
	private void ClickListforEdit(){
		List.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {				
				
				alarm = MainActivity.AlarmList.get(position);
				StrId="Id="+alarm.Id;
												
		        alertDialogBuilder.setMessage(alarm.marker.getTitle())
		        .setCancelable(true)
		        .setPositiveButton(R.string.delete,
		                new DialogInterface.OnClickListener(){
		            public void onClick(DialogInterface dialog, int id){
		            	
		            	db=SaveData.getWritableDatabase();
		        		db.delete("ALARMLIST", StrId, null);
		        			        		
		            	MainActivity.AlarmList.remove(alarm);
		            	List.setAdapter(Adapter);
		            	 
		            	Intent MainScreen = new Intent();
		            	setResult(RESULT_OK,MainScreen);            	
		            }
		        });
		        
		        alertDialogBuilder.setNegativeButton(R.string.edit_alarm,
		                new DialogInterface.OnClickListener(){
		            public void onClick(DialogInterface dialog, int id){
		            	
						Intent EditListScreen = new Intent(AlarmListActivity.this,EditListActivity.class);				
						EditListScreen.putExtra("Id",alarm.Id);
						startActivityForResult(EditListScreen,1);
		            }
		        });
		        
		        AlertDialog alert = alertDialogBuilder.create();
		        alert.show();
		       
			}
			
		});
	}

	
	private class MyListAdapter extends ArrayAdapter <Alarm> {
		
		public MyListAdapter(){
			super(AlarmListActivity.this, R.layout.item_view, MainActivity.AlarmList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View itemView = convertView;
			
			if(itemView == null){
				itemView=getLayoutInflater().inflate(R.layout.item_view, parent, false);
			}
			
			final Alarm currentAlarm=MainActivity.AlarmList.get(position);
			
			TextView textViewTitle=(TextView)itemView.findViewById(R.id.titleView);
			textViewTitle.setText(currentAlarm.marker.getTitle());
			
			final ToggleButton toggleButton=(ToggleButton)itemView.findViewById(R.id.statusView);
			toggleButton.setChecked(currentAlarm.Status);
			
			TextView textViewRange=(TextView)itemView.findViewById(R.id.rangeView);
			textViewRange.setText(String.valueOf(currentAlarm.circle.getRadius()));
			
			toggleButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					String selection = "Id="+currentAlarm.Id;			
					currentAlarm.Status=toggleButton.isChecked();
					
					db=SaveData.getReadableDatabase();
					ContentValues cv=new ContentValues();	
					cv.put("Status", currentAlarm.Status);
					db.update("ALARMLIST", cv, selection, null);
				}
			});
			
			return itemView;

		}				
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
		     
			if(resultCode == RESULT_OK){
		    	 		    	 
		    	 alarm.marker.setTitle(data.getStringExtra("Title"));
		    	 alarm.circle.setRadius(data.getIntExtra("Radius",0));
		    	 alarm.Status=data.getBooleanExtra("Status", true);
		    	 List.setAdapter(Adapter);
		    	 	    	 
		    	 Intent MainScreen = new Intent();
		    	 setResult(RESULT_OK,MainScreen);	    	 		    	 
			}		      
		}
	}
	
}
