package ru.audiogid.krsk.stolby.model;

import java.io.Serializable;

public class StaticPoint implements Serializable {
	
	private double lon;
	
	private double lat;
	
	private String title;
	
	public StaticPoint(final double lon, final double lat, final int radius, final String title) {
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
		return getTitle() + getLat() + getLon();
	}
	

}
