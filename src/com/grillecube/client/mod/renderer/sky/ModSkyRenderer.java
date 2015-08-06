package com.grillecube.client.mod.renderer.sky;

import com.grillecube.client.Game;
import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.ModInfo;

import fr.toss.lib.Logger;

/**
 * Modding test class
 */
@ModInfo(author = "toss", version = "1.0")
public class ModSkyRenderer implements IMod
{
	@Override
	public void initialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Loading sky renderer mod!");
		game.getRenderer().registerRenderer(new SkyRenderer(game));
	}

	@Override
	public void deinitialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Unloading sky renderer mod!");
	}

}
