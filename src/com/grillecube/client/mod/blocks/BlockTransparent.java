package com.grillecube.client.mod.blocks;

import com.grillecube.client.world.blocks.Block;

public abstract class BlockTransparent extends Block
{
	public BlockTransparent(int textureID)
	{
		super(textureID);
	}

	public BlockTransparent(int textureID, int ... faces)
	{
		super(textureID, faces);
	}

	@Override
	public abstract String getName();

	@Override
	public boolean isVisible()
	{
		return (true);
	}

	@Override
	public boolean isOpaque()
	{
		return (false);
	}
}
