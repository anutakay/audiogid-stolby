package ru.audiogid.krsk.stolby.audio;

import android.widget.RelativeLayout;
import android.widget.MediaController.MediaPlayerControl;

public interface IMediaController {
    
    void setAnchorView(RelativeLayout anchorView);

    void setMediaPlayer(MediaPlayerControl player);
    
    void showOverlay();
    
    void hideOverlay();
    
    void doPausePlay();
    
    void updatePausePlay();
    
    void freezePausePlay();
    
    void unfreezePausePlay();
    
}
