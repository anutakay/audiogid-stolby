package ru.audiogid.krsk.stolby.maps;

import ru.audiogid.krsk.stolby.audio.Player;
import ru.audiogid.krsk.stolby.model.RecordSetter;
import ru.audiogid.krsk.stolby.model.Record;
import ru.audiogid.krsk.stolby.sqlite.Constants;
import ru.audiogid.krsk.stolby.sqlite.DataBaseContentProvider;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapFragmentImpl extends SupportMapFragment implements
        MapFragment, ProximityNotificationListener {

    private GPSTracker tracker;

    private Player player;

    private boolean mapIsMoved = false;

    public boolean activeModePreference = false;
    
    private RecordMarkerTranslator translator = new RecordMarkerTranslator();

    private LocationListenerImpl locationListener;

    private View originalContentView;
    
    private RecordSetter recordSetter;
    
    public MapFragmentImpl() {
        super();
    }

    @Override
    public void init() {      
        locationListener = new LocationListenerImpl(getActivity()
                .getApplicationContext(), getMap());
        locationListener.locationModeOff();
        tracker = new GPSTracker(getActivity(), locationListener);
        if (tracker.canGetLocation()) {
            Log.d("debug", "Можно определить координаты " + tracker.getLatitude()
                    + " " + tracker.getLongitude());
        } else {
            Log.d("debug", "Нельзя определить координаты");
        }
        recordSetter = new RecordSetterImpl(getActivity(), getMap(), tracker, translator);
        toHomeLocation();
        getMap().getUiSettings().setZoomControlsEnabled(true);
        getMap().setInfoWindowAdapter(infoWindowAdapter);
        getMap().setOnInfoWindowClickListener(onInfoWindowClickListener);
        getMap().setOnMarkerClickListener(onMarkerClickListener);
        getMap().setOnMapClickListener(onMapClickListener);
        getMap().setOnCameraChangeListener(mOnCameraChangeListener);
        final DataBaseContentProvider provider = new DataBaseContentProvider(
                getActivity());
        provider.setRecordSetter(recordSetter);
        provider.load();
    }

    @Override
    public void toHomeLocation() {
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Constants.HOME_LAT, Constants.HOME_LON))
                .zoom(12).build();

        final CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        getMap().moveCamera(cameraUpdate);
    }

    @Override
    public void setPlayer(final Player player) {
        this.player = player;
    }

    private OnMapClickListener onMapClickListener = new OnMapClickListener() {

        @Override
        public void onMapClick(final LatLng arg0) {
            player.hideOverlay();
        }
    };

    private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(final Marker marker) {
            if (translator.contains(marker)) {
                playMarkerAudio(marker, false, false);
                return false;
            } else {
                return false;
            }
        }
    };

    private OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(final Marker marker) {
            player.doPlayPause();
        }
    };

    private final OnCameraChangeListener mOnCameraChangeListener = new OnCameraChangeListener() {
        @Override
        public void onCameraChange(final CameraPosition cameraPosition) {
            if (mapIsMoved) {
                Log.d("Debug", "Камеру переместили прикосновением");
                mapIsMoved = false;
            }
        }
    };

    private Marker showInfoWindow(final String snippet) {
        Marker marker = translator.getMarkerBySnippet(snippet);
        marker.showInfoWindow();
        return marker;
    }

    private void playMarkerAudio(final Marker marker, final boolean playNow, final boolean jingle) {
        Record r = translator.getRecordByMarker(marker);
        if (r.getAudio() != null) {
            player.setAudio(r.getAudio(), playNow, jingle);
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
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup parent, Bundle savedInstanceState) {
        originalContentView = super.onCreateView(inflater, parent,
                savedInstanceState);

        TouchableWrapper mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(originalContentView);

        return mTouchView;
    }

    @Override
    public View getView() {
        return originalContentView;
    }

    private class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(final Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(final MotionEvent ev) {
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                mapIsMoved = true;
                break;
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public void onProximity(final String snippet) {
        Marker marker = showInfoWindow(snippet);
        playMarkerAudio(marker, this.activeModePreference, true);
    }
    
    @Override
    public void onProximityNotificationClick(String snippet) {
        Marker marker = showInfoWindow(snippet);
        playMarkerAudio(marker, true, false);
    }

    
}