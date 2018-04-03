package com.grillecube.common.world.block;

import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class BlockPlant extends Block {

	public BlockPlant(int blockID) {
		super(blockID);
	}

	@Override
	public String getName() {
		return ("plant");
	}

	@Override
	public boolean isVisible() {
		return (true);
	}

	@Override
	public boolean isCrossable() {
		return (true);
	}

	@Override
	public boolean isOpaque() {
		return (true);
	}

	@Override
	public boolean hasTransparency() {
		return (true);
	}

	@Override
	public void update(WorldObjectTerrain terrain, int x, int y, int z) {
	}

	@Override
	public void onSet(WorldObjectTerrain terrain, int x, int y, int z) {
	}

	@Override
	public void onUnset(WorldObjectTerrain terrain, int x, int y, int z) {
	}
}
