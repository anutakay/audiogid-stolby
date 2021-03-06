package ru.audiogid.krsk.stolby.model;

public class Record extends StaticPoint {

    /**
     * 
     */
    private static final long serialVersionUID = 5675723231376116824L;

    private int radius;

    private String audio;

    public Record(final double lon, final double lat, final int radius,
            final String title, final String audio) {
        super(lon, lat, title);
        this.radius = radius;
        this.audio = audio;
    }

    public int getRadius() {
        return radius;
    }

    public String getAudio() {
        return audio;
    }

    public String getSnippet() {
        return getTitle() + getLat() + getLon();
    }
}
