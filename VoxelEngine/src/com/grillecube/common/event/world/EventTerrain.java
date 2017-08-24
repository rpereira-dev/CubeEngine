package com.grillecube.common.event.world;

import com.grillecube.common.event.Event;
import com.grillecube.common.world.terrain.Terrain;

public class EventTerrain extends Event {

	private final Terrain terrain;

	public EventTerrain(Terrain terrain) {
		super();
		this.terrain = terrain;
	}

	public final Terrain getTerrain() {
		return (this.terrain);
	}
}
