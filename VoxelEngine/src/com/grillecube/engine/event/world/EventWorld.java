package com.grillecube.engine.event.world;

import com.grillecube.engine.event.Event;
import com.grillecube.engine.world.World;

/** represents a world event */
public abstract class EventWorld extends Event
{
	private World _world;

	public EventWorld(World world)
	{
		this._world = world;
	}
	
	public World getWorld()
	{
		return (this._world);
	}
}
