package ru.audiogid.krsk.stolby.notification;

import ru.audiogid.krsk.stolby.AudiogidApp;
import ru.audiogid.krsk.stolby.MainActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class ProximityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final  Intent intent) {
		Log.d("Debug", "onReceive " + intent);
		final String key = LocationManager.KEY_PROXIMITY_ENTERING;

        final Boolean entering = intent.getBooleanExtra(key, false);
        final String title = intent.getExtras().getString("title");
        final String audio = intent.getExtras().getString("audio");
        final String snippet = intent.getExtras().getString("snippet");
        if (entering && title != null) {
            processProximity(context, title, audio, snippet);
        }
    }

    private void processProximity(final Context context, final String title,
            final String audio, final String snippet) {
        log(title, audio);
        createNotification(context, title, audio);
        //notifyInMap(context, snippet);
    }

    private void log(final String title, final String audio) {
        Log.d("Debug", "Мы приблизились к точке " + title + "\t" + audio);
    }

    private void notifyInMap(final Context context, final String snippet) {
        Activity currentActivity = ((AudiogidApp) context
                .getApplicationContext()).getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        currentActivity.startActivity(this.createIntent(currentActivity,
                snippet));
        Log.d("Debug", "Intent в Activity отправлен");
    }

    private Intent createIntent(final Context context, final String snippet) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("snippet", snippet);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    @SuppressWarnings("unused")
    private void createNotification(final Context context, final String title,
            final String audio) {
        NotificationUtils n = NotificationUtils.getInstance(context);
        n.createProximityNotification(title, audio);
    }

}
