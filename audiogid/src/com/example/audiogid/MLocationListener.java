package com.example.audiogid;

import com.example.audiogid.notification.NotificationUtils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MLocationListener implements LocationListener {
	
	private Context mContext;
	
	public MLocationListener(Context context){
		mContext = context;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		//Log.d("debug", ""+arg0);
		//createNotification();
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
