package ru.audiogid.krsk.stolby.maps;
import ru.audiogid.krsk.stolby.R;

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
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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
        IAGMapFragment, IProximityNotification, IRecordSetter, IFakeProximityCreator {

    public static final String PROXIMITY_DETECTED = "ru.audiogid.krsk.stolby.category.PROXIMITY";
	
	private Record firstRecord;
	private Record secondRecord;
	
	@Override
    public Intent getFakeProximityIntent(int n) {
        Intent intent;
        if(n == 1) {
            intent = getProximityIntent(firstRecord);
        } else {
            intent = getProximityIntent(secondRecord); 
        }
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
        return intent;
    }

    private GPSTracker mGPS;

    private IPlayer mPlayer;

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
        mGPS = new GPSTracker(getActivity(), mLocationListener);
        if (mGPS.canGetLocation()) {
            Log.d("debug", "Можно определить координаты " + mGPS.getLatitude()
                    + " " + mGPS.getLongitude());
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
                .target(new LatLng(Constants.HOME_LAT, Constants.HOME_LON))
                .zoom(12).build();

        final CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        getMap().moveCamera(cameraUpdate);
    }

    @Override
    public void setPlayer(final IPlayer player) {
        this.mPlayer = player;
    }

    private OnMapClickListener onMapClickListener = new OnMapClickListener() {

        @Override
        public void onMapClick(final LatLng arg0) {
            mPlayer.hideOverlay();
        }
    };

    private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(final Marker marker) {
            if (markerMap.containsValue(marker)) {
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
            mPlayer.doPlayPause();
        }
    };

    private final OnCameraChangeListener mOnCameraChangeListener = new OnCameraChangeListener() {
        @Override
        public void onCameraChange(final CameraPosition cameraPosition) {
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

    private void playMarkerAudio(final Marker marker, final boolean playNow, final boolean jingle) {
        Record r = recordMap.get(marker.getId());
        if (r.getAudio() != null) {
            mPlayer.setAudio(r.getAudio(), playNow, jingle);
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
                mMapIsMoved = true;
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
        playMarkerAudio(marker, false, false);
    }

    private void setProximityAlert(final Record record) {
        final Intent notificationIntent = getProximityIntent(record);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mGPS.getLocationManager().addProximityAlert(record.getLat(),
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
        if(firstRecord == null) {
            firstRecord = record;
        } else if(secondRecord == null) {
            secondRecord = record;
        }
    }

    @Override
    public void setStaticPoint(final StaticPoint point) {
        getMap().addMarker(
                new MarkerOptions()
                        .position(new LatLng(point.getLat(), point.getLon()))
                        .title(point.getTitle())
                        .snippet(point.getSnippet())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.mount)));
    }    
}
