package ru.audiogid.krsk.stolby.maps;

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

    private LocationListener mLocationListener;

    private final Context mContext;

    private boolean isGPSEnabled = false;

    private boolean isNetworkEnabled = false;

    private boolean canGetLocation = false;

    private Location mLocation;

    private double mLatitude;

    private double mLongitude;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager mLocationManager;

    private GPSTracker(final Context context) {
        this.mContext = context;
        mLocationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
        getLocation();
        Log.d("debug", "Сервис запущен");
    }

    public GPSTracker(final Context context, final LocationListener listener) {
        this(context);
        mLocationListener = listener;
    }

    public Location getLocation() {
        mLocationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);
        try {
            isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (mLocationManager != null) {
                        mLocation = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (mLocation == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 1, 1, this);
                        if (mLocationManager != null) {
                            mLocation = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                mLatitude = mLocation.getLatitude();
                                mLongitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return mLocation;
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (mLocationListener != null) {
            mLocationListener.onLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(final String provider, final int status,
            final Bundle extras) {
        if (mLocationListener != null) {
            mLocationListener.onStatusChanged(provider, status, extras);
        }
    }

    @Override
    public void onProviderEnabled(final String provider) {
        if (mLocationListener != null) {
            mLocationListener.onProviderEnabled(provider);
        }
    }

    @Override
    public void onProviderDisabled(final String provider) {
        if (mLocationListener != null) {
            mLocationListener.onProviderDisabled(provider);
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    public double getLatitude() {
        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }

    public double getLongitude() {
        if (mLocation != null) {
            mLongitude = mLocation.getLongitude();
        }
        return mLongitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS is settings");

        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void stopUsingGPS() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(GPSTracker.this);
        }
    }

    public LocationManager getLocationManager() {
        mLocationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);
        return mLocationManager;
    }
}