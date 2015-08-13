package ru.audiogid.krsk.stolby.audio;

public interface Jingled {
	
	void setJinglePlayer(IPlayer jinglePlayer);
	
	IPlayer getJinglePlayer();
	
	void setJinglePlayMode(boolean begin, boolean end);

}
