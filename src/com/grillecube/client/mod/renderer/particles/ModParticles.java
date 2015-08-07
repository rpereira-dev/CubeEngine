package com.grillecube.client.mod.renderer.particles;

import com.grillecube.client.Game;
import com.grillecube.common.logger.Logger;
import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.ModInfo;

/**
 * Modding test class
 */
@ModInfo(author = "toss", version = "1.0")
public class ModParticles implements IMod
{
	@Override
	public void initialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Loading particle mod!");
//		game.getRenderer().registerRenderer(new ParticlePointRenderer(game));
	}

	@Override
	public void deinitialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Unloading particle mod!");
	}

}
