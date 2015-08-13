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

public class Player implements MediaPlayerControl, IPlayer,
        OnAudioFocusChangeListener {

    private Context mContext;

    private MediaPlayer mMediaPlayer;

    private IMediaController mMediaController;

    private AudioManager mAudioManager;

    private Handler mHandler = new Handler();

    public Player(final Context context, final RelativeLayout anchorView) {
        mContext = context;
        mMediaController = new MediaController(context);
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(anchorView);
        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
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

    OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mAudioManager.abandonAudioFocus(Player.this);
            mMediaController.freezePausePlay();
        }
    };

    @Override
    public void setAudio(final String audioFile, final boolean playNow) {
        mMediaPlayer.reset();
        if (playNow) {
            mMediaPlayer.setOnPreparedListener(onPreparedListenerPlayNow);
        } else {
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
        }
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        AssetFileDescriptor afd;
        try {
            afd = ((Context) mContext).getAssets().openFd(audioFile);

            mMediaPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getLength());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("PlayAudioDemo", "Could not open file " + audioFile
                    + " for playback.", e);
        }
    }

    @Override
    public void start() {
        int requestResult = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
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
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(final int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        int pos = mMediaPlayer.getCurrentPosition();
        if (pos == 0) {
            return 0;
        }
        return 100 * mMediaPlayer.getDuration() / pos;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public void destroy() {
        mAudioManager.abandonAudioFocus(this);
        mMediaPlayer.stop();
        mMediaPlayer.release();
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
    public void onAudioFocusChange(final int focusChange) {
        Log.d("Debug", "Аудио фокус сменился");
    }
}
