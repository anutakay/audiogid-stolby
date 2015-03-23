package com.example.audiogid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.audiogid.maps.AGMapFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity {
	
	AGMapFragment mapFragment;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mPlayer = new Player( this, findViewById(R.id.mainView));
        //mPlayer.setPlayerStatusChangeListener(this);
       
        mapFragment = (AGMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment.getMap() == null) {
          finish();
          return;
        }
        init();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
       // mPlayer.destroy();
    }
    private void init() {
    	mapFragment.init();
    	 
    }
    
    public void onClick(View b){
    	toHomeLocation();
    }
    
    private void toHomeLocation(){
    	CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(55.975218, 92.738501))
        .zoom(12)
        .build();
    	
    	CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
    	mapFragment.getMap().moveCamera(cameraUpdate);
    }
    
    protected void setRecord(double lng, double lat, String filename, int d){
    	mapFragment.setRecord(lng, lat, filename, d);
    }	
	
}