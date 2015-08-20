package ru.audiogid.krsk.stolby.maps;

public interface IProximityNotification {

    void onProximity(final String snippet);
    
    void onProximityNotificationClick(final String snippet);
}
