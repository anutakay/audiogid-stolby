package com.example.audiogid.audio;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class Player implements MediaPlayerControl {
	
	private MediaPlayer mMediaPlayer;
	private MediaController mMediaController;
	private Context mContext;
    private Handler mHandler = new Handler();
	
	public Player(Context context, View anchorView){
		mContext = context;
		mMediaController = new MediaController(context){
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent event)
	        {
	            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
	            	destroy();
	            	((Activity) mContext).finish();
	            	return true;
	            }

	            return super.dispatchKeyEvent(event);
	        }
			
			@Override
            public void hide() {
                super.show();
            }
		};;
		mMediaController.setMediaPlayer(this);
		mMediaController.setAnchorView(anchorView);
		setMediaPlayer(new MediaPlayer());
        
	}
	
	private void setMediaPlayer(MediaPlayer mediaPlayer){
		mMediaPlayer = mediaPlayer;
		
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                    mHandler.post(new Runnable() {
                            public void run() {
                            		mMediaPlayer.start();
                            		mMediaController.show(0);
                            }
                    });
            }
        });
		
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
        public void onCompletion(MediaPlayer mp) {
            mp.release();
            mp = null;
            Log.d("Debug", "Media player has completed playing");

        }
    });
	}
	
public void playAudio(String audioFile){
		
	if(mMediaPlayer == null){
		return;
	}
		mMediaPlayer.reset();
		
		AssetFileDescriptor afd;
        try {
			afd = ((Context)mContext).getAssets().openFd(audioFile);

			mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
			mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("PlayAudioDemo", "Could not open file " + audioFile + " for playback.", e);
        }
	}
	
	@Override
	public void start() {
		if(mMediaPlayer == null){
			return;
		}
		mMediaPlayer.start();
	}

	@Override
	public void pause() {
		if(mMediaPlayer == null){
			return;
		}
		mMediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		if(mMediaPlayer == null){
			return 0;
		}
		return mMediaPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		
		if(mMediaPlayer == null){
			return 0;
		}
		
			return mMediaPlayer.getCurrentPosition();

	}

	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		if(mMediaPlayer == null){
			return;
		}
		mMediaPlayer.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		if(mMediaPlayer == null){
			return false;
		}
		// TODO Auto-generated method stub
		return mMediaPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		if(mMediaPlayer == null){
			return 0;
		}
		int pos =  mMediaPlayer.getCurrentPosition();
		if(pos == 0){
			return 0;
		}
		return 100*mMediaPlayer.getDuration()/pos;
	}

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	/*@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return mMediaPlayer.getAudioSessionId();
	}*/
	
	public void destroy(){
		if(mMediaPlayer == null){
			return;
		}
		mMediaPlayer.stop();
		mMediaPlayer.release();
		mMediaPlayer = null;
	}
}
