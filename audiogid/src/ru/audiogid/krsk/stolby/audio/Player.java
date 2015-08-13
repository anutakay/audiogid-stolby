package ru.audiogid.krsk.stolby.audio;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;

public class Player implements MediaPlayerControl, IPlayer, OnAudioFocusChangeListener, Jingled {
	
	private Context mContext;
	
	private MediaPlayer mMediaPlayer;
	
	private MediaController mMediaController;
	
	private AudioManager mAudioManager;
	
    private Handler mHandler = new Handler();
    
    private IPlayer mJinglePlayer;
    
    private boolean jingleBegin, jingleEnd;
	
	public Player(Context context, RelativeLayout anchorView) {
		mContext = context;
		mMediaController = new MediaController(context);
		mMediaController.setMediaPlayer(this);
		mMediaController.setAnchorView(anchorView);
		setMediaPlayer(new MediaPlayer());
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        
	}

	private void setMediaPlayer(MediaPlayer mediaPlayer) {
		mMediaPlayer = mediaPlayer;
	}
	
	OnPreparedListener onPreparedListenerPlayNow = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
        	mHandler.post(new Runnable() {
        		public void run() {
        			start();
        			mMediaController.show();
        	 	}
        	});
        }
    };
    
    OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
        	mHandler.post(new Runnable() {
        		public void run() {
        			mMediaController.show();
        	 	}
        	});
        }
    };
    
    OnCompletionListener onCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			mAudioManager.abandonAudioFocus(Player.this);
			mMediaController.freezeButton();
		}
    	
    };
	
    @Override
    public void setAudio(String audioFile, boolean playNow) {
		
		mMediaPlayer.reset();
		if(playNow) {
			mMediaPlayer.setOnPreparedListener(onPreparedListenerPlayNow);
		} else {
			mMediaPlayer.setOnPreparedListener(onPreparedListener);
		}
		mMediaPlayer.setOnCompletionListener(onCompletionListener);
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
		int requestResult = mAudioManager.requestAudioFocus(this,
		        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if(requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			mMediaPlayer.start();
		} else {
			Log.d("Debug", "Невозможно получить фокус ");
		}
	}

	@Override
	public void pause() {
		mAudioManager.abandonAudioFocus(this);
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
	
	public void destroy() {
		mAudioManager.abandonAudioFocus(this);
		mMediaPlayer.stop();
		mMediaPlayer.release();
	}
	
	@Override
	public void hideOverlay() {
		if(mMediaController != null) {
			mMediaController.hide();
		}
		if(this != null && this.isPlaying()) {
			this.pause();
		}
	}

	@Override
	public void doPlayPause() {
		if(mMediaController != null) {
			mMediaController.doPauseResume();
		}
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		Log.d("Debug", "Аудио фокус сменился");
	}

	@Override
	public void setJinglePlayer(IPlayer jinglePlayer) {
		mJinglePlayer = jinglePlayer;
	}

	@Override
	public IPlayer getJinglePlayer() {
		return mJinglePlayer;
	}

	@Override
	public void setJinglePlayMode(boolean begin, boolean end) {
		jingleBegin = begin;
		jingleEnd = end;
	}
}
