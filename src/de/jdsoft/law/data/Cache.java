package de.jdsoft.law.data;

import java.io.File;
import java.io.IOException;

import android.util.Log;

import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;

public class Cache {

	private DiskLruCache cache = null;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
																 // TODO this should be a property!
	private static final String DISK_CACHE_SUBDIR = ".Gesetze";
	private static final int DISK_CACHE_VERSION = 7;
	
	
	public Cache() {
		try {
			this.openCache();
		} catch (IOException e) {
			// Okay, so we can't use it :(
			Log.e(Cache.class.getName(), "Can't open cache " + DISK_CACHE_SUBDIR + "!");
			e.printStackTrace();
		}
	}
	
	public void openCache() throws IOException {
        String javaTmpDir = System.getProperty("java.io.tmpdir");
        File cacheDir = new File(javaTmpDir, DISK_CACHE_SUBDIR);
        cacheDir.mkdir();
        
        cache = DiskLruCache.open(cacheDir, DISK_CACHE_VERSION, 1, DISK_CACHE_SIZE);
	}
	
	public void close() {
		try {
			cache.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isClosed() {
		return cache.isClosed();
	}
	
	public Snapshot get(String s) throws IOException {
		return cache.get(s);
	}
	
	public Editor edit(String s) throws IOException {
		return cache.edit(s);
	}
	
	public void flush() throws IOException {
		cache.flush();
	}
}
