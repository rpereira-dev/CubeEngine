package com.grillecube.common.event.world;

import com.grillecube.common.world.terrain.Terrain;

public class EventTerrainSetBlock extends EventTerrain {

	private final short index;

	public EventTerrainSetBlock(Terrain terrain, short index) {
		super(terrain);
		this.index = index;
	}

	public final short getIndex() {
		return (this.index);
	}

}
