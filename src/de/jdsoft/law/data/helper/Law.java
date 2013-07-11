package de.jdsoft.law.data.helper;

import java.io.IOException;
import java.io.Serializable;

public class Law implements Serializable {
    private static final long serialVersionUID = 9107072458243854482L;

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

    private void writeObject(java.io.ObjectOutputStream out)
          throws IOException {

        out.write(id);
        out.writeObject(shortname);
        out.writeObject(longname);
        out.writeObject(slug);
   }

   private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException {

       this.id = in.read();
       this.shortname = (String)in.readObject();
       this.longname = (String)in.readObject();
       this.slug = (String)in.readObject();
  }
}
