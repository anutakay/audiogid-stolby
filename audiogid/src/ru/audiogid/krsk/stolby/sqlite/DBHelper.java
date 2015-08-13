package ru.audiogid.krsk.stolby.sqlite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DBHelper extends SQLiteOpenHelper {

    private static final int BUFFER_SIZE = 1024;

    final String LOG_TAG = "DBHelper";

    private static final String DB_PATH = Constants.DB_PATH;

    private static final String DB_NAME = Constants.DB_NAME;

    private static final int DB_VERSION = Constants.DB_VERSION;

    Context mContext;

    public DBHelper(final Activity context) {
        super(context, DB_NAME, null, 1);
        mContext = context;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
            final int newVersion) {
    }

    private boolean checkDataBaseActuality() {
        SharedPreferences preferences = ((Activity) mContext)
                .getPreferences(Context.MODE_PRIVATE);
        Boolean correctVersion = false;
        correctVersion = preferences.getInt("db_version", 1) == DB_VERSION;
        return isDataBaseExist() && correctVersion;
    }

    private boolean isDataBaseExist() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    };

    public void createDataBase() throws IOException {

        boolean actual = checkDataBaseActuality();

        if (actual) {
            Log.d(LOG_TAG, "Base already exist and have right version");
        } else {
            updateDatabase();
        }

    }

    private void updateDatabase() throws Error {
        if (isDataBaseExist()) {
            mContext.deleteDatabase(DB_NAME);
        }
        getReadableDatabase();

        try {
            copyDataBase();
        } catch (IOException e) {
            throw new Error("Error copying database");
        }

        updateDatabaseVersion();
    }

    private void updateDatabaseVersion() {
        SharedPreferences sPref = ((Activity) mContext)
                .getPreferences(Context.MODE_PRIVATE);
        Editor ed = sPref.edit();
        ed.putInt("db_version", DB_VERSION);
        ed.commit();
        Log.d(LOG_TAG, "Base succesfully copied. Current version = "
                + DB_VERSION);
    }

    private void copyDataBase() throws IOException {

        InputStream myInput = mContext.getAssets()
                .open(DB_NAME + Constants.EXT);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }
}