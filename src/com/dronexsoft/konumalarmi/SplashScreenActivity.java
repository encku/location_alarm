package com.dronexsoft.konumalarmi;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class SplashScreenActivity extends Activity {

    Timer splashTimer =  new Timer();  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
			
	    splashTimer.schedule(new TimerTask() {
	 
	    	@Override
	    	public void run() {
	            	
	    		Intent intent =  new Intent(SplashScreenActivity.this, MainActivity.class);
	    		startActivity(intent);
	    		splashTimer.cancel();
	    		finish();
	          	
	    	}
	    },2000);
	         
	}
			
}
