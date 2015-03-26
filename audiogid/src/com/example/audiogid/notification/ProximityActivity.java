package com.example.audiogid.notification;

import com.example.audiogid.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class ProximityActivity extends FragmentActivity {
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.notif_activity);
	     TextView title = (TextView) findViewById(R.id.title);
	     title.setText(getIntent().getStringExtra("title"));
	 }

}
