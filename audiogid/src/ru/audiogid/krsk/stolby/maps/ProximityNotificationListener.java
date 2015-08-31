package ru.audiogid.krsk.stolby.maps;

public interface ProximityNotificationListener {

    void onProximity(final String snippet);
    
    void onProximityNotificationClick(final String snippet);
}
