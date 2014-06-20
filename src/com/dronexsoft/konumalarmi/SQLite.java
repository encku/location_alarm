package com.dronexsoft.konumalarmi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper{
	
	 static final String ALARMDATA="ALARMDATA";
	 static final int version=1;

	public SQLite(Context context) {
		super(context, ALARMDATA, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE ALARMLIST (Id INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT, Content TEXT, LocationX NUMERIC, LocationY NUMERIC, Radius INTEGER, Status BOOLEAN, Inner BOOLEAN );");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXIST ALARMLIST");
		  onCreate(db);
		
	}

}
