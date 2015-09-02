package ru.audiogid.krsk.stolby;

import ru.audiogid.krsk.stolby.audio.PlayerImpl;
import ru.audiogid.krsk.stolby.maps.MapFragmentImpl;
import ru.audiogid.krsk.stolby.maps.ProximityNotificationListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends SavedFragmentActivity {

    private MapFragmentImpl mapFragment;
    
    private GoogleMap map;
    
    private LocationManager locationManager;
    
    private LocationSource locationSource;
    
    private LocationListener locationListener;
    
    private ProximityNotificationListener proximityNotification;

    private PlayerImpl player;
    
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

        mapFragment = (MapFragmentImpl) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment.getMap() == null) {
            finish();
            return;
        }
        mapFragment.init();
        setUpMapIfNeeded();
        
        LocationListenerImpl lli = new LocationListenerImpl();
        locationListener = lli;
        locationSource = lli;
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                locationManager.getBestProvider(new Criteria(), true), 1000, 1,
                locationListener);
        map.setLocationSource(locationSource);

        proximityNotification = mapFragment;

        player = new PlayerImpl(this, (RelativeLayout) findViewById(R.id.mainView));
        mapFragment.setPlayer(player);
        
       
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        this.getPrefs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.destroy();
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
                this.proximityNotification.onProximityNotificationClick(snippet);
            } else {
                this.proximityNotification.onProximity(snippet);
            }
        }   
    }

    public void onClick(final View b) {
        mapFragment.toHomeLocation();
    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        mapFragment.activeModePreference = prefs.getBoolean("active_mode",
                false);
    }
    
    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            map.setMyLocationEnabled(true);
        }
    }

}