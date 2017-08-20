package com.grillecube.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.grillecube.common.Logger.Level;
import com.grillecube.common.defaultmod.VoxelEngineDefaultMod;
import com.grillecube.common.event.Event;
import com.grillecube.common.event.EventCallback;
import com.grillecube.common.event.EventGetTasks;
import com.grillecube.common.event.EventOnLoop;
import com.grillecube.common.event.EventPostLoop;
import com.grillecube.common.event.EventPreLoop;
import com.grillecube.common.mod.ModLoader;
import com.grillecube.common.network.INetwork;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.Timer;
import com.grillecube.common.world.World;

public abstract class VoxelEngine {

	/** version */
	public static final String VERSION = "0.0.1";

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
	private EventOnLoop eventOnLoop;
	private EventPostLoop eventPostLoop;

	/** loaded worlds */
	private ArrayList<World> loadedWorlds;

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

		this.resources = this.instanciateResourceManager();
		this.resources.initialize();

		this.modLoader = new ModLoader();
		this.tasks = new ArrayList<VoxelEngine.Callable<Taskable>>(256);

		// inject default mod
		this.modLoader.injectMod(VoxelEngineDefaultMod.class);

		// events
		this.eventPreLoop = new EventPreLoop(this);
		this.eventOnLoop = new EventOnLoop(this);
		this.eventPostLoop = new EventPostLoop(this);

		this.loadedWorlds = new ArrayList<World>();

		this.onInitialized();

		Logger.get().log(Level.FINE, "Common Engine initialized!");
	}

	protected abstract void onInitialized();

	/** deallocate the engine properly */
	public final void deinitialize() {

		this.stopRunning();

		if (this.resources == null) {
			Logger.get().log(Level.WARNING, "Tried to stop an unstarted / already stopped engine! Cancelling");
			return;
		}

		Logger.get().log(Level.FINE, "Deinitializing engine...");

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
			this.invokeEvent(this.eventOnLoop);
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
		this.invokeEvent(new EventGetTasks(this, this.tasks));
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

	protected final void registerEventCallback(EventCallback<?> eventCallback) {
		this.getResourceManager().getEventManager().registerEventCallback(eventCallback);
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

}
