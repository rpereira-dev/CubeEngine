package com.grillecube.client.mod;

import com.grillecube.client.Game;
import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.ModInfo;

import fr.toss.lib.Logger;

/**
 * Modding test class
 */
@ModInfo(author = "toss")
public class ModBidon implements IMod
{
	@Override
	public void initialize(Game game)
	{
		game.getLogger().log(Logger.Level.DEBUG, "Loading Bidon mods!");
		game.getResourceManager().addRessource(new ResourceBlocks());
	}

	@Override
	public void deinitialize(Game game)
	{
		game.getLogger().log(Logger.Level.DEBUG, "Unloading Bidon mods!");
	}

}
