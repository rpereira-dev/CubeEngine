package com.grillecube.client.renderer.particles;

import org.lwjgl.util.vector.FastMath;
import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.camera.Camera;

/** particles made with a quad, 1 particle = 1 quad instance (more CPU (billboarding); less GPU */
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
	
	/** billboarding is based on: http://www.rastertek.com/dx10tut34.html (removing 2 useless multiplication) */
	@Override
	protected void calculateTransformationMatrix(Camera camera)
	{
		// Calculate the rotation to make the particle face the current camera position using the arc tangent function
		float rotY = (float) (FastMath.atan2(this._pos.x - camera.getPosition().x, this._pos.z - camera.getPosition().z));

		this._transf_matrix.setIdentity();
		this._transf_matrix.translate(this._pos);
		this._transf_matrix.rotate(this._rot.x, Vector3f.AXIS_X);
		this._transf_matrix.rotate(this._rot.y, Vector3f.AXIS_Y);
		this._transf_matrix.rotate(this._rot.z, Vector3f.AXIS_Z);
		this._transf_matrix.rotate(rotY, Vector3f.AXIS_Y);
		this._transf_matrix.scale(this._scale);		
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
