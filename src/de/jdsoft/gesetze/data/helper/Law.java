package de.jdsoft.gesetze.data.helper;

public class Law {
	int id;
	String shortname;
	String longname;
	String slug;

	public Law(){

	}
	public Law(int id, String shortname, String slug, String longname) {
		this.id = id;
		this.shortname = shortname;
		this.longname = longname;
		this.slug = slug;
	}

	public Law(String shortname, String slug, String longname) {
		this.shortname = shortname;
		this.longname = longname;
		this.slug = slug;
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
	
	public String getSlug() {
		return this.slug;
	}
	
	public void setSlug(String slug) {
		this.slug = slug;
	}
}
