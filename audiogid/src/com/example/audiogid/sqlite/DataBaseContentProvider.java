package com.example.audiogid.sqlite;

import java.io.IOException;

import com.example.audiogid.IRecordSetter;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class DataBaseContentProvider implements IRecordSetter {
	
	private final String LOG_TAG = "DataBaseActivity";
	
	private DBHelper dbHelper;
	
	private IRecordSetter recordSetter = this;
	
	public DataBaseContentProvider(final Activity context) {
		init(context);
	}
	
    private void init(final Activity context){
    	dbHelper = new DBHelper(context);
        try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void setRecordSetter(final IRecordSetter recordSetter) {
		this.recordSetter = recordSetter;
	}
    
    public final void getData(){
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor c = db.query(Constants.RECORDS_TABLE, null, null, null, null, null, null);
    	  if (c.moveToFirst()) {
    		  int longColIndex = c.getColumnIndex(Constants.LON_COLUMN);
    		  int latColIndex = c.getColumnIndex(Constants.LAT_COLUMN);
    		  int titleColIndex = c.getColumnIndex(Constants.TITLE_COLUMN);
    		  int diameterColIndex = c.getColumnIndex(Constants.DIAMETER_COLUMN);
    		  do {
    			  if(recordSetter == null) { break; }
    			  recordSetter.setRecord( c.getDouble(longColIndex),
    					  c.getDouble(latColIndex), 
    					  c.getString(titleColIndex), 
    					  c.getInt(diameterColIndex)
    					  );
    		  } while(c.moveToNext());
    	  }else{
    		  Log.d(LOG_TAG, "Empty records table");
    	  }
    	  c.close();
    }
    
}
