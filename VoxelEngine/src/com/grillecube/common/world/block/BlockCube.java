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

import com.grillecube.common.world.terrain.WorldObjectTerrain;

public abstract class BlockCube extends Block {

	public BlockCube(int blockID) {
		super(blockID);
	}

	@Override
	public void update(WorldObjectTerrain terrain, int x, int y, int z) {
	}

	public void onSet(WorldObjectTerrain terrain, int x, int y, int z) {
	}

	public void onUnset(WorldObjectTerrain terrain, int x, int y, int z) {
	}

	public boolean influenceAO() {
		return (true);
	}

	public boolean isCube() {
		return (true);
	}
}
