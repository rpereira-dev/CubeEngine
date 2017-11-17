package com.grillecube.common.event.world;

import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.block.Block;

public class EventTerrainSetBlock extends EventTerrain {

	private final Block block;
	private final int index;

	public EventTerrainSetBlock(Terrain terrain, Block block, int index) {
		super(terrain);
		this.block = block;
		this.index = index;
	}

	public final int getIndex() {
		return (this.index);
	}

	public final Block getBlock() {
		return (this.block);
	}

}
