package ru.audiogid.krsk.stolby.maps;

import java.util.HashMap;
import java.util.Map;

import ru.audiogid.krsk.stolby.audio.IPlayer;
import ru.audiogid.krsk.stolby.model.IRecordSetter;
import ru.audiogid.krsk.stolby.model.Record;
import ru.audiogid.krsk.stolby.model.StaticPoint;
import ru.audiogid.krsk.stolby.notification.ProximityReceiver;
import ru.audiogid.krsk.stolby.sqlite.Constants;
import ru.audiogid.krsk.stolby.sqlite.DataBaseContentProvider;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import ru.audiogid.krsk.stolby.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AGMapFragment extends SupportMapFragment implements 
        IAGMapFragment, IProximityNotification, IRecordSetter {

    public static final String PROXIMITY_DETECTED = "ru.audiogid.krsk.stolby.category.PROXIMITY";

    private GPSTracker gps;

    private IPlayer player;
    
    private boolean mMapIsMoved = false;

    public boolean activeModePreference = false;

    // По id маркера можно получить запись, айди можно получить из маркера.
    private Map<String, Record> recordMap = new HashMap<String, Record>();

    // По сниппету можно получить маркер, сниппет можно получить по записи
    private Map<String, Marker> markerMap = new HashMap<String, Marker>();

    private MLocationListener mLocationListener;
    
    private View mOriginalContentView;
  
    @Override
    public void init() {
        mLocationListener = new MLocationListener(getActivity()
                .getApplicationContext(), getMap());
        mLocationListener.locationModeOff();
        gps = new GPSTracker(getActivity(), mLocationListener);
        if (gps.canGetLocation()) {
            Log.d("debug", "Можно определить координаты " + gps.getLatitude()
                    + " " + gps.getLongitude());
        } else {
            Log.d("debug", "Нельзя определить координаты");
        }
        toHomeLocation();
        getMap().getUiSettings().setZoomControlsEnabled(true);
        getMap().setInfoWindowAdapter(infoWindowAdapter);
        getMap().setOnInfoWindowClickListener(onInfoWindowClickListener);
        getMap().setOnMarkerClickListener(onMarkerClickListener);
        getMap().setOnMapClickListener(onMapClickListener);
        getMap().setOnCameraChangeListener(mOnCameraChangeListener);
        final DataBaseContentProvider provider = new DataBaseContentProvider(
                getActivity());
        provider.setRecordSetter(this);
        provider.getData();
    }
    
    @Override
    public void toHomeLocation() {
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Constants.HOME_LAT, Constants.HOME_LON)).zoom(12).build();

        final CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        getMap().moveCamera(cameraUpdate);
    }
    
    @Override
    public void setPlayer(IPlayer player) {
        this.player = player;
    }

    private OnMapClickListener onMapClickListener = new OnMapClickListener() {

        @Override
        public void onMapClick(LatLng arg0) {
            player.hideOverlay();
        }
    };
    
    private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (markerMap.containsValue(marker)) {
                playMarkerAudio(marker, false);
                return false;
            } else {
                return false;
            }
        }
    };

    private OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(final Marker marker) {
            Log.d("Debug", "onInfoWindowClick " + marker);
            player.doPlayPause();
        }
    };
    
    private final OnCameraChangeListener mOnCameraChangeListener = new OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            if (mMapIsMoved) {
                Log.d("Debug", "Камеру переместили прикосновением");
                mMapIsMoved = false;
            }
        }
    };

    private Marker showInfoWindow(final String snippet) {
        Marker marker = markerMap.get(snippet);
        marker.showInfoWindow();
        return marker;
    }
    
    private void playMarkerAudio(final Marker marker, final boolean playNow) {
        Record r = recordMap.get(marker.getId());
        if (r.getAudio() != null) {
            player.setAudio(r.getAudio(), playNow);
        }
    }

    @SuppressLint("InflateParams")
    private InfoWindowAdapter infoWindowAdapter = new InfoWindowAdapter() {

        @Override
        public View getInfoWindow(final Marker marker) {
            final View v = getActivity().getLayoutInflater().inflate(
                    R.layout.infowindow_layout, null, false);
            TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(marker.getTitle());
            return v;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            return null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent,
                savedInstanceState);

        TouchableWrapper mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);

        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }
    
    private class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_MOVE:
                mMapIsMoved = true;
                break;
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public void onProximity(String snippet) {
        Marker marker = showInfoWindow(snippet);
        playMarkerAudio(marker, this.activeModePreference);
    } 
       
    private void setProximityAlert(final Record record) {
        final Intent notificationIntent = getProximityIntent(record);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        gps.getLocationManager().addProximityAlert(record.getLat(),
                record.getLon(), record.getRadius(), 1000000, pendingIntent);
    }

    private Intent getProximityIntent(final Record record) {
        final Intent notificationIntent = new Intent(getActivity()
                .getApplicationContext(), ProximityReceiver.class);
        notificationIntent.setAction(PROXIMITY_DETECTED + "_" + record.getLat()
                + "_" + record.getLon());
        notificationIntent.addCategory(PROXIMITY_DETECTED);
        notificationIntent.putExtra("title", record.getTitle());
        notificationIntent.putExtra("audio", record.getAudio());
        notificationIntent.putExtra("snippet", record.getSnippet());
        return notificationIntent;
    }
    
    @Override
    public void setRecord(final Record record) {
        Marker m = getMap().addMarker(
                new MarkerOptions()
                        .position(new LatLng(record.getLat(), record.getLon()))
                        .title(record.getTitle()).snippet(record.getSnippet()));
        getMap().addCircle(
                new CircleOptions()
                        .center(new LatLng(record.getLat(), record.getLon()))
                        .radius(record.getRadius()).fillColor(0x300099cc)
                        .strokeColor(0xff0099cc).strokeWidth(2));
        recordMap.put(m.getId(), record);
        markerMap.put(m.getSnippet(), m);
        setProximityAlert(record);
    }

    @Override
    public void setStaticPoint(StaticPoint point) {
        getMap().addMarker(
                new MarkerOptions()
                        .position(new LatLng(point.getLat(), point.getLon()))
                        .title(point.getTitle())
                        .snippet(point.getSnippet())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.mount)));
    }    
}