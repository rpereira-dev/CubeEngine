package com.grillecube.client.mod.renderer;

import com.grillecube.client.Game;
import com.grillecube.client.mod.renderer.particles.ParticleRenderer;
import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.ModInfo;

import fr.toss.lib.Logger;

/**
 * Modding test class
 */
@ModInfo(author = "toss", version = "1.0")
public class ModRenderer implements IMod
{
	@Override
	public void initialize(Game game)
	{
		game.getLogger().log(Logger.Level.DEBUG, "Loading renderer mod!");
		//game.getRenderer().registerRenderer(new ParticleRenderer());
	}

	@Override
	public void deinitialize(Game game)
	{
		game.getLogger().log(Logger.Level.DEBUG, "Unloading renderer mod!");
	}

}
