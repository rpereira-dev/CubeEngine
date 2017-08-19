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
import com.grillecube.common.event.EventGetTasks;
import com.grillecube.common.event.EventPostLoop;
import com.grillecube.common.event.EventPreLoop;
import com.grillecube.common.mod.ModLoader;
import com.grillecube.common.network.INetwork;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.Timer;

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
	protected final Side side;

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

	private Event eventGetTasks;

	public VoxelEngine(Side side) {

		Logger.get().log(Level.FINE, "Starting common Engine!");

		VOXEL_ENGINE_INSTANCE = this;

		this.side = side;
		this.debug(true);

		this.timer = new Timer();
		this.rng = new Random();

		this.resources = this.instanciateResourceManager();
		this.modLoader = new ModLoader();
		this.tasks = new ArrayList<VoxelEngine.Callable<Taskable>>(256);

		// inject default mod
		this.modLoader.injectMod(VoxelEngineDefaultMod.class);

		this.eventGetTasks = new EventGetTasks();

		Logger.get().log(Level.FINE, "Common Engine started!");
	}

	protected abstract ResourceManager instanciateResourceManager();

	/** load resources */
	public void load() {

		this.preLoaded();
		this.loadResources("./mods", "./mod", "./plugin", "./plugins");
		this.postLoaded();
		isRunning = true;
	}

	/** reload every game resources */
	public void reloadResources(String... folders) {
		this.resources.clean();
		this.modLoader.deinitializeAll(this.getResourceManager());
		this.loadResources(folders);
	}

	private void loadResources(String... folders) {

		for (String folder : folders) {
			this.modLoader.injectMods(folder);
		}

		this.modLoader.initializeAll(this.getResourceManager());
		this.modLoader.loadAll(this.getResourceManager());
		this.resources.load();
	}

	protected abstract void preLoaded();

	protected abstract void postLoaded();

	/**
	 * make the engine loop
	 * 
	 * @throws InterruptedException
	 */
	public void loop() throws InterruptedException {

		this.executor = Executors.newFixedThreadPool(8);
		this.resources.getEventManager().invokeEvent(new EventPreLoop());
		this.onLoopStart();

		while (this.isRunning()) {
			this.onLoop();
			this.timer.update();
			this.resources.update();
			this.updateTasks();
		}

		this.onLoopEnd();
		this.resources.getEventManager().invokeEvent(new EventPostLoop());

		this.stopAll();
	}

	private void updateTasks() {

		// clear tasks
		this.tasks.clear();

		this.getTasks(this.tasks);
		this.getResourceManager().getEventManager().invokeEvent(this.eventGetTasks);
		this.runTasks();
	}

	/** get the tasks to run for this frame */
	protected abstract void getTasks(ArrayList<VoxelEngine.Callable<Taskable>> tasks);

	private void runTasks() {

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
	private void stopExecutor() {
		Logger.get().log(Logger.Level.DEBUG, "Stopping thread executor...");
		this.executor.shutdown();
		try {
			this.executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.get().log(Logger.Level.DEBUG, "Thread executor timeout: " + e.getLocalizedMessage());
		}
	}

	public Timer getTimer() {
		return (this.timer);
	}

	protected abstract void onLoopEnd();

	protected abstract void onLoopStart();

	public abstract void onLoop();

	/** request the game to stop */
	public void stop() {
		this.isRunning = false;
	}

	/** stop the game properly */
	public void stopAll() {

		if (this.resources == null) {
			Logger.get().log(Level.WARNING, "Tried to stop an unstarted / already stopped engine! Cancelling");
			return;
		}

		Logger.get().log(Level.FINE, "Stopping engine...");

		this.stopExecutor();
		this.resources.stop();
		this.resources = null;

		this.modLoader.stop(this.resources);
		this.modLoader = null;

		if (this.network != null) {
			this.network.stop();
			this.network = null;
		}
		Logger.get().log(Level.FINE, "Stopped");

		this.onStopped();
	}

	protected abstract void onStopped();

	/** return the main resource manager */
	public abstract <T extends ResourceManager> T getResourceManager();

	/** get the mod loader */
	public ModLoader getModLoader() {
		return (this.modLoader);
	}

	/** run the garbage collector */
	public void runGC() {
		Runtime.getRuntime().gc();
	}

	/** return true if the voxel engine is running */
	public boolean isRunning() {
		return (this.isRunning);
	}

	/** get the rng */
	public Random getRNG() {
		return (this.rng);
	}

	public static VoxelEngine instance() {
		return (VOXEL_ENGINE_INSTANCE);
	}

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
}
