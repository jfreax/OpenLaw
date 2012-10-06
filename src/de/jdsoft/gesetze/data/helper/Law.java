package de.jdsoft.gesetze.data.helper;

public class Law {
	int id;
	String shortname;
	String longname;
	String text;

	public Law(){

	}
	public Law(int id, String shortname, String longname, String text) {
		this.id = id;
		this.shortname = shortname;
		this.longname = longname;
		this.text = text;
	}

	public Law(String shortname, String longname, String text) {
		this.shortname = shortname;
		this.longname = longname;
		this.text = text;
	}

	public int getID() {
		return this.id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getShortName() {
		return this.shortname;
	}

	public void setShortName(String shortname) {
		this.shortname = shortname;
	}

	public String getLongName() {
		return this.longname;
	}
	
	public void setLongName(String longname) {
		this.longname = longname;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
