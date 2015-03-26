package com.example.audiogid.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class ProximityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final String key = LocationManager.KEY_PROXIMITY_ENTERING;
        final Boolean entering = intent.getBooleanExtra(key, false);
        String title = intent.getExtras().getString("title");
        Log.d("debug", "title " + title);
        if(entering&&title!=null){
        	createNotification(context, title);
        }
	}
	
	private void createNotification(Context context, String title){
		NotificationUtils n = NotificationUtils.getInstance(context);
	    n.createProximityNotification(title);
	}

}
