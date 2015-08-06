package ru.audiogid.krsk.stolby.sqlite;

import java.io.IOException;

import ru.audiogid.krsk.stolby.model.IRecordSetter;
import ru.audiogid.krsk.stolby.model.Record;
import ru.audiogid.krsk.stolby.model.StaticPoint;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseContentProvider{
	
	private final String LOG_TAG = "DataBaseActivity";
	
	private DBHelper dbHelper;
	
	private IRecordSetter recordSetter;
	
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
    
    public final void getData() {
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor c = db.query(Constants.STATIC_OBJECTS_TABLE, null, null, null, null, null, null);
  	  if (c.moveToFirst()) {
		  int longColIndex = c.getColumnIndex(Constants.LON_COLUMN);
		  int latColIndex = c.getColumnIndex(Constants.LAT_COLUMN);
		  int titleColIndex = c.getColumnIndex(Constants.TITLE_COLUMN);
		  do {
			  if(recordSetter == null) { break; }
			  StaticPoint sp = new StaticPoint(c.getDouble(longColIndex),
					  				c.getDouble(latColIndex),  
					  				c.getString(titleColIndex));
			  recordSetter.setStaticPoint(sp);
		  } while(c.moveToNext());
	  }else{
		  Log.d(LOG_TAG, "Empty records table");
	  }
	  c.close(); 
    	
    	c = db.query(Constants.RECORDS_TABLE, null, null, null, null, null, null);
    	  if (c.moveToFirst()) {
    		  int longColIndex = c.getColumnIndex(Constants.LON_COLUMN);
    		  int latColIndex = c.getColumnIndex(Constants.LAT_COLUMN);
    		  int titleColIndex = c.getColumnIndex(Constants.TITLE_COLUMN);
    		  int diameterColIndex = c.getColumnIndex(Constants.DIAMETER_COLUMN);
    		  int audioColIndex = c.getColumnIndex(Constants.AUDIO_FILENAME_COLUMN);
    		  do {
    			  if(recordSetter == null) { break; }
    			  Record r = new Record(c.getDouble(longColIndex),
    					  				c.getDouble(latColIndex), 
    					  				c.getInt(diameterColIndex),  
    					  				c.getString(titleColIndex), 
    					  				c.getString(audioColIndex));
    			  recordSetter.setRecord(r);
    		  } while(c.moveToNext());
    	  }else{
    		  Log.d(LOG_TAG, "Empty records table");
    	  }
    	  c.close();
    	  
    	  c = db.query(Constants.STATIC_OBJECTS_TABLE, null, null, null, null, null, null);
    	  if (c.moveToFirst()) {
    		  int longColIndex = c.getColumnIndex(Constants.LON_COLUMN);
    		  int latColIndex = c.getColumnIndex(Constants.LAT_COLUMN);
    		  int titleColIndex = c.getColumnIndex(Constants.TITLE_COLUMN);
    		  do {
    			  if(recordSetter == null) { break; }
    			  StaticPoint sp = new StaticPoint(c.getDouble(longColIndex),
    					  				c.getDouble(latColIndex),  
    					  				c.getString(titleColIndex));
    			  recordSetter.setStaticPoint(sp);
    		  } while(c.moveToNext());
    	  }else{
    		  Log.d(LOG_TAG, "Empty records table");
    	  }
    	  c.close();
    }
    
}
