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

public abstract class BlockCubeOpaque extends BlockCube {
	public BlockCubeOpaque(int blockID, int textureID) {
		super(blockID, textureID);
	}

	public BlockCubeOpaque(int blockID, int... faces) {
		super(blockID, faces);
	}

	@Override
	public boolean isVisible() {
		return (true);
	}

	@Override
	public boolean isOpaque() {
		return (true);
	}
}
