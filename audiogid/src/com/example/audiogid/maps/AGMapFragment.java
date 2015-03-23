package com.example.audiogid.maps;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.audiogid.AudioActivity;
import com.example.audiogid.GPSTracker;
import com.example.audiogid.IRecordSetter;
import com.example.audiogid.MLocationListener;
import com.example.audiogid.R;
import com.example.audiogid.model.Record;
import com.example.audiogid.notif.ProximityReceiver;
import com.example.audiogid.sqlite.DataBaseContentProvider;
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

public class AGMapFragment extends SupportMapFragment implements IRecordSetter {

	private static final double HOME_LON = 92.738501;
	
	private static final double HOME_LAT = 55.975218;
	
	public static final String PROXIMITY_DETECTED = "com.example.audiogid.category.PROXIMITY";
	 
	private GPSTracker gps;
	
	private Map<String, Record> recordMap = new HashMap<String, Record>();
	 
	@SuppressWarnings("unused")
	private Marker currentMarker;	 
	
	public void init(){
		gps = new GPSTracker(getActivity(), (LocationListener) new MLocationListener(getActivity().getApplicationContext()));
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
		
		Intent intent = new Intent(this.getActivity(), AudioActivity.class);
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
	public void setRecord(double lon, double lat, final String title, final int radius){
		Marker m = getMap().addMarker(new MarkerOptions().position(
				new LatLng(lat, lon))
    	        .title(title));
    	getMap().addCircle(new CircleOptions()
    						.center(new LatLng(lat, lon))
    						.radius(radius)
    						.fillColor(0x300099cc)
    						.strokeColor(0xff0099cc)
    						.strokeWidth(2));
    	setProximityAlert(lon, lat, title, radius);	
    	
    	Record record = new Record(lon, lat, title, "example.mp3");
    	recordMap.put(m.getId(), record);
	}

	private void setProximityAlert(final double lng, final double lat, final String title,
			int radius) {
		final Intent notificationIntent = new Intent(getActivity().getApplicationContext(), ProximityReceiver.class); 
    	notificationIntent.setAction(PROXIMITY_DETECTED + "_" + lat + "_" + lng);
    	notificationIntent.addCategory(PROXIMITY_DETECTED);
    	notificationIntent.putExtra("title", title);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    	gps.getLocationManager().addProximityAlert(lat, lng, radius, -1, pendingIntent);
	}
}