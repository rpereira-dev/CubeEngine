package com.grillecube.common.event.world;

import com.grillecube.common.world.terrain.Terrain;

public class EventTerrainSetBlock extends EventTerrain {

	private final int index;

	public EventTerrainSetBlock(Terrain terrain, int index) {
		super(terrain);
		this.index = index;
	}

	public final int getIndex() {
		return (this.index);
	}

}
