package de.jdsoft.gesetze.data.helper;

public class Composer {
	public static final String TAG = Composer.class.getSimpleName();
	
	public String shortName;
	public String longName;
	
	public Composer(String shortName, String longName) {
		this.shortName = shortName;
		this.longName = longName;
	}
}
