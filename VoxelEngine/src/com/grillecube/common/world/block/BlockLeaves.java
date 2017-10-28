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

public class BlockLeaves extends BlockCube {
	public BlockLeaves(int blockID) {
		super(blockID);
	}

	@Override
	public String getName() {
		return ("Leaves");
	}

	@Override
	public boolean isOpaque() {
		return (false);
	}

	@Override
	public boolean hasTransparency() {
		return (true);
	}

	@Override
	public boolean isVisible() {
		return (true);
	}
}
