package ru.audiogid.krsk.stolby.maps;

import ru.audiogid.krsk.stolby.notification.NotificationUtils;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MLocationListener implements LocationListener, ILocationModeSwitch {

    private boolean mLocationModeOn = true;

    private Context mContext;

    GoogleMap mMap;

    public MLocationListener(final Context context, final GoogleMap map) {
        mContext = context;
        mMap = map;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Debug", "Теперь мы в точке " + location);
        if (mLocationModeOn) {
            moveCameraToCurrentLocation(location);
        }
    }

    private void moveCameraToCurrentLocation(Location location) {
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location
                        .getLongitude())).zoom(mMap.getCameraPosition().zoom)
                .build();

        final CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
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

    @SuppressWarnings("unused")
    private void createNotification() {
        NotificationUtils n = NotificationUtils.getInstance(mContext);
        n.createInfoNotification("prox notification");
    }

    @Override
    public void locationModeOn() {
        mLocationModeOn = true;
    }

    @Override
    public void locationModeOff() {
        mLocationModeOn = false;
    }

    @Override
    public boolean isLocationModeOn() {
        return mLocationModeOn;
    }
}
