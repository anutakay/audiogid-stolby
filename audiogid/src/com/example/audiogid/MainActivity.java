package com.example.audiogid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.audiogid.maps.AGMapFragment;

public class MainActivity extends FragmentActivity {
	
	AGMapFragment mapFragment;
	
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        mapFragment = (AGMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment.getMap() == null) {
          finish();
          return;
        }
        mapFragment.init();	
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    public void onClick(final View b){
    	mapFragment.toHomeLocation();
    }
}