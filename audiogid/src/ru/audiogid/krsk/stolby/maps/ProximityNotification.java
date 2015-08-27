package ru.audiogid.krsk.stolby.maps;

public interface ProximityNotification {

    void onProximity(final String snippet);
    
    void onProximityNotificationClick(final String snippet);
}
