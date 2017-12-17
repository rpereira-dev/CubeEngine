package com.grillecube.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.grillecube.common.Logger.Level;
import com.grillecube.common.defaultmod.VoxelEngineDefaultMod;
import com.grillecube.common.event.Event;
import com.grillecube.common.event.EventGetTasks;
import com.grillecube.common.event.Listener;
import com.grillecube.common.event.EventLoop;
import com.grillecube.common.event.EventPostLoop;
import com.grillecube.common.event.EventPreLoop;
import com.grillecube.common.mod.ModLoader;
import com.grillecube.common.network.INetwork;
import com.grillecube.common.resources.AssetsPack;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.Timer;
import com.grillecube.common.world.World;

public abstract class VoxelEngine {

	/** version */
	public static final String VERSION = "0.0.1";
	public static final String MOD_ID = "VoxelEngine";

	/** singleton */
	private static VoxelEngine VOXEL_ENGINE_INSTANCE;

	public enum Side {
		CLIENT, SERVER, BOTH;

		public boolean match(Side side) {
			return (this == side || side == BOTH || this == BOTH);
		}
	}

	public abstract class Callable<T> implements java.util.concurrent.Callable<T> {
		@Override
		public abstract T call() throws Exception;

		public abstract String getName();
	}

	/** the tasks to run each frames */
	private ArrayList<VoxelEngine.Callable<Taskable>> tasks;

	/** executor service */
	private ExecutorService executor;

	/** the resources directory */
	private File gameDir;
	private ArrayList<AssetsPack> assets;

	/** side of the running engine */
	protected Side side;

	/** bools */
	private boolean isRunning;
	private boolean debug;

	/** Mod loader */
	private ModLoader modLoader;

	/** Resources */
	protected ResourceManager resources;

	/** networking */
	private INetwork network;

	/** random number generator */
	private Random rng;

	/** Timer */
	private Timer timer;

	/** events */
	private EventPreLoop eventPreLoop;
	private EventLoop eventLoop;
	private EventPostLoop eventPostLoop;

	/** loaded worlds */
	private ArrayList<World> loadedWorlds;

	/**
	 * config hashmap, key: filepath, value: config file
	 */
	private HashMap<String, Config> config;

	public VoxelEngine(Side side) {

		Logger.get().log(Level.FINE, "Starting common Engine!");

		VOXEL_ENGINE_INSTANCE = this;
		this.side = side;
	}

	/** allocate the engine */
	public final void initialize() {

		Logger.get().log(Level.FINE, "Initializing engine...");

		this.debug(true);

		this.timer = new Timer();
		this.rng = new Random();

		// assets
		this.loadGamedir();
		this.assets = new ArrayList<AssetsPack>();

		this.resources = this.instanciateResourceManager();
		this.resources.initialize();

		// config
		this.config = new HashMap<String, Config>();
		this.loadConfig(MOD_ID, R.getResPath("config.json"));

		this.modLoader = new ModLoader();
		this.tasks = new ArrayList<VoxelEngine.Callable<Taskable>>(256);

		// inject default mod
		this.modLoader.injectMod(VoxelEngineDefaultMod.class);

		// events
		this.eventPreLoop = new EventPreLoop();
		this.eventLoop = new EventLoop();
		this.eventPostLoop = new EventPostLoop();

		// worlds
		this.loadedWorlds = new ArrayList<World>();

		this.onInitialized();

		Logger.get().log(Level.FINE, "Common Engine initialized!");
	}

