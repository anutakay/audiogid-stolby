package com.example.audiogid.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class ProximityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final  Intent intent) {
		final String key = LocationManager.KEY_PROXIMITY_ENTERING;
        final Boolean entering = intent.getBooleanExtra(key, false);
        final String title = intent.getExtras().getString("title");
        final String audio = intent.getExtras().getString("audio");
        final String snippet = intent.getExtras().getString("snippet");
        if (entering && title != null) {  	
        	processProximity(context, title, audio, snippet);
        }
	}
	
	private void processProximity(final Context context, final String title, final String audio, final String snippet ) {
		log(title, audio);
		createNotification(context, title, audio);
	}
	
	private void log(final String title, final String audio) {
		Log.d("Debug", "Мы приблизились к точке " + title + "\t" + audio);
	}
	
	private void createNotification(final Context context, final String title, final String audio) {
		NotificationUtils n = NotificationUtils.getInstance(context);
	    n.createProximityNotification(title, audio);
	}

}
