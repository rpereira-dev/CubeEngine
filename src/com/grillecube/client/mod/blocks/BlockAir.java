package com.grillecube.client.mod.blocks;

import com.grillecube.client.world.blocks.Block;

public class BlockAir extends Block
{

	public BlockAir()
	{
		super("air");
	}

	@Override
	public boolean isVisible()
	{
		return (false);
	}
	

	@Override
	public boolean isOpaque()
	{
		return (false);
	}
}
