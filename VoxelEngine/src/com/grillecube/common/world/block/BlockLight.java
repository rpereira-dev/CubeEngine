/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.common.world.block;

import com.grillecube.common.world.terrain.Terrain;

public abstract class BlockLight extends Block {
	public BlockLight(int blockID) {
		super(blockID);
	}

	@Override
	public String getName() {
		return ("Light");
	}

	@Override
	public boolean isVisible() {
		return (true);
	}

	@Override
	public boolean isOpaque() {
		return (true);
	}

	@Override
	public void update(Terrain terrain, int x, int y, int z) {
	}

	@Override
	public boolean influenceAO() {
		return (false);
	}

	public abstract byte getLightValue();

	@Override
	public void onSet(Terrain terrain, int x, int y, int z) {
		terrain.addBlockLight(this.getLightValue(), x, y, z);
	}

	@Override
	public void onUnset(Terrain terrain, int x, int y, int z) {
		terrain.removeLight(x, y, z);
	}
}