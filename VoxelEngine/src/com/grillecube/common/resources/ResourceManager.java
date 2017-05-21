/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.common.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.grillecube.common.Logger;
import com.grillecube.common.VoxelEngine;

public abstract class ResourceManager {

	/** the resources directory */
	private File gameDir;
	private ArrayList<AssetsPack> assets;

	/** World manager */
	private final WorldManager worldManager;

	/** Block manager */
	private final BlockManager blockManager;

	/** Block manager */
	private final ItemManager itemManager;

	/** Packets */
	private final PacketManager packetManager;

	/** Entities */
	private final EntityManager entityManager;

	/** Models manager */
	private final ModelManager modelManager;

	/** Sound manager */
	private final SoundManager soundManager;

	/** events */
	private final EventManager eventManager;

	/** lang manager */
	private final LangManager langManager;

	/** managers as a list */
	private final ArrayList<GenericManager<?>> managers;

	private VoxelEngine engine;

	/** configuration */
	private ResourceManager.Config _config;

	private static ResourceManager RESOURCE_MANAGER_INSTANCE;

	public ResourceManager(VoxelEngine engine) {
		RESOURCE_MANAGER_INSTANCE = this;

		this.engine = engine;
		this.assets = new ArrayList<AssetsPack>();

		this.managers = new ArrayList<GenericManager<?>>();
		this.worldManager = new WorldManager(this);
		this.blockManager = new BlockManager(this);
		this.itemManager = new ItemManager(this);
		this.entityManager = new EntityManager(this);
		this.soundManager = new SoundManager(this);
		this.modelManager = new ModelManager(this);
		this.packetManager = new PacketManager(this);
		this.eventManager = new EventManager(this);
		this.langManager = new LangManager(this);

		this.managers.add(this.blockManager);
		this.managers.add(this.itemManager);
		this.managers.add(this.soundManager);
		this.managers.add(this.modelManager);
		this.managers.add(this.entityManager);
		this.managers.add(this.packetManager);
		this.managers.add(this.eventManager);
		this.managers.add(this.worldManager);
		this.managers.add(this.langManager);

		this.addResources(this.managers);

		// prepare path
		this.prepareEnginePath();

		// create config
		this._config = new ResourceManager.Config();

		// initialize resources
		Logger.get().log(Logger.Level.FINE, "initializing resources");
		for (GenericManager<?> manager : this.managers) {
			Logger.get().log(Logger.Level.FINE, manager.getClass().getSimpleName());
			manager.initialize();
		}
	}

	protected void addResources(ArrayList<GenericManager<?>> managers) {

	}

	private void prepareEnginePath() {
		String OS = (System.getProperty("os.name")).toUpperCase();
		String gamepath;
		if (OS.contains("WIN")) {
			gamepath = System.getenv("AppData");
		} else {
			gamepath = System.getProperty("user.home");
		}

		if (!gamepath.endsWith("/")) {
			gamepath = gamepath + "/";
		}
		this.gameDir = new File(gamepath + "VoxelEngine");

		Logger.get().log(Logger.Level.FINE, "Game directory is: " + this.gameDir.getAbsolutePath());
		if (!this.gameDir.exists()) {
			this.gameDir.mkdirs();
		} else if (!this.gameDir.canWrite()) {
			this.gameDir.setWritable(true);
		}
	}

	/** engine on which this resource manager correspond to */
	public VoxelEngine getEngine() {
		return (this.engine);
	}

	public static ResourceManager instance() {
		return (RESOURCE_MANAGER_INSTANCE);
	}

	public void clean() {
		Logger.get().log(Logger.Level.FINE, "* Cleaning resources manager");
		for (GenericManager<?> manager : this.managers) {
			Logger.get().log(Logger.Level.FINE, manager.getClass().getSimpleName());
			manager.clean();
		}
	}

	/** load every resources */
	public void load() {
		Logger.get().log(Logger.Level.FINE, "* Loading resources manager");
		for (GenericManager<?> manager : this.managers) {
			Logger.get().log(Logger.Level.FINE, manager.getClass().getSimpleName());
			manager.load();
		}
	}

	/** load every game resources */
	public void stop() {
		Logger.get().log(Logger.Level.FINE, "* Stopping resources manager");
		for (GenericManager<?> manager : this.managers) {
			Logger.get().log(Logger.Level.FINE, manager.getClass().getSimpleName());
			manager.stop();
		}
	}

	/** get the world manager */
	public final WorldManager getWorldManager() {
		return (this.worldManager);
	}

	/** language manager */
	public final LangManager getLangManager() {
		return (this.langManager);
	}

	/** get the block manager */
	public final BlockManager getBlockManager() {
		return (this.blockManager);
	}

