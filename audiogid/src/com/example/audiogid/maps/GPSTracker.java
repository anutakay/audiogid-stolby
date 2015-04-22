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
        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
        getLocation();
        Log.d("debug", "Сервис запущен");
    }
    
    public GPSTracker(final Context context, final LocationListener listener) {
    	this(context);
    	locationListener = listener;
    }
    
    public Location getLocation() {
    	locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
        try {
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
 
        } catch (final Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }

	@Override
	public void onLocationChanged(final Location location) {
		if (locationListener != null) {
			locationListener.onLocationChanged(location);
		}
	}

	@Override
	public void onStatusChanged(final String provider, final  int status, final  Bundle extras) {
		if (locationListener != null) {
			locationListener.onStatusChanged(provider, status, extras);
		}
	}

	@Override
	public void onProviderEnabled(final String provider) {
		if (locationListener != null) {
			locationListener.onProviderEnabled(provider);
		}
	}

	@Override
	public void onProviderDisabled(final String provider) {
		if (locationListener != null) {
			locationListener.onProviderDisabled(provider);
		}
	}

	@Override
	public IBinder onBind(final Intent intent) {
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
    
    public void stopUsingGPS() {
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }      
    }
    
    public LocationManager getLocationManager() {
    	locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
    	return locationManager;
    }
}