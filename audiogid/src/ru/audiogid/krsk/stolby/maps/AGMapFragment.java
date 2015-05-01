package ru.audiogid.krsk.stolby.maps;
import ru.audiogid.krsk.stolby.R;

import java.util.HashMap;
import java.util.Map;

import ru.audiogid.krsk.stolby.audio.AudioActivity;
import ru.audiogid.krsk.stolby.model.IRecordSetter;
import ru.audiogid.krsk.stolby.model.Record;
import ru.audiogid.krsk.stolby.notification.ProximityReceiver;
import ru.audiogid.krsk.stolby.sqlite.DataBaseContentProvider;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AGMapFragment extends SupportMapFragment implements IRecordSetter, IFakeProximityCreator, IProximityNotification {


	private static final Class<AudioActivity> AUDIO_ACTIVITY_CLASS = AudioActivity.class;

	private static final double HOME_LON = 92.738501;
	
	private static final double HOME_LAT = 55.975218;
	
	public static final String PROXIMITY_DETECTED = "com.example.audiogid.category.PROXIMITY";
	 
	private GPSTracker gps;
	
	//По id маркера можно получить запись, айди можно получить из маркера.
	private Map<String, Record> recordMap = new HashMap<String, Record>();
	
	//По сниппету можно получить маркер, сниппет можно получить по записи
	private Map<String, Marker> markerMap = new HashMap<String, Marker>();
	
	private Record firstRecord;
	private Record secondRecord;
	 
	@SuppressWarnings("unused")
	private Marker currentMarker;	 
	
	public void init(){
		gps = new GPSTracker(getActivity(), (LocationListener) new MLocationListener(getActivity().getApplicationContext(), getMap()));
    	if(gps.canGetLocation()) {
    		Log.d("debug", "Можно определить координаты " + gps.getLatitude() + " " + gps.getLongitude());
    	} else {
    		Log.d("debug", "Нельзя определить координаты");
    	}
    	toHomeLocation();
    	getMap().getUiSettings().setZoomControlsEnabled(true);
    	getMap().setInfoWindowAdapter(infoWindowAdapter);
    	getMap().setOnInfoWindowClickListener(onInfoWindowClickListener);
    	final DataBaseContentProvider provider = new DataBaseContentProvider(getActivity());
    	provider.setRecordSetter(this);
    	provider.getData();
	}
	
	private void showInfoWindow(final String snippet){
		Marker marker = markerMap.get(snippet);
		marker.showInfoWindow();
	}
	 
	@SuppressLint("InflateParams")
	InfoWindowAdapter infoWindowAdapter = new InfoWindowAdapter() {
		
	    @Override
	    public View getInfoWindow(final Marker marker) {
	    	currentMarker = marker;
	    	final View v  = getActivity().getLayoutInflater().inflate(R.layout.infowindow_layout, null, false);
 	        TextView title = (TextView) v.findViewById(R.id.title);
 	        title.setText(marker.getTitle());
	    	return v;
	    }

	    @Override
	    public View getInfoContents(final Marker marker) {    
	    	return null;
	    }
	};
	 
	OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {
		 
		@Override
	    public void onInfoWindowClick(final Marker marker) {
			showAudioActivity(marker.getId());
	    }
	};
	    
	private void showAudioActivity(final String id) {
		
		Record r = this.recordMap.get(id);
		
		Intent intent = new Intent(this.getActivity(), AUDIO_ACTIVITY_CLASS);
		intent.putExtra("point_title", r.getTitle());
		intent.putExtra("point_audio", r.getAudio());
	    startActivity(intent);
	}

	public void toHomeLocation() {
		final CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(HOME_LAT, HOME_LON))
        .zoom(12)
        .build();
    	
    	final CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
    	getMap().moveCamera(cameraUpdate);
	}
	
	@Override
	public void setRecord(final Record record) {
		if(record.getAudio() == null) {
			return;
		}
		Marker m = getMap().addMarker(new MarkerOptions().position(
				new LatLng(record.getLat(), record.getLon()))
    	        .title(record.getTitle())
    	        .snippet(record.getSnippet()));
    	getMap().addCircle(new CircleOptions()
    						.center(new LatLng(record.getLat(), record.getLon()))
    						.radius(record.getRadius())
    						.fillColor(0x300099cc)
    						.strokeColor(0xff0099cc)
    						.strokeWidth(2));
    	recordMap.put(m.getId(), record);
    	markerMap.put(m.getSnippet(), m);
    	setProximityAlert(record);
    	if(firstRecord == null) {
    		firstRecord = record;
    	} else if(secondRecord == null) {
    		secondRecord = record;
    	}
	}

	private void setProximityAlert(final Record record) {
		final Intent notificationIntent = getProximityIntent(record);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    	gps.getLocationManager().addProximityAlert(record.getLat(), record.getLon(), record.getRadius(), 1000000, pendingIntent);
    }
	
	private Intent getProximityIntent(final Record record) {
		final Intent notificationIntent = new Intent(getActivity().getApplicationContext(), ProximityReceiver.class); 
    	notificationIntent.setAction(PROXIMITY_DETECTED + "_" + record.getLat() + "_" + record.getLon());
    	notificationIntent.addCategory(PROXIMITY_DETECTED);
    	notificationIntent.putExtra("title", record.getTitle());
    	notificationIntent.putExtra("audio", record.getAudio());
    	notificationIntent.putExtra("snippet", record.getSnippet());
		return notificationIntent;
	}
	
	@Override
	public Intent getFakeProximityIntent(int n) {
		Intent intent;
		if(n == 1) {
			intent = getProximityIntent(firstRecord);
		} else {
			intent = getProximityIntent(secondRecord); 
		}
		intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
		return intent;
	}

	@Override
	public void onProximity(String snippet) {
		showInfoWindow(snippet);
	}
}
