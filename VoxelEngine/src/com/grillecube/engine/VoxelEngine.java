package com.grillecube.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.event.EventPostLoop;
import com.grillecube.engine.event.EventPreLoop;
import com.grillecube.engine.mod.IMod;
import com.grillecube.engine.mod.Mod;
import com.grillecube.engine.mod.ModLoader;
import com.grillecube.engine.network.Network;
import com.grillecube.engine.resources.ResourceManager;
import com.grillecube.engine.world.Timer;

public abstract class VoxelEngine {

	/** version */
	public static final String VERSION = "0.0.1";

	/** singleton */
	private static VoxelEngine _instance;

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
	private ArrayList<VoxelEngine.Callable<Taskable>> _tasks;

	/** executor service */
	private ExecutorService _executor;

	/** side of the running engine */
	protected final Side _side;

	/** bools */
	private boolean _is_running;
	private boolean _debug;

	/** Mod loader */
	private ModLoader _mod_loader;

	/** intern mod that are injected and loaded first */
	private ArrayList<Mod> _intern_mods;

	/** Resources */
	protected ResourceManager _resources;

	/** networking */
	private Network _network;

	/** random number generator */
	private Random _rng;

	/** Timer */
	private Timer _timer;

	public VoxelEngine(Side side) {

		this._side = side;

		this.debug(true);

		Logger.get().log(Level.FINE, "Starting common Engine!");

		setInstance(this);

		this._timer = new Timer();
		this._rng = new Random();

		this._resources = this.instanciateResourceManager();
		this._mod_loader = new ModLoader();
		this._intern_mods = new ArrayList<Mod>();
		this._tasks = new ArrayList<VoxelEngine.Callable<Taskable>>(256);

		// inject default mod
		this.injectInternalMod(new VoxelEngineDefaultMod());

		Logger.get().log(Level.FINE, "common Engine started!");
	}

	/** set the singleton instance to use */
	public static final VoxelEngine setInstance(VoxelEngine instance) {
		_instance = instance;
		return (_instance);
	}

	protected abstract ResourceManager instanciateResourceManager();

	/** load resources */
	public void load() {

		this.preLoaded();
		this.loadResources("./mods", "./mod", "./plugin", "./plugins");
		this.postLoaded();
		_is_running = true;
	}

	/** reload every game resources */
	public void reloadResources(String... folders) {
		this._resources.clean();
		this._mod_loader.deinitializeAll(this.getResourceManager());
		this.loadResources(folders);
	}

	private void loadResources(String... folders) {
		for (Mod mod : this._intern_mods) {
			this._mod_loader.injectMod(mod);
		}

		for (String folder : folders) {
			this._mod_loader.injectMods(folder);
		}

		this._mod_loader.initializeAll(this.getResourceManager());
		this._mod_loader.loadAll(this.getResourceManager());
		this._resources.load();
	}

	protected abstract void preLoaded();

	protected abstract void postLoaded();

	/**
	 * make the engine loop
	 * 
	 * @throws InterruptedException
	 */
	public void loop() throws InterruptedException {

		this._executor = Executors.newFixedThreadPool(8);
		this._resources.getEventManager().invokeEvent(new EventPreLoop());
		this.onLoopStart();

		while (this.isRunning()) {
			this.onLoop();
			this._timer.update();
			this._resources.update();
			this.updateTasks();
		}

		this.onLoopEnd();
		this._resources.getEventManager().invokeEvent(new EventPostLoop());

		this.stopAll();
	}

	private void updateTasks() {

		// clear tasks
		this._tasks.clear();

		this.getTasks(this._tasks);
		this.runTasks();
	}

	/** get the tasks to run for this frame */
	protected abstract void getTasks(ArrayList<VoxelEngine.Callable<Taskable>> tasks);

	private void runTasks() {

		// run tasks and get their results
		List<Future<Taskable>> results;
		try {
			results = this._executor.invokeAll(this._tasks, 500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			return;
		}

		// check that each tasks worked properly (none crashed basically)
		if (this.debug()) {

			// for each tasks
			int i;
			for (i = 0; i < results.size(); i++) {
				Future<Taskable> result = results.get(i);
				String task = this._tasks.get(i).getName();
				try {
					// try to get it
					if (!result.isCancelled() && result.get() != null) {
						//task ran properly
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
		this._executor.shutdown();
		try {
			this._executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.get().log(Logger.Level.DEBUG, "Thread executor timeout: " + e.getLocalizedMessage());
		}
	}

	public Timer getTimer() {
		return (this._timer);
	}

	protected abstract void onLoopEnd();

	protected abstract void onLoopStart();

	public abstract void onLoop();

	/** inject an internal mod, which will be loaded firstly towards others */
	public void injectInternalMod(IMod mod) {
		this._intern_mods.add(new Mod(mod, "."));
	}

	/** request the game to stop */
	public void stop() {
		this._is_running = false;
	}

	/** stop the game properly */
	public void stopAll() {

		if (this._resources == null) {
			Logger.get().log(Level.WARNING, "Tried to stop an unstarted / already stopped engine! Cancelling");
			return;
		}

		Logger.get().log(Level.FINE, "Stopping engine...");

		this.stopExecutor();
		this._resources.stop();
		this._mod_loader.stop(this._resources);

		if (_network != null) {
			_network.stop();
		}
		Logger.get().log(Level.FINE, "Stopped");

		this.onStopped();

		this._resources = null;
	}

	protected abstract void onStopped();

	/** return the main resource manager */
	public abstract <T extends ResourceManager> T getResourceManager();

	/** get the mod loader */
	public ModLoader getModLoader() {
		return (this._mod_loader);
	}

	/** run the garbage collector */
	public void runGC() {
		Runtime.getRuntime().gc();
	}

	/** return true if the voxel engine is running */
	public boolean isRunning() {
		return (this._is_running);
	}

	/** get the rng */
	public Random getRNG() {
		return (this._rng);
	}

	public static VoxelEngine instance() {
		return (_instance);
	}

	public final Side getSide() {
		return (instance()._side);
	}

	/** enable or disable debug (note: debug on may influence performances) */
	public void debug(boolean value) {
		this._debug = value;
	}

	public boolean debug() {
		return (this._debug);
	}
}
