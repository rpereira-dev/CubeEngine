package com.grillecube.common.event.world;

import com.grillecube.common.VoxelEngine;
import com.grillecube.common.event.Event;
import com.grillecube.common.world.Terrain;

public abstract class EventTerrain extends Event {

	private final Terrain terrain;

	public EventTerrain(Terrain terrain) {
		super();
		this.terrain = terrain;
	}

	public final Terrain getTerrain() {
		return (this.terrain);
	}
}
