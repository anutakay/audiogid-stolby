package ru.audiogid.krsk.stolby.audio;

public interface IPlayer {
	public void setAudio(final String audio, final boolean playNow);
	public void hideOverlay();
	public void doPlayPause();
}
