package com.grillecube.common.world;

import com.grillecube.common.event.Event;
import com.grillecube.common.world.World;

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
