package ru.audiogid.krsk.stolby.maps;

import java.util.HashMap;
import java.util.Map;

import ru.audiogid.krsk.stolby.model.Record;

import com.google.android.gms.maps.model.Marker;

public class RecordMarkerTranslator {
    
    private Map<String, Record> records = new HashMap<String, Record>(); 
    
    private Map<String, Marker> markers = new HashMap<String, Marker>();
    
    public Record getRecordByMarker(Marker marker) {
        Record record = records.get(marker.getId());
        return record;
    }
    
    public Marker getMarkerByRecord(Record record) {
        String snippet = record.getSnippet();
        return getMarkerBySnippet(snippet);
    }
    
    public Marker getMarkerBySnippet(String snippet) {
        Marker marker = markers.get(snippet);
        return marker;
    }
    
    public void add(Marker marker, Record record) {
        records.put(marker.getId(), record);
        markers.put(record.getSnippet(), marker);
    }
    
    public boolean contains(Marker marker) {
        return markers.containsValue(marker);
    }
    
    public boolean contains(Record record) {
        return records.containsValue(record);
    }

}
