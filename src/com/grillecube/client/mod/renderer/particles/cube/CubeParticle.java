package com.grillecube.client.mod.renderer.particles.cube;

import com.grillecube.client.mod.renderer.particles.Particle;
import com.grillecube.client.mod.renderer.particles.quad.ParticleSprite;

public class CubeParticle extends Particle
{
	/** sprite to use */
	private ParticleSprite _sprite;
	
	/** ID in sprite (0 to sprite.cols * sprite.lines)*/
	private int _spriteID;
	
	/** update the particle */
	@Override
	public void update()
	{
		super.update();
		this._spriteID++;
	}

	/** get current spriteID */
	public int getSpriteID()
	{
		return (this._spriteID);
	}
}