	/** get the item manager */
	public ItemManager getItemManager() {
		return (this.itemManager);
	}

	/** get the packet manager */
	public final PacketManager getPacketManager() {
		return (this.packetManager);
	}

	/** get the entity manager */
	public final EntityManager getEntityManager() {
		return (this.entityManager);
	}

	/** get the sound manager */
	public final SoundManager getSoundManager() {
		return (this.soundManager);
	}

	/** get the event manager */
	public final EventManager getEventManager() {
		return (this.eventManager);
	}

	/** get the model manager */
	public final ModelManager getModelManager() {
		return (this.modelManager);
	}

	/** get the resource filepath */
	public String getResourcePath(String modid, String path) {
		String assetsdir = "assets" + File.separator;
		int length = this.gameDir.getAbsolutePath().length() + assetsdir.length() + modid.length() + path.length() + 1;
		StringBuilder builder = new StringBuilder(length);
		String respath = this.gameDir.getAbsolutePath();
		builder.append(respath);
		if (!respath.endsWith(File.separator)) {
			builder.append(File.separator);
		}
		builder.append(assetsdir);
		builder.append(modid);
		if (!modid.endsWith(File.separator)) {
			builder.append(File.separator);
		}
		builder.append(path);
		return (builder.toString().replace("/", File.separator));
	}

	/** add an assets pack (zip file) to be added to the game */
	public AssetsPack putAssets(AssetsPack pack) {
		for (AssetsPack tmppack : this.assets) {
			if (tmppack.getModID().equals(pack.getModID())) {
				Logger.get().log(Logger.Level.ERROR,
						"Tried to put an assets pack which already exists. Canceling operation. ModID: "
								+ pack.getModID());
				return (null);
			}
		}
		this.assets.add(pack);
		pack.extract();
		return (pack);
	}

	// config file constants
	private static final char COMMENT_CHAR = '#';

	public static final HashMap<String, String> getConfigFile(String filepath, int defaultcapacity) {
		return (getConfigFile(filepath, new HashMap<String, String>(defaultcapacity)));
	}

	/**
	 * a function which parse the given file and return a hashmap containing
	 * 'key-values'
	 */
	public static final HashMap<String, String> getConfigFile(String filepath, HashMap<String, String> map) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath));
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				++i;

				line = line.trim();
				int index = line.indexOf(COMMENT_CHAR);
				if (index == 0) {
					continue;
				}
				String data = null;

				data = index == -1 ? line : line.substring(0, index - 1);
				if (data != null && data.length() > 0) {
					String[] strs = data.split("=", 0);
					if (strs.length < 2) {
						Logger.get().log(Logger.Level.DEBUG,
								"line malformatted: l " + i + " : `" + line + "` in file " + filepath);
						continue;
					}
					String value = strs[strs.length - 1].trim();
					for (int j = 0; j < strs.length - 1; j++) {
						map.put(strs[j].trim(), value);
					}
				}
			}
			reader.close();

		} catch (IOException e) {
			Logger.get().log(Logger.Level.ERROR, "Couldnt read config file: " + filepath);
		}
		return (map);
	}

	public static boolean fileExists(String filepath) {
		return (new File(filepath).exists());
	}

	/** export the map to the given filepath */
	public static final void exportConfigFile(String filepath, HashMap<String, String> map) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
			ArrayList<String> keys = new ArrayList<String>(map.keySet());
			Collections.sort(keys);
			for (String key : keys) {
				writer.append(key);
				writer.append('=');
				writer.append(map.get(key));
				writer.newLine();
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {
			Logger.get().log(Logger.Level.ERROR, "Couldnt read config file: " + filepath);
		}

	}

	public void update() {
		for (GenericManager<?> manager : this.managers) {
			manager.update();
		}
	}

	/** get the config informations */
	public Config getConfig() {
		return (this._config);
	}

	public class Config {

		/** config file name */
		public static final String CONFIG_FILE = ".config";

		/** config **/
		private HashMap<String, String> _config;

		public Config() {

			this._config = new HashMap<String, String>(1024);

			// configuration stuff
			String configpath = R.getResPath(CONFIG_FILE);
			if (ResourceManager.fileExists(configpath)) {
				ResourceManager.getConfigFile(configpath, this._config);
			} else {
				ResourceManager.exportConfigFile(configpath, this._config);
			}
		}

		/** get a config value */
		public String get(String key, String default_value) {
			String value = this._config.get(key);
			return (value == null ? default_value : value);
		}

		/** set a new config value */
		public void set(String key, String value) {
			this._config.put(key, value);
		}

		public boolean isSet(String key) {
			return (this._config.get(key) != null);
		}
	}
}
