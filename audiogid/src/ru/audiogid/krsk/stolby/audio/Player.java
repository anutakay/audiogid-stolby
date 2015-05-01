package ru.audiogid.krsk.stolby.audio;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;

public class Player implements MediaPlayerControl {
	
	private MediaPlayer mMediaPlayer;
	private MediaController mMediaController;
	private Context mContext;
    private Handler mHandler = new Handler();
	
	public Player(Context context, RelativeLayout anchorView) {
		mContext = context;
		mMediaController = new MediaController(context);
		mMediaController.setMediaPlayer(this);
		mMediaController.setAnchorView(anchorView);
		setMediaPlayer(new MediaPlayer());
        
	}

	private void setMediaPlayer(MediaPlayer mediaPlayer) {
		mMediaPlayer = mediaPlayer;
		
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            	mHandler.post(new Runnable() {
            		public void run() {
            			mMediaPlayer.start();
            			mMediaController.show();
            	 	}
            	});
            }
        });
	}
	
public void playAudio(String audioFile) {
		
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
		mMediaPlayer.start();
	}

	@Override
	public void pause() {
		mMediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return mMediaPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		mMediaPlayer.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return mMediaPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
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
	
	public void destroy(){
		mMediaPlayer.stop();
		mMediaPlayer.release();
	}
	
	public void hide() {
		if(mMediaController != null) {
			mMediaController.hide();
		}
		
	}
}
