package ru.audiogid.krsk.stolby.model;

import java.io.Serializable;

public class Record extends StaticPoint implements Serializable {
	
	private int radius;
	
	private String audio;
	
	public Record(final double lon, final double lat, final int radius, final String title, final String audio) {
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
