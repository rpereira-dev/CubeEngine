package com.grillecube.client.mod;

import com.grillecube.client.Game;
import com.grillecube.common.mod.IMod;

/**
 * Modding test class
 */
public class ModBidon implements IMod
{

	@Override
	public void initialize(Game game)
	{
		game.getResourceManager().addRessource(new ResourceBlocks());
	}

	@Override
	public void deinitialize(Game game)
	{
		// TODO Auto-generated method stub
	}

}
