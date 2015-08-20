package ru.audiogid.krsk.stolby.audio;

public interface IPlayer {

    public void setAudio(final String audio, final boolean playNow, final boolean jingle);

    public void hideOverlay();

    void doPlayPause();
    
    void reset();
}
