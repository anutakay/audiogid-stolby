package ru.audiogid.krsk.stolby;

import ru.audiogid.krsk.stolby.audio.Player;
import ru.audiogid.krsk.stolby.maps.AGMapFragment;
import ru.audiogid.krsk.stolby.maps.IProximityNotification;
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

public class MainActivity extends SavedFragmentActivity implements LocationSource, LocationListener {
	
	private AGMapFragment mapFragment;
	private GoogleMap mMap;
	private LocationManager locationManager;
	private OnLocationChangedListener locationListener;
	private IProximityNotification proximityNotification;
	
	private Player mPlayer;
	
    @Override
	public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        mapFragment = (AGMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment.getMap() == null) {
          finish();
          return;
        }
        mapFragment.init();	
        setUpMapIfNeeded();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates( locationManager.getBestProvider(new Criteria(), true), 1000, 1, this);
        mMap.setLocationSource(this);
        
        proximityNotification = mapFragment;
        
        mPlayer = new Player( this, (RelativeLayout)findViewById(R.id.mainView));
        mapFragment.setPlayer(mPlayer);
    }
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
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
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Preferences");
        mi.setIntent(new Intent(this, PrefActivity.class));
        return super.onCreateOptionsMenu(menu);
      }
    
    protected void onNewIntent (final Intent intent) {
    	final String snippet = intent.getExtras().getString("snippet");
    	this.proximityNotification.onProximity(snippet);
    }
    
    public void onClick(final View b){
    	mapFragment.toHomeLocation();
    }

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		Log.d("Debug", "activate");
		 locationListener = listener;
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		Log.d("Debug", "deactivate");
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(locationListener != null){           
			locationListener.onLocationChanged(location);
       }
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
	
	private void getPrefs() {
        SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
        mapFragment.activeModePreference = prefs.getBoolean("active_mode", false);
    }

}