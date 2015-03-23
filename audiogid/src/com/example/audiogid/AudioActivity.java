package com.example.audiogid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AudioActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = this.getIntent();
        if(intent != null && intent.hasExtra("point_title")) {
        	String title = intent.getExtras().getString("point_title");
        	this.setTitle(title);
        }
	}
}
