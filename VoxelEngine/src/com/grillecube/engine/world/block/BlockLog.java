package com.grillecube.engine.world.block;

import com.grillecube.engine.faces.Face;

public class BlockLog extends BlockCubeOpaque
{
	public BlockLog(int blockID)
	{
		super(blockID, Face.LEFT, Blocks.T_LOG_SIDE, Face.RIGHT, Blocks.T_LOG_SIDE, Face.FRONT, Blocks.T_LOG_SIDE,
				Face.BACK, Blocks.T_LOG_SIDE, Face.TOP, Blocks.T_LOG_INSIDE, Face.BOT, Blocks.T_LOG_INSIDE);
	}

	@Override
	public String getName()
	{
		return ("Log");
	}
}
