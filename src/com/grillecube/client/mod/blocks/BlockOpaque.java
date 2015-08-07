package com.grillecube.client.mod.blocks;

import com.grillecube.client.world.blocks.Block;

public abstract class BlockOpaque extends Block
{
	public BlockOpaque(int textureID)
	{
		super(textureID);
	}
	
	public BlockOpaque(int textureID, int ... faces)
	{
		super(textureID, faces);
	}

	@Override
	public boolean isVisible()
	{
		return (true);
	}

	@Override
	public boolean isOpaque()
	{
		return (true);
	}

}
