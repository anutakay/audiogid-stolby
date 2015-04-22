package com.example.audiogid.maps;

import com.example.audiogid.notification.NotificationUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MLocationListener implements LocationListener {
	
	private Context mContext;
	
	GoogleMap map;
	
	public MLocationListener(final Context context, final GoogleMap map) {
		mContext = context;
		this.map = map;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.d("Debug", "Теперь мы в точке " + location);
		//addMarker(location);
		//createNotification();
	}
	
	private void addMarker(final Location location) {
		map.addMarker(new MarkerOptions().position(
				new LatLng(location.getLatitude(), location.getLongitude()))
    	        .title("Мы сейчас"));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	
	 @SuppressWarnings("unused")
	private void createNotification(){
			NotificationUtils n = NotificationUtils.getInstance(mContext);
		    n.createInfoNotification("prox notification");
		}

}
