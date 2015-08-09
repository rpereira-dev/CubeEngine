package com.grillecube.client.mod.renderer.particles;

import java.util.Random;

import com.grillecube.client.Game;
import com.grillecube.client.GameEvent;
import com.grillecube.client.event.IEvent;
import com.grillecube.client.renderer.particles.ParticleQuad;
import com.grillecube.client.renderer.particles.TextureSprite;
import com.grillecube.common.logger.Logger;
import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.ModInfo;

/**
 * Modding test class
 */
@ModInfo(author = "toss", version = "1.0")
public class ModParticles implements IMod, IEvent
{
	/** sprites */
	private static TextureSprite _sprite_explosion;
	private static TextureSprite _sprite_leave;
	private static TextureSprite _sprite_fire;
	private static TextureSprite _sprite_flame;

	@Override
	public void initialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Loading particles mod!");
		
		
		game.registerEventCallback(this, GameEvent.POST_START);
		
		
		
//		game.getRenderer().registerRenderer(new ParticlePointRenderer(game));
	}

	@Override
	public void deinitialize(Game game)
	{
		Logger.get().log(Logger.Level.DEBUG, "Unloading particle mod!");
	}

	@Override
	public void invoke(Game game)
	{
		_sprite_explosion = new TextureSprite("./assets/textures/particles/explosion.png", 5, 5);
		_sprite_leave = new TextureSprite("./assets/textures/particles/leaves.png", 1, 1);
		_sprite_fire = new TextureSprite("./assets/textures/particles/flamedrop.png", 5, 2);
		_sprite_flame = new TextureSprite("./assets/textures/particles/flame.png", 8, 4);
	
		Random rand = new Random();
		for (int i = 0 ; i < 512 ; i++)
		{
			ParticleQuad particle = null;
			int r = rand.nextInt() % 3;
			if (r == 0)
			{
				particle = new ParticleQuad(_sprite_explosion);
			}
			else if (r == 1)
			{
				particle = new ParticleQuad(_sprite_leave);
			}
			else if (r == 2)
			{
				particle = new ParticleQuad(_sprite_fire);
			}
			else
			{
				particle = new ParticleQuad(_sprite_flame);
			}

			particle.setColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1.0f);
			particle.setPosition(0, 64, 0);
			float x = rand.nextInt() % 2 == 0 ? -rand.nextFloat() : rand.nextFloat();
			float y = rand.nextInt() % 2 == 0 ? -rand.nextFloat() : rand.nextFloat();
			float z = rand.nextInt() % 2 == 0 ? -rand.nextFloat() : rand.nextFloat();
			particle.setPositionVel(x / 16, y / 16, z / 16);
			game.getRenderer().getParticleRenderer().spawnParticle(particle);
		}
	}
}
