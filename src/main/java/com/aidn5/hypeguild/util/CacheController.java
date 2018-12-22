package com.aidn5.hypeguild.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CacheController {
	private static final long saveCacheFor = 60 * 60 * 6; // 6 hours

	private final String cacheFolder;

	/**
	 * Indicate whether the CacheController is ready to save() and get()
	 */
	private boolean isReady = false;

	public CacheController(File cacheFolder) {
		this.cacheFolder = cacheFolder.getAbsolutePath() + "/";
	}

	/**
	 * Save data for later request
	 * 
	 * @param name
	 *            the name of data.
	 * @param string
	 *            the data to be saved
	 * 
	 * @return TRUE on success
	 */
	public boolean save(String name, String string) {
		if (!isReady()) return false;

		try {
			String file = this.cacheFolder + name + ".cache";
			OutputStream out = new FileOutputStream(file);
			out.write(string.getBytes());
			out.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param name
	 *            the name of the data
	 * @param time
	 *            how many seconds should the data be old
	 * @return <b>String</b> if there is data. <b>NULL</b> when there is no data,
	 *         not isReady() or can not get the data
	 */
	public String get(String name) {
		if (!isReady()) return null;

		try {
			File file = new File(this.cacheFolder + name + ".cache");
			if (!file.exists() || !file.isFile()) return null;

			long lastModified = file.lastModified();
			if (lastModified == 0) return null;

			long modifiedSince = System.currentTimeMillis() - lastModified;
			if ((this.saveCacheFor * 1000L) < modifiedSince) return null;

			FileInputStream fis = new FileInputStream(file);

			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			return new String(data, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Clear the cache
	 */
	public void clearCache() {
		File dir = new File(this.cacheFolder);

		for (File file : dir.listFiles())
			if (!file.isDirectory()) file.delete();
	}

	/**
	 * check whether the CacheController is ready to save(...) and get(...) data.
	 * When not, Create folder for the cache
	 * 
	 * @return TRUE on success. FALSE on failure
	 */
	public boolean isReady() {
		if (this.isReady) return true;

		File file = new File(this.cacheFolder);
		if (!file.exists()) {
			if (!file.mkdirs()) return false;
		}

		this.isReady = true;
		return true;
	}

}
