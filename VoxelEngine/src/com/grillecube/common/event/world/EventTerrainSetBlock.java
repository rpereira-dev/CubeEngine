package com.grillecube.common.event.world;

import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class EventTerrainSetBlock extends EventTerrain {

	private final Block block;
	private final int index;

	public EventTerrainSetBlock(WorldObjectTerrain terrain, Block block, int index) {
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

	@Override
	protected void process() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unprocess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onReset() {
		// TODO Auto-generated method stub
		
	}

}
