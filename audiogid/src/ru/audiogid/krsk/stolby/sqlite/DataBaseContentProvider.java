package ru.audiogid.krsk.stolby.sqlite;

import java.io.IOException;

import ru.audiogid.krsk.stolby.R;
import ru.audiogid.krsk.stolby.model.RecordSetter;
import ru.audiogid.krsk.stolby.model.Record;
import ru.audiogid.krsk.stolby.model.StaticPoint;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseContentProvider {
    
    DatabaseHelper databaseHelper;

    RecordSetter recordSetter;
    
    private Constants constants;
    
    private class Constants {

        String records_table;
    
        String statis_objects_table;
    
        String longitude_column;
    
        String latitude_column;
    
        String audio_column;
    
        String title_column;
    
        String radius_column;
        
        Constants(Context context) {
            records_table = context.getString(R.string.records_table);
            statis_objects_table = context.getString(R.string.static_objects_table);
            longitude_column = context.getString(R.string.longitude_column);
            latitude_column = context.getString(R.string.latitude_column);
            audio_column = context.getString(R.string.audio_filename_column);
            title_column = context.getString(R.string.title_column);
            radius_column = context.getString(R.string.raduis_column);
        }
    }

    public DataBaseContentProvider(final Activity context) {
        init(context);
    }

    private void init(final Activity context) {
        constants = new Constants(context);
        
        databaseHelper = new DatabaseHelper(context);
        try {
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRecordSetter(final RecordSetter recordSetter) {
        this.recordSetter = recordSetter;
    }

    public final void load() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        loadStaticPoints(database);
        loadRecords(database);
    }
    
    private void loadStaticPoints(SQLiteDatabase db) {
        Cursor c = db.query(constants.statis_objects_table, null, null, null,
                null, null, null);
        if (c.moveToFirst() && recordSetter != null) {
            int longColIndex = c.getColumnIndex(constants.longitude_column);
            int latColIndex = c.getColumnIndex(constants.latitude_column);
            int titleColIndex = c.getColumnIndex(constants.title_column);
            do {
                StaticPoint sp = new StaticPoint(c.getDouble(longColIndex),
                        c.getDouble(latColIndex), c.getString(titleColIndex));
                recordSetter.setStaticPoint(sp);
            } while (c.moveToNext());
        } 
        c.close();
    }
    
    private void loadRecords(SQLiteDatabase db) {
        Cursor c = db.query(constants.records_table, null, null, null, null, null,
                null);
        if (c.moveToFirst() && recordSetter != null) {
            int longColIndex = c.getColumnIndex(constants.longitude_column);
            int latColIndex = c.getColumnIndex(constants.latitude_column);
            int titleColIndex = c.getColumnIndex(constants.title_column);
            int diameterColIndex = c.getColumnIndex(constants.radius_column);
            int audioColIndex = c.getColumnIndex(constants.audio_column);
            do {
                Record r = new Record(c.getDouble(longColIndex),
                        c.getDouble(latColIndex), c.getInt(diameterColIndex),
                        c.getString(titleColIndex), c.getString(audioColIndex));
                recordSetter.setRecord(r);
            } while (c.moveToNext());
        } 
        c.close();
    }

}
