package com.grillecube.common.world.block;

import com.grillecube.common.world.terrain.Terrain;

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
	public boolean isOpaque() {
		return (false);
	}

	@Override
	public void update(Terrain terrain, int x, int y, int z) {
	}

	@Override
	public void onSet(Terrain terrain, int x, int y, int z) {
	}

	@Override
	public void onUnset(Terrain terrain, int x, int y, int z) {
	}
}
