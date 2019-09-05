package com.grillecube.common.event.world;

import com.grillecube.common.VoxelEngine;
import com.grillecube.common.event.Event;
import com.grillecube.common.world.World;

/** represents a world event */
public abstract class EventWorld extends Event {
	private final World world;

	public EventWorld(World world) {
		super();
		this.world = world;
	}

	public final World getWorld() {
		return (this.world);
	}
}
