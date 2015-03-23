package com.example.audiogid.sqlite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.audiogid.notif.NotificationUtils;

import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;


public abstract class DataBaseContentProvider {
	
	final String LOG_TAG = "DataBaseActivity";
	
	SharedPreferences sPref;
	
	DBHelper dbHelper;
	
	private Activity context;
	
	public DataBaseContentProvider(Activity _context){
		context = _context;
		init(context);
	}

    private void init(Context context){
    	dbHelper = new DBHelper(context);
        try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public final void getData(){
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor c = db.query(Constants.RECORDS_TABLE, null, null, null, null, null, null);
    	  if (c.moveToFirst()) {
    		  int longColIndex = c.getColumnIndex(Constants.LONG_COLUMN);
    		  int latColIndex = c.getColumnIndex(Constants.LAT_COLUMN);
    		  int filenameColIndex = c.getColumnIndex(Constants.TITLE_COLUMN);
    		  int diameterColIndex = c.getColumnIndex(Constants.DIAMETER_COLUMN);
    		  do {
    			  setRecord( c.getDouble(longColIndex),
    					  c.getDouble(latColIndex), 
    					  c.getString(filenameColIndex), 
    					  c.getInt(diameterColIndex)
    					  );
    		  } while(c.moveToNext());
    	  }else{
    		  Log.d(LOG_TAG, "Empty records table");
    	  }
    	  c.close();
    }
    
    abstract protected void setRecord(double lon, double lat, String filename, int d);

    
    class DBHelper extends SQLiteOpenHelper {
    	
    	private static final String DB_PATH = Constants.DB_PATH;
    	 
        private static final String DB_NAME = Constants.DB_NAME;
        
        private static final int DB_VERSION = Constants.DB_VERSION;
        
        Context mContext;

        public DBHelper(Context context) {
        	super(context, DB_NAME, null, 1);
            this.mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        } 
        
        private boolean checkDataBase(){
        	sPref = ((Activity) context).getPreferences(context.MODE_PRIVATE);  
            Boolean correctVersion = false;
            correctVersion = sPref.getInt("db_version", 1) == DB_VERSION;
            return isDataBaseExist() && correctVersion;
        }
        
        private boolean isDataBaseExist(){
        	File dbFile = new File(DB_PATH + DB_NAME);        	
        	return dbFile.exists();
        };
        
        public void createDataBase() throws IOException{
        	 
            boolean dbExist = checkDataBase();
     
            if(dbExist){
            	Log.d(LOG_TAG,"Base already exist and have right version");
            }else{
            	if(isDataBaseExist()){
            		mContext.deleteDatabase(DB_NAME);
            	}
                this.getReadableDatabase();
               
                try {
     
                    copyDataBase();
                    
     
                } catch (IOException e) {
     
                    throw new Error("Error copying database");
     
                }
                sPref = ((Activity) context).getPreferences(context.MODE_PRIVATE);
                Editor ed = sPref.edit();
                ed.putInt("db_version", DB_VERSION);
                ed.commit();
                Log.d(LOG_TAG,"Base succesfully copied. Current version = " + DB_VERSION);
            }
     
        }

        private void copyDataBase() throws IOException{
        	
            InputStream myInput = mContext.getAssets().open(DB_NAME + Constants.EXT);
     
            String outFileName = DB_PATH + DB_NAME;
     
            OutputStream myOutput = new FileOutputStream(outFileName);
     
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0){
                myOutput.write(buffer, 0, length);
            }
     
            myOutput.flush();
            myOutput.close();
            myInput.close();
     
        }
      }
}
