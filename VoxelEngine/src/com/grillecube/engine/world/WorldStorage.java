package com.grillecube.engine.world;

import com.grillecube.engine.Taskable;
import com.grillecube.engine.event.Event;
import com.grillecube.engine.resources.ResourceManager;

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
		ResourceManager.instance().getEventManager().invokeEvent(event);
	}

}
