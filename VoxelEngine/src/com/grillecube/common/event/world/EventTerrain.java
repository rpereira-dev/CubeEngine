package com.grillecube.common.event.world;

import com.grillecube.common.VoxelEngine;
import com.grillecube.common.event.Event;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public abstract class EventTerrain extends Event {

	private final WorldObjectTerrain terrain;

	public EventTerrain(WorldObjectTerrain terrain) {
		super();
		this.terrain = terrain;
	}

	public final WorldObjectTerrain getTerrain() {
		return (this.terrain);
	}
}
