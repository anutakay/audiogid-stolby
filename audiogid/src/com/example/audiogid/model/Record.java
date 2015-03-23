package com.example.audiogid.model;

public class Record {
	
	private double lon;
	
	private double lat;
	
	private String title;
	
	private String audio;
	
	public Record(final double lon, final double lat, final String title, final String audio) {
		this.lon = lon;
		this.lat = lat;
		this.title = title;
		this.audio = audio;
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
	
	public String getAudio() {
		return audio;
	}
	
}