	/** load game directory */
	private final void loadGamedir() {
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

	/** load config */
	private final Config loadConfig(String id, String filepath) {
		if (this.config.containsKey(id)) {
			return (this.config.get(id));
		}
		Config cfg = new Config(filepath);
		this.config.put(id, cfg);
		Logger.get().log(Logger.Level.FINE, "Loading config", filepath);
		cfg.load();
		return (cfg);
	}

	/** get a config */
	public final Config getConfig(String filepath) {
		return (this.config.get(filepath));
	}

	/** get every configs */
	public final HashMap<String, Config> getConfigs() {
		return (this.config);
	}

	protected abstract void onInitialized();

	/** deallocate the engine properly */
	public final void deinitialize() {

		Logger.get().log(Level.FINE, "Deinitializing engine...");

		this.stopRunning();

		if (this.resources == null) {
			Logger.get().log(Level.WARNING, "Tried to stop an unstarted / already stopped engine! Cancelling");
			return;
		}

		Logger.get().log(Level.FINE, "Saving configs");
		for (Entry<String, Config> entry : this.config.entrySet()) {
			Config cfg = entry.getValue();
			Logger.get().log(Level.FINE, "\t\t", entry.getKey(), cfg.getFilepath());
			cfg.save();
		}

		this.stopExecutor();
		this.resources.deinitialize();
		this.resources = null;

		this.modLoader.deinitialize(this.resources);
		this.modLoader = null;

		if (this.network != null) {
			this.network.stop();
			this.network = null;
		}
		Logger.get().log(Level.FINE, "Stopped");

		this.onDeinitialized();
	}

	protected abstract void onDeinitialized();

	protected abstract ResourceManager instanciateResourceManager();

	/** load resources */
	public void load() {
		this.loadResources("./mods", "./mod", "./plugin", "./plugins");
	}

	/** reload every game resources */
	public final void reload(String... folders) {
		this.unload();
		this.load();
	}

	private final void unload() {
		this.resources.unload();
		this.modLoader.unload(this.getResourceManager());
	}

	private final void loadResources(String... folders) {

		for (String folder : folders) {
			this.modLoader.injectMods(folder);
		}

		this.modLoader.load(this.getResourceManager());
		this.resources.load();
	}

	/**
	 * make the engine loop
	 * 
	 * @throws InterruptedException
	 */
	public final void loop() throws InterruptedException {

		this.isRunning = true;

		this.executor = Executors.newFixedThreadPool(8);
		this.invokeEvent(this.eventPreLoop);

		while (this.isRunning()) {
			this.timer.update();
			this.invokeEvent(this.eventLoop);
			this.updateTasks();
		}

		this.invokeEvent(this.eventPostLoop);
	}

	private final void updateTasks() {

		// clear tasks
		this.tasks.clear();
		for (World world : this.loadedWorlds) {
			world.getTasks(this, this.tasks);
		}
		this.invokeEvent(new EventGetTasks(this.tasks));
		this.runTasks();
	}

	private final void runTasks() {

		// run tasks and get their results
		List<Future<Taskable>> results;
		try {
			results = this.executor.invokeAll(this.tasks, 2, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			return;
		}

		// check that each tasks worked properly (none crashed basically)
		if (this.debug()) {

			// for each tasks
			int i;
			for (i = 0; i < results.size(); i++) {
				Future<Taskable> result = results.get(i);
				String task = this.tasks.get(i).getName();
				try {
					// try to get it
					if (!result.isCancelled() && result.get() != null) {
						// task ran properly
					} else {
						Logger.get().log(Logger.Level.ERROR, "Task cancelled (timeout): ", task);
					}
				} catch (Exception e) {
					// if get() failed, then an error occured
					Logger.get().log(Logger.Level.ERROR, "Exception occured when executing task", task);
					e.printStackTrace(Logger.get().getPrintStream());
				}
			}
		}
	}

	/** stop the thread executor */
	private final void stopExecutor() {
		if (this.executor == null) {
			return;
		}
		Logger.get().log(Logger.Level.DEBUG, "Stopping thread executor...");
		this.executor.shutdown();
		try {
			this.executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.get().log(Logger.Level.DEBUG, "Thread executor timeout: " + e.getLocalizedMessage());
		}
	}

	public final Timer getTimer() {
		return (this.timer);
	}

	protected final void invokeEvent(Event event) {
		// if (this.getResourceManager() == null ||
		// this.getResourceManager().getEventManager() == null) {
		// return;
		// }
		this.getResourceManager().getEventManager().invokeEvent(event);
	}

	protected final void registerEventCallback(Listener<?> eventCallback) {
		this.getResourceManager().getEventManager().addListener(eventCallback);
	}

	/** request the game to stop */
	public final void stopRunning() {
		this.isRunning = false;
	}

	/** return the main resource manager */
	public abstract <T extends ResourceManager> T getResourceManager();

	/** get the mod loader */
	public final ModLoader getModLoader() {
		return (this.modLoader);
	}

	/** run the garbage collector */
	public final void runGC() {
		Runtime.getRuntime().gc();
	}

	/** return true if the voxel engine is running */
	public final boolean isRunning() {
		return (this.isRunning);
	}

	/** get the rng */
	public final Random getRNG() {
		return (this.rng);
	}

	public static VoxelEngine instance() {
		return (VOXEL_ENGINE_INSTANCE);
	}

	/** get the side on which the engine is running */
	public final Side getSide() {
		return (instance().side);
	}

	/** enable or disable debug (note: debug on may influence performances) */
	public void debug(boolean value) {
		this.debug = value;
	}

	public boolean debug() {
		return (this.debug);
	}

	/** add a world to the game logic loop */
	public final World loadWorld(int worldID) {
		World world = this.getResourceManager().getWorldManager().getWorld(worldID);
		if (world == null) {
			Logger.get().log(Logger.Level.ERROR, "Tried to load an unknown world, with id", worldID);
			return (null);
		}
		if (this.loadedWorlds.contains(world)) {
			Logger.get().log(Logger.Level.ERROR, "Tried to load an already-loaded world, with id", worldID);
			return (null);
		}
		this.loadedWorlds.add(world);
		world.load();
		return (world);
	}

	/** remove a world from the game logic loop */
	public final void unloadWorld(int worldID) {
		World world = this.getResourceManager().getWorldManager().getWorld(worldID);
		if (world == null) {
			Logger.get().log(Logger.Level.ERROR, "Tried to unload an unknown world, with id", worldID);
			return;
		}
		this.loadedWorlds.remove(world);
	}

	/** get the current world of the client (can be null) */
	public World getWorld(int worldID) {
		return (this.getResourceManager().getWorldManager().getWorld(worldID));
	}

	/** return the game main directory */
	public final File getGamedir() {
		return (this.gameDir);
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
}
