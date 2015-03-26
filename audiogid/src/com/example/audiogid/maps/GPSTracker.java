package com.example.audiogid.maps;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {
	
	private LocationListener locationListener;
	 
    private final Context context;
 
    private boolean isGPSEnabled = false;
 
    private boolean isNetworkEnabled = false;
 
    private boolean canGetLocation = false;
 
    private Location location; 
    
    private double latitude; 
    
    private double longitude; 
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; 
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; 
 
    protected LocationManager locationManager;
 
    private GPSTracker(final Context context) {
        this.context = context;
        getLocation();
        Log.d("debug", "Сервис запущен");
    }
    
    public GPSTracker(Context context, LocationListener listener){
    	this(context);
    	locationListener = listener;
    }
    
    public Location getLocation() {
        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);
 
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1,
                                1, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }

	@Override
	public void onLocationChanged(Location location) {
		locationListener.onLocationChanged(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		locationListener.onStatusChanged(provider, status, extras);	
	}

	@Override
	public void onProviderEnabled(String provider) {
		locationListener.onProviderEnabled(provider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		locationListener.onProviderDisabled(provider);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    public double getLatitude() {
        if(location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }
    
    public double getLongitude() {
        if(location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }
    
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
      
        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
  
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        alertDialog.show();
    }
    
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }      
    }
    
    public LocationManager getLocationManager(){
    	return locationManager;
    }
}