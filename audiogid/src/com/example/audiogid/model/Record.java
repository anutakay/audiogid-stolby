package com.example.audiogid.model;

public class Record {
	
	private double lon;
	
	private double lat;
	
	private int radius;
	
	private String title;
	
	private String audio;
	
	public Record(final double lon, final double lat, final int radius, final String title, final String audio) {
		this.lon = lon;
		this.lat = lat;
		this.radius = radius;
		this.title = title;
		this.audio = audio;
	}

	
	public double getLon() {
		return lon;
	}
	
	public double getLat() {
		return lat;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAudio() {
		return audio;
	}
	
}
