package com.grillecube.common;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import com.grillecube.common.utils.JSONHelper;

public class Config {
	private final String filepath;
	private JSONObject values;

	protected Config(String filepath) {
		this.values = new JSONObject();
		this.filepath = filepath;
	}

	public final String getFilepath() {
		return (this.filepath);
	}

	public final JSONObject getValues() {
		return (this.values);
	}

	/**
	 * @param key
	 *            : the key of the object
	 * @param hierarchy
	 *            : the hierarchy of this object (since topest layer of the json
	 *            object)
	 * @return : the associated objects, or null if doesn't exists
	 */
	public final JSONObject getObject(String... hierarchy) {
		JSONObject jsonObject = this.values;
		for (String child : hierarchy) {
			jsonObject = jsonObject.getJSONObject(child);
		}
		return (jsonObject);
	}

	public final String getString(String key, String defaultValue, String... hierarchy) {
		JSONObject jsonObject = this.getObject(hierarchy);
		if (jsonObject != null) {
			try {
				return (jsonObject.getString(key));
			} catch (Exception e) {
			}
		}
		return (defaultValue);
	}

	public final double getDouble(String key, double defaultValue, String... hierarchy) {
		JSONObject jsonObject = this.getObject(hierarchy);
		if (jsonObject != null) {
			try {
				return (jsonObject.getDouble(key));
			} catch (Exception e) {
			}
		}
		return (defaultValue);
	}

	public final float getFloat(String key, float defaultValue, String... hierarchy) {
		JSONObject jsonObject = this.getObject(hierarchy);
		if (jsonObject != null) {
			try {
				return ((float) jsonObject.getDouble(key));
			} catch (Exception e) {
			}
		}
		return (defaultValue);
	}

	public final int getInt(String key, int defaultValue, String... hierarchy) {
		JSONObject jsonObject = this.getObject(hierarchy);
		if (jsonObject != null) {
			try {
				return (jsonObject.getInt(key));
			} catch (Exception e) {
			}
		}
		return (defaultValue);
	}

	/**
	 * load the config
	 * 
	 * @return true if the config was loaded from it file, and the config is filled
	 *         with the values, else it returns false and the config is asusmed
	 *         empty
	 */
	public final boolean load() {
		File file = new File(this.filepath);
		if (!file.exists()) {
			return (false);
		}
		try {
			String src = JSONHelper.readFile(file);
			this.values = new JSONObject(src);
			return (true);
		} catch (Exception e) {
			Logger.get().log(Logger.Level.ERROR, "Couldn't load config", e.getLocalizedMessage());
			this.values = new JSONObject();
		}
		return (false);
	}

	/** save the config to it associated file */
	public final void save() {
		try {
			File file = new File(this.filepath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			JSONHelper.writeJSONObjectToFile(file, this.values);
		} catch (IOException e) {
			Logger.get().log(Logger.Level.ERROR, "Couldn't load config", e.getLocalizedMessage());
		}
	}
}
