package ru.audiogid.krsk.stolby.audio;

import java.io.IOException;

import ru.audiogid.krsk.stolby.R;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;

public class Player extends AudioFocusPlayer implements IPlayer, OnLoadCompleteListener {

    private Context mContext;

    private IMediaController mMediaController;
    
    private Handler mHandler = new Handler();
    
    SoundPool sp;
    
    int soundIdShot;
    
    int soundDuration = 0;

    public Player(final Context context, final RelativeLayout anchorView) {
        super(context);
        mContext = context;
        mMediaController = new MediaController(context);
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(anchorView); 
        
        sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);
        soundIdShot = sp.load(mContext, R.raw.uodl, 1);
        soundDuration = getSoundDuration(R.raw.uodl);
    }
    
    private int getSoundDuration(int id) {
        MediaPlayer mp = MediaPlayer.create(mContext, id);
        int a = mp.getDuration();
        mp.release();
        return a;
    }

    OnPreparedListener onPreparedListenerPlayNow = new OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mp) {
            mHandler.post(new Runnable() {
                public void run() {
                    sp.play(soundIdShot, 1, 1, 0, 0, 1);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start();
                            mMediaController.showOverlay();
                        }
                    }, soundDuration);
                    
                }
            });
        }
    };

    OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mp) {
            mHandler.post(new Runnable() {
                public void run() {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMediaController.showOverlay();
                        }
                    }, soundDuration);
                    
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

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.d("debug", "onLoadComplete, sampleId = " + sampleId + ", status = " + status);
        
    }
}
