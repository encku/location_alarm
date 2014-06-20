package com.dronexsoft.konumalarmi;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EditListActivity extends Activity {

	SQLiteDatabase db;
	SQLite SaveData=new SQLite(this);
	Cursor Import;
	int Id;
	String StrId;	
	String [] Coloums={"Id", "Title", "Content", "LocationX", "LocationY", "Radius", "Status", "Inner"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_list);
		
		Id=getIntent().getIntExtra("Id", 0);
		StrId="Id="+Id;
		
		UpdateData();
	
		final EditText Title = (EditText) findViewById(R.id.titleText); 
		final EditText Content = (EditText) findViewById(R.id.cotentText);
		final ToggleButton TButton = (ToggleButton) findViewById(R.id.Status);
		final SeekBar mSeekbar = (SeekBar) findViewById(R.id.setRange);
		final TextView textRange = (TextView) findViewById(R.id.textRange);
		
		Title.setText(Import.getString(Import.getColumnIndex("Title")));
		Content.setText(Import.getString(Import.getColumnIndex("Content")));
		textRange.setText(Import.getString(Import.getColumnIndex("Radius")));
		mSeekbar.setProgress(Import.getInt(Import.getColumnIndex("Radius")));
		TButton.setChecked(Import.getInt(Import.getColumnIndex("Status"))>0);
		
		
		
		//Save button
		Button Save=(Button) findViewById(R.id.Save);		
		Save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					
				db=SaveData.getWritableDatabase();
				ContentValues cv=new ContentValues();
				cv.put("Title", Title.getText().toString());
				cv.put("Content", Content.getText().toString());
				cv.put("Radius", mSeekbar.getProgress()+50);
				cv.put("Status", TButton.isChecked());
				db.update("ALARMLIST", cv, StrId, null);
						
				// going to main screen
				Intent AlarmListScreen = new Intent();
				AlarmListScreen.putExtra("Title", Title.getText().toString());
				AlarmListScreen.putExtra("Radius", mSeekbar.getProgress()+50);
				AlarmListScreen.putExtra("Status", TButton.isChecked());
				setResult(RESULT_OK,AlarmListScreen);
				finish();
			}
		});
		//Save button
		
		
		//cancel button
		Button Delete=(Button) findViewById(R.id.Cancel);
		Delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();			
			}
		});
		//cancel button
		
		
		//if click seek bar hide keyboard and text range
	    mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
	    	
	    	//text range
	    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	    	   textRange.setText(Integer.toString(progress+50));    	   
	    	}

	       //hide keyboard
	    	public void onStartTrackingTouch(SeekBar seekBar){
	    	   InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    	   imm.hideSoftInputFromWindow(mSeekbar.getWindowToken(), 0);
	    	}

	      public void onStopTrackingTouch(SeekBar seekBar) {}
	    });
	  //if click seek bar hide keyboard and text range
	}
	//onCreate end

	//update data
	public void UpdateData(){		
		db = SaveData.getReadableDatabase();
		Import = db.query("ALARMLIST", Coloums, StrId, null, null, null,null);
		Import.moveToFirst();
	}
	//update data	
}