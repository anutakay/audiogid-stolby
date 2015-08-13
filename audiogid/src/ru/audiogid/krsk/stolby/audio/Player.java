package ru.audiogid.krsk.stolby.audio;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.widget.RelativeLayout;

public class Player extends AudioFocusPlayer implements IPlayer {

    private Context mContext;

    private IMediaController mMediaController;
    
    private Handler mHandler = new Handler();

    public Player(final Context context, final RelativeLayout anchorView) {
        super(context);
        mContext = context;
        mMediaController = new MediaController(context);
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(anchorView);   
    }

    OnPreparedListener onPreparedListenerPlayNow = new OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mp) {
            mHandler.post(new Runnable() {
                public void run() {
                    start();
                    mMediaController.showOverlay();
                }
            });
        }
    };

    OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mp) {
            mHandler.post(new Runnable() {
                public void run() {
                    mMediaController.showOverlay();
                }
            });
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Player.this.onCompletion(mp);
        }
    };
    
    @Override
    protected void onCompletion(MediaPlayer mp) {
        super.onCompletion(mp);
        mMediaController.freezePausePlay();
    }

    @Override
    public void setAudio(final String audioFile, final boolean playNow) {
        mMediaController.unfreezePausePlay();
        AssetFileDescriptor afd = null;
        try {
            afd = getAFD(audioFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        OnPreparedListener pl = null;
        if (playNow) {
            pl = onPreparedListenerPlayNow;
        } else {
            pl = onPreparedListener;
        }
        super.setAudio(afd, pl, onCompletionListener);       
    }
      
    private AssetFileDescriptor getAFD(final String audioFile) throws IOException {
        return ((Context) mContext).getAssets().openFd(audioFile);
    } 
    
    @Override
    public void hideOverlay() {
        if (mMediaController != null) {
            mMediaController.hideOverlay();
        }
        if (this != null && this.isPlaying()) {
            this.pause();
        }
    }

    @Override
    public void doPlayPause() {
        if (mMediaController != null) {
            mMediaController.doPausePlay();
        }
    }

    @Override
    public void reset() {
        super.reset();
        mMediaController.unfreezePausePlay();
    }
}
