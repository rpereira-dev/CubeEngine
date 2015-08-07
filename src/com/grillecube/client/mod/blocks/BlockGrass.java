package com.grillecube.client.mod.blocks;

import com.grillecube.client.world.Faces;

public class BlockGrass extends BlockOpaque
{
	public BlockGrass()
	{
		super(ResourceBlocks.T_GRASS_SIDE,
				Faces.TOP, ResourceBlocks.T_GRASS_TOP,
				Faces.BOT, ResourceBlocks.T_DIRT);
	}

	@Override
	public String getName()
	{
		return ("grass");
	}

}
