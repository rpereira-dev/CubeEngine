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

package com.grillecube.engine.world.block;

public class BlockLeaves extends BlockCubeOpaque
{
	public BlockLeaves(int blockID)
	{
		super(blockID, Blocks.T_LEAVES);
	}

	@Override
	public String getName()
	{
		return ("Leaves");
	}
}
