package com.aidn5.hypeguild.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Little boy helps with saving and loading settings from the jar itself and
 * from file systems
 * 
 * @author aidn5
 * @version 1.2
 */
public class Settings extends Properties {
	public final String settingsPath;
	public final boolean inSelf;// In the program itself and not outside in fileSystem

	private boolean fileChecked; // Check

	/**
	 * @param settings_path
	 *            Where the settings located
	 * 
	 * @param inSelf
	 *            true when the settings in the jar file itself. false when when it
	 *            is a file in the system
	 * 
	 * @throws IOException
	 *             when can't create/load the settings
	 */
	public Settings(String settingsPath, boolean inSelf) throws IOException {
		super();
		this.settingsPath = settingsPath;
		this.inSelf = inSelf;

		checkFile();
	}

	public boolean reloadUserSettings() {
		try {
			checkFile();

			if (this.inSelf) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						getClass().getClassLoader().getResourceAsStream(settingsPath), "UTF-8");

				BufferedReader readIn = new BufferedReader(inputStreamReader);
				this.load(readIn);
			} else {
				InputStream inputstream = new FileInputStream(settingsPath);
				this.load(inputstream);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean SaveUserSettings() {
		if (inSelf) return false; // Can't change the jar itself

		try {
			checkFile();

			OutputStream out = new FileOutputStream(settingsPath);
			this.store(out, "localsettings");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void checkFile() throws IOException {
		if (inSelf || fileChecked) return;

		File file = new File(settingsPath);
		if (!file.exists()) {
			// Easiest way to create the dir and the file
			file.mkdirs();
			file.delete();
			file.createNewFile();
		}

		fileChecked = true;
	}
}
