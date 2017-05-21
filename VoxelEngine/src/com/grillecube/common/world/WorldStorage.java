package com.grillecube.common.world;

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
		ResourceManager.instance().getEventManager().invokeEvent(event);
	}

}
