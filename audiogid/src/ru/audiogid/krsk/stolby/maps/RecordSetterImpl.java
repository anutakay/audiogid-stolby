package ru.audiogid.krsk.stolby.maps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.audiogid.krsk.stolby.R;
import ru.audiogid.krsk.stolby.model.Record;
import ru.audiogid.krsk.stolby.model.RecordSetter;
import ru.audiogid.krsk.stolby.model.StaticPoint;
import ru.audiogid.krsk.stolby.notification.ProximityReceiver;

public class RecordSetterImpl implements RecordSetter {
    
    public static final String PROXIMITY_DETECTED = "ru.audiogid.krsk.stolby.category.PROXIMITY";

    private Context context;
    
    private GoogleMap map;
    
    private GPSTracker tracker;
    
    private RecordMarkerTranslator translator;
    
    public RecordSetterImpl(Context context, GoogleMap map, GPSTracker tracker, 
                            RecordMarkerTranslator translator) {
        this.context = context;
        this.map = map;
        this.tracker = tracker;
        this.translator = translator;
    }
    
    @Override
    public void setRecord(final Record record) {
        Marker marker = map.addMarker(
                new MarkerOptions()
                        .position(new LatLng(record.getLat(), record.getLon()))
                        .title(record.getTitle()).snippet(record.getSnippet()));
        map.addCircle(
                new CircleOptions()
                        .center(new LatLng(record.getLat(), record.getLon()))
                        .radius(record.getRadius())
                        .fillColor(context.getResources().getColor(R.color.circle_color))
                        .strokeColor(context.getResources().getColor(R.color.border_color))
                        .strokeWidth(2));
        translator.add(marker, record);
        setProximityAlert(record);
    }

    @Override
    public void setStaticPoint(final StaticPoint point) {
        map.addMarker(
                new MarkerOptions()
                        .position(new LatLng(point.getLat(), point.getLon()))
                        .title(point.getTitle())
                        .snippet(point.getSnippet())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.mount)));
    }
    
    private void setProximityAlert(final Record record) {
        final Intent notificationIntent = getProximityIntent(record);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        tracker.getLocationManager().addProximityAlert(record.getLat(),
                record.getLon(), record.getRadius(), 1000000, pendingIntent);
    }

    private Intent getProximityIntent(final Record record) {
        final Intent notificationIntent = new Intent(context
                .getApplicationContext(), ProximityReceiver.class);
        notificationIntent.setAction(PROXIMITY_DETECTED + "_" + record.getLat()
                + "_" + record.getLon());
        notificationIntent.addCategory(PROXIMITY_DETECTED);
        notificationIntent.putExtra("title", record.getTitle());
        notificationIntent.putExtra("audio", record.getAudio());
        notificationIntent.putExtra("snippet", record.getSnippet());
        return notificationIntent;
    }
}
