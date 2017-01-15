package com.grillecube.engine.resources;

import java.util.Collection;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.world.World;

/** physic update thread pool */

public class WorldManager extends GenericManager<World> {

	public WorldManager(ResourceManager resource_manager) {
		super(resource_manager);
	}

	/** register a world the to engine */
	public int registerWorld(World world) {
		if (super.hasObject(world)) {
			Logger.get().log(Level.WARNING, "Tried to register an already registered world: " + world);
			return (GenericManager.ERROR_OBJECT_ID);
		}
		Logger.get().log(Level.FINE, "Registered a world: " + world);
		return (super.registerObject(world));
	}

	public World getWorld(int worldID) {
		return (super.getObjectByID(worldID));
	}

	/** return the list of worlds */
	public Collection<World> getWorlds() {
		return (super.getObjects());
	}

	@Override
	protected void onObjectRegistered(World object) {
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
	}
}

// old version bellow

// package com.grillecube.engine.resources;
//
// import java.util.Collection;
//
// import com.grillecube.engine.Logger;
// import com.grillecube.engine.Logger.Level;
// import com.grillecube.engine.world.World;
// import com.grillecube.engine.world.WorldThreadPool;
// import com.grillecube.engine.world.entity.Entity;
//
/// ** physic update thread pool */
//
// public class WorldManager extends GenericManager<World> {
//
// private WorldThreadPool _thrd_pool;
//
// public WorldManager(ResourceManager resource_manager) {
// super(resource_manager);
// this._thrd_pool = new WorldThreadPool();
// }
//
// /** register a world the to engine */
// public int registerWorld(World world) {
// if (super.hasObject(world)) {
// Logger.get().log(Level.WARNING, "Tried to register an already registered
// world: " + world);
// return (GenericManager.ERROR_OBJECT_ID);
// }
// Logger.get().log(Level.FINE, "Registered a world: " + world);
// this._thrd_pool.addWorld(world);
// return (super.registerObject(world));
// }
//
// public World getWorld(int worldID) {
// return (super.getObjectByID(worldID));
// }
//
// /** return the list of worlds */
// public Collection<World> getWorlds() {
// return (super.getObjects());
// }
//
// @Override
// protected void onObjectRegistered(World object) {}
//
// @Override
// protected void onInitialized() {}
//
// @Override
// protected void onStopped() {
//
// this._thrd_pool.stop();
//
// for (World world : super.getObjects()) {
// world.delete();
// }
// }
//
// @Override
// protected void onCleaned() {}
//
// @Override
// protected void onLoaded() {}
//
// public void restartWorldUpdates() {
// this._thrd_pool.stop();
// this._thrd_pool.start();
// }
//
// public void spawnEntity(Entity entity) {
// this._thrd_pool.spawnEntity(entity);
// }
// }
