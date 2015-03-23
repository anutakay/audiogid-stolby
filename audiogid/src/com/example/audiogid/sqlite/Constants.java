package com.example.audiogid.sqlite;

import android.annotation.SuppressLint;

public class Constants {
	
	public static final String PACKAGE = "com.example.audiogid";
	
	@SuppressLint("SdCardPath")
	public static final String DB_PATH = "/data/data/" + PACKAGE + "/databases/";
	public static final String DB_NAME = "1"; 
	public static final int DB_VERSION = 2;
	public static final String RECORDS_TABLE = "stolby";
	public static final String LONG_COLUMN = "lg";
	public static final String LAT_COLUMN = "lt";
	public static final String FILENAME_COLUMN = "audio";
	public static final String TITLE_COLUMN = "name";
	public static final String DIAMETER_COLUMN = "diametr";
	
	public static final String EXT = ".sqlite";
}
