package ru.audiogid.krsk.stolby;

import ru.audiogid.krsk.stolby.audio.PlayerImpl;
import ru.audiogid.krsk.stolby.maps.MapFragmentImpl;
import ru.audiogid.krsk.stolby.maps.ProximityNotificationListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends SavedFragmentActivity implements
        LocationSource, LocationListener {

    private MapFragmentImpl mMapFragment;
    
    private GoogleMap mMap;
    
    private LocationManager mLocationManager;
    
    private OnLocationChangedListener mLocationListener;
    
    private ProximityNotificationListener mProximityNotification;

    private PlayerImpl mPlayer;
    
    public boolean visibleOnScreen = false;
    @Override
    protected void onPause() {
        super.onPause();
        visibleOnScreen = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        visibleOnScreen = true;
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapFragment = (MapFragmentImpl) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mMapFragment.getMap() == null) {
            finish();
            return;
        }
        mMapFragment.init();
        setUpMapIfNeeded();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(
                mLocationManager.getBestProvider(new Criteria(), true), 1000, 1,
                this);
        mMap.setLocationSource(this);

        mProximityNotification = mMapFragment;

        mPlayer = new PlayerImpl(this, (RelativeLayout) findViewById(R.id.mainView));
        mMapFragment.setPlayer(mPlayer);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.getPrefs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.destroy();
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Preferences");
        mi.setIntent(new Intent(this, PrefActivity.class));
        return super.onCreateOptionsMenu(menu);
    }

    protected void onNewIntent(final Intent intent) {
        if(intent.hasExtra("snippet")) {
            final String snippet = intent.getExtras().getString("snippet");
            if(intent.hasExtra("audio")) {
                this.mProximityNotification.onProximityNotificationClick(snippet);
            } else {
                this.mProximityNotification.onProximity(snippet);
            }
        }
        
    }

    public void onClick(final View b) {
        mMapFragment.toHomeLocation();
    }

    @Override
    public void activate(final OnLocationChangedListener listener) {
        Log.d("Debug", "activate");
        mLocationListener = listener;
    }

    @Override
    public void deactivate() {
        Log.d("Debug", "deactivate");
        mLocationListener = null;
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (mLocationListener != null) {
            mLocationListener.onLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(final String provider, final int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(final String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderDisabled(final String provider) {
        // TODO Auto-generated method stub
    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        mMapFragment.activeModePreference = prefs.getBoolean("active_mode",
                false);
    }

}