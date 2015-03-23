package com.example.audiogid.maps;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.audiogid.GPSTracker;
import com.example.audiogid.MLocationListener;
import com.example.audiogid.R;
import com.example.audiogid.notif.ProximityReceiver;
import com.example.audiogid.sqlite.DataBaseContentProvider;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AGMapFragment extends SupportMapFragment {
	
	 public static final String PROXIMITY_DETECTED = "com.example.audiogid.category.PROXIMITY";
	 private GPSTracker gps;
	 Marker currentMarker;
	 DataBaseContentProvider provider;
	
	 public void init(){
		gps = new GPSTracker(getActivity(), (LocationListener) new MLocationListener(getActivity().getApplicationContext()));
    	if(gps.canGetLocation()){
    		Log.d("debug", "Можно определить координаты " + gps.getLatitude() + " " + gps.getLongitude());
    	} else {
    		Log.d("debug", "Нельзя определить координаты");
    	}
    	toHomeLocation();
    	
    	
    	getMap().getUiSettings().setZoomControlsEnabled(true);
    	getMap().setInfoWindowAdapter(new InfoWindowAdapter() {

    	      @Override
    	      public View getInfoWindow(Marker marker) {
    	    	  currentMarker = marker;
    	    	  View v  = getActivity().getLayoutInflater().inflate(R.layout.infowindow_layout, null, false);
      	          TextView title = (TextView) v.findViewById(R.id.title);
      	          title.setText(marker.getTitle());
    	    	  return v;

    	      }

    	      @Override
    	      public View getInfoContents(Marker marker) {
    	        
    	    	  return null;
    	      }
    	    });
    	 
    	 getMap().setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

    	        @Override
    	        public void onInfoWindowClick(Marker marker) {
    	        	 
    	        }
    	    });
    	  provider = new DataBaseContentProvider(getActivity()){

			@Override
			protected void setRecord(double lng, double lat, String filename,
					int d) {
				// TODO Auto-generated method stub
				AGMapFragment.this.setRecord(lng, lat, filename, d);
			}};
			getData();
	}
	 
	 private void getData() {
		// TODO Auto-generated method stub
		provider.getData();
	}

	private void toHomeLocation() {
		// TODO Auto-generated method stub
		
	}

	public void setRecord(double lng, double lat, String filename, int d){

		getMap().addMarker(new MarkerOptions().position(
				new LatLng(lat, lng))
    	        .title(filename));
    	getMap().addCircle(new CircleOptions()
    						.center(new LatLng(lat, lng))
    						.radius(d)
    						.fillColor(0x300099cc)
    						.strokeColor(0xff0099cc)
    						.strokeWidth(2));
    	 Intent notificationIntent = new Intent(getActivity().getApplicationContext(), ProximityReceiver.class); 
    	 notificationIntent.setAction(PROXIMITY_DETECTED + "_" + lat + "_" + lng);
    	 notificationIntent.addCategory(PROXIMITY_DETECTED);
    	 notificationIntent.putExtra("title", filename);
    	 PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    	 gps.getLocationManager().addProximityAlert(lat, lng, d, -1, pendingIntent);
		
	}

}
