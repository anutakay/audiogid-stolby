package ru.audiogid.krsk.stolby.maps;

import ru.audiogid.krsk.stolby.audio.Player;

public interface MapFragment {

    void init();

    void toHomeLocation();

    void setPlayer(Player player);

}
