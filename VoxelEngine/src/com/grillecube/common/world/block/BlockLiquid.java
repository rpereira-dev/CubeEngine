package com.grillecube.common.world.block;

import com.grillecube.common.world.block.instances.BlockInstance;
import com.grillecube.common.world.block.instances.BlockInstanceLiquid;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public abstract class BlockLiquid extends Block {
	public BlockLiquid(int blockID) {
		super(blockID);
	}

	@Override
	public abstract String getName();

	@Override
	public boolean isVisible() {
		return (true);
	}

	@Override
	public boolean isOpaque() {
		return (false);
	}

	/** a liquid block need it own instance */
	@Override
	public BlockInstance createBlockInstance(WorldObjectTerrain terrain, int index) {
		return (new BlockInstanceLiquid(terrain, this, index));
	}

	@Override
	public boolean isCrossable() {
		return (true);
	}

	@Override
	public void update(WorldObjectTerrain terrain, int x, int y, int z) {
	}

	public void onSet(WorldObjectTerrain terrain, int x, int y, int z) {
	}

	public void onUnset(WorldObjectTerrain terrain, int x, int y, int z) {
	}

	@Override
	public boolean bypassRaycast() {
		return (true);
	}
}