package com.grillecube.common.resources;

import java.util.HashMap;

public class LangManager extends GenericManager<HashMap<String, String>> {

	private static LangManager _instance;

	private HashMap<String, String> _lang;

	public static int FR_FR;
	public static int EN_US;

	public LangManager(ResourceManager resource_manager) {
		super(resource_manager);
		_instance = this;
	}

	@Override
	protected void onInitialized() {
	}

	@Override
	protected void onStopped() {
	}

	@Override
	protected void onCleaned() {
	}

	@Override
	protected void onLoaded() {
		FR_FR = this.registerLang("fr_FR");
		EN_US = this.registerLang("en_US");
		// TODO add langs
		this.setLang(EN_US);
	}

	/** register a lang to the default voxel engine assets dir */
	private int registerLang(String langID) {
		return (super.registerObject(ResourceManager.getConfigFile(R.getResPath("lang/" + langID + ".lang"), 1024)));
	}

	/**
	 * register a new lang
	 * 
	 * @return the lang id
	 */
	public int registerLang(String modid, String langID) {
		return (super.registerObject(
				ResourceManager.getConfigFile(super.getResource(modid, "lang/" + langID + ".lang"), 1024)));
	}

	/** set the language to be use */
	public void setLang(int langID) {
		this._lang = this.getObjectByID(langID);
	}

	/** get the string with the given id */
	public String getString(String strid) {
		if (this._lang == null || strid == null || strid.length() == 0) {
			return (null);
		}

		return (this._lang.get(strid));
	}

	private static String capitalizeStr(String str) {
		if (str.length() == 0) {
			return (str);
		}

		if (str.length() == 0) {
			return ("" + Character.toUpperCase(str.charAt(0)));
		}
		return (Character.toUpperCase(str.charAt(0)) + str.toLowerCase().substring(1, str.length()));
	}

	@Override
	protected void onObjectRegistered(HashMap<String, String> object) {
	}

	public static LangManager instance() {
		return (_instance);
	}
}
