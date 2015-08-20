package ru.audiogid.krsk.stolby.model;

import java.io.Serializable;

public class StaticPoint implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1483716846873507761L;

    private double lon;

    private double lat;

    private String title;

    public StaticPoint(final double lon, final double lat, final String title) {
        this.lon = lon;
        this.lat = lat;
        this.title = title;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return "sp" + getTitle() + getLat() + getLon();
    }

}
