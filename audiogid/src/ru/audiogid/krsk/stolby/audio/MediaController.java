package ru.audiogid.krsk.stolby.audio;

import android.widget.RelativeLayout;
import android.widget.MediaController.MediaPlayerControl;

public interface MediaController {

    void setAnchorView(final RelativeLayout anchorView);

    void setMediaPlayer(final MediaPlayerControl player);

    void showOverlay();

    void hideOverlay();

    void doPausePlay();

    void updatePausePlay();

    void freezePausePlay();

    void unfreezePausePlay();

}
