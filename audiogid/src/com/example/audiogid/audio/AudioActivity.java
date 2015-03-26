package com.example.audiogid.audio;

import com.example.audiogid.R;
import com.example.audiogid.R.id;
import com.example.audiogid.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AudioActivity extends Activity {
	
	private Player mPlayer;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = null;
        String audio = null;
        Intent intent = this.getIntent();
        if(intent != null) {
        	title = intent.getExtras().getString("point_title");
        	audio = intent.getExtras().getString("point_audio");
        	this.setTitle(title + " " + audio);
        }
        
        setContentView(R.layout.activity_audio);
		mPlayer = new Player( this, findViewById(R.id.mainView));
		play(audio);
	}
	
	private void play(final String audio) {
        mPlayer.playAudio(audio);
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.destroy();
    }

}
