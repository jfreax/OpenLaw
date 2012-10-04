package de.jdsoft.gesetze.data;

public class Composer {
	public static final String TAG = Composer.class.getSimpleName();
	
	public String name;
	public String year;
	
	public Composer(String name, String year) {
		this.name = name;
		this.year = year;
	}
}
