package com.grillecube.client.mod.blocks;

import com.grillecube.client.Game;
import com.grillecube.common.logger.Logger;
import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.ModInfo;

/**
 * Modding test class
 */
@ModInfo(author = "toss")
public class ModBlocks implements IMod
{
	@Override
	public void initialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Loading Blocks mods!");
		game.getResourceManager().addRessource(new ResourceBlocks());
	}

	@Override
	public void deinitialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Unloading Blocks mods!");
	}

}
