package com.grillecube.common.world;

import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.event.Event;
import com.grillecube.common.resources.ResourceManager;

public abstract class WorldStorage implements Taskable {

	/** the world */
	private final World _world;

	public WorldStorage(World world) {
		this._world = world;
	}

	public World getWorld() {
		return (this._world);
	}

	public abstract void delete();

	protected void invokeEvent(Event event) {
		if (ResourceManager.instance() != null && ResourceManager.instance().getEventManager() != null) {
			ResourceManager.instance().getEventManager().invokeEvent(event);
		} else {
			Logger.get().log(Logger.Level.WARNING, "Tried to invoke an event before EventManager initialization");
		}
	}

}
