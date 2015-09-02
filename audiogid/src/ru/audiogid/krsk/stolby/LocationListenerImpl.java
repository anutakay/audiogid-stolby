package ru.audiogid.krsk.stolby;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.LocationSource;

public class LocationListenerImpl implements LocationListener, LocationSource {
    
    private OnLocationChangedListener locationChangedListener;
    
    @Override
    public void onLocationChanged(final Location location) {
        if (locationChangedListener != null) {
            locationChangedListener.onLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(final String provider, final int status, Bundle extras) {        
    }

    @Override
    public void onProviderEnabled(final String provider) {
    }

    @Override
    public void onProviderDisabled(final String provider) {
    }
    
    @Override
    public void activate(final OnLocationChangedListener listener) {
        Log.d("Debug", "activate");
        locationChangedListener = listener;
    }

    @Override
    public void deactivate() {
        Log.d("Debug", "deactivate");
        locationChangedListener = null;
    }
}
