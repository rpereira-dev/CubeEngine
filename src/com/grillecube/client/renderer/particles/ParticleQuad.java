package com.grillecube.client.renderer.particles;

import com.grillecube.client.renderer.Camera;

/** quad particle system (not billboarded yet) */
public class ParticleQuad extends Particle
{
	/** sprite to use */
	private TextureSprite _sprite;
	
	/** ID in sprite (0 to sprite.cols * sprite.lines)*/
	private int _spriteID;
	
	/** TextureSprite is the Texture to use for this particle */
	public ParticleQuad(int lifetime, TextureSprite sprite)
	{
		super(lifetime);
		this._sprite = sprite;
	}
	
	public ParticleQuad(TextureSprite sprite)
	{
		this(500, sprite);
	}
	
	/** update the particle */
	@Override
	public void update(Camera camera)
	{
		super.update(camera);
		this._spriteID = (int) ((1 - this.getHealthRatio()) * this._sprite.getMaxID());
	}

	/** get current spriteID */
	public int getSpriteID()
	{
		return (this._spriteID);
	}
	
	public TextureSprite getSprite()
	{
		return (this._sprite);
	}
}
