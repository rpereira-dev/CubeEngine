package com.grillecube.common.event.world;

import com.grillecube.common.world.terrain.Terrain;

public class EventTerrainDespawn extends EventTerrain {
	public EventTerrainDespawn(Terrain terrain) {
		super(terrain);
	}
}
