package com.example.audiogid;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.audiogid.maps.AGMapFragment;
import com.example.audiogid.maps.IFakeProximityCreator;
import com.example.audiogid.maps.IProximityNotification;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends SavedFragmentActivity implements LocationSource, LocationListener {
	
	private AGMapFragment mapFragment;
	private GoogleMap mMap;
	private LocationManager locationManager;
	private OnLocationChangedListener locationListener;
	private IFakeProximityCreator detecter;
	private IProximityNotification proximityNotification;
	
    @Override
	public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        mapFragment = (AGMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment.getMap() == null) {
          finish();
          return;
        }
        detecter = this.mapFragment;
        mapFragment.init();	
        setUpMapIfNeeded();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates( locationManager.getBestProvider(new Criteria(), true), 1000, 1, this);
        mMap.setLocationSource(this);
        
        proximityNotification = mapFragment;
    }
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
	
	public void firstButtonClick(View v) {
		Intent intent = detecter.getFakeProximityIntent(1);
		Log.d("Debug", "firstButtonClick " + intent);
		sendBroadcast(intent);
	}
	
	public void secondButtonClick(View v) {
		Intent intent = detecter.getFakeProximityIntent(2);
		Log.d("Debug", "secondButtonClick " + intent);
		sendBroadcast(intent);
	}
}
