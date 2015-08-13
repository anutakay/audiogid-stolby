package ru.audiogid.krsk.stolby.maps;

import ru.audiogid.krsk.stolby.audio.IPlayer;

public interface IAGMapFragment {
    
    void init();
    
    void toHomeLocation();
    
    void setPlayer(IPlayer player);

}
