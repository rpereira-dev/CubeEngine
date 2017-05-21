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

import com.grillecube.common.defaultmod.Blocks;

public class BlockDirt extends BlockCubeOpaque
{
	public BlockDirt(int blockID)
	{
		super(blockID, Blocks.T_DIRT);
	}

	@Override
	public String getName()
	{
		return ("dirt");
	}
}
