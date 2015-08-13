package ru.audiogid.krsk.stolby.audio;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.widget.MediaController.MediaPlayerControl;

public class AudioFocusPlayer implements OnAudioFocusChangeListener, MediaPlayerControl{
    
    private AudioManager mAudioManager;
    
    private MediaPlayer mMediaPlayer;
    
    public AudioFocusPlayer(final Context context) {
        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
        Log.d("Debug", "Аудио фокус сменился");
    }
    
    protected void onCompletion(MediaPlayer mp) {
        mAudioManager.abandonAudioFocus(this);
    }

    public boolean canStart() {
        int requestResult = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
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
    public void start() {
        if (canStart()) {
            mMediaPlayer.start();
        } else {
            Log.d("Debug", "Невозможно получить фокус ");
        }
    }
    
    public void destroy() {
        mAudioManager.abandonAudioFocus(this);
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }
    
    protected void setAudio(final AssetFileDescriptor afd, 
            OnPreparedListener preparedListener, 
            OnCompletionListener completionListener) {
        mMediaPlayer.reset();
        mMediaPlayer.setOnPreparedListener(preparedListener);
        mMediaPlayer.setOnCompletionListener(completionListener);
        try {
            mMediaPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getLength());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("PlayAudioDemo", "Could not open file for playback.", e);
        }
    }
    
    void reset() {
        mMediaPlayer.reset();
    }
    
}
