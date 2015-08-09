package com.grillecube.client.renderer.particles;

import org.lwjgl.opengl.GL11;

public class ProgramParticleQuad extends ProgramParticle
{
	private int _cols;
	private int _lines;

	private int _spriteID;

	public ProgramParticleQuad()
	{
		super("particle_quad", "particle_quad");
	}

	@Override
	public void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "uv");
	}
	
	@Override
	public void linkUniforms()
	{
		super.linkUniforms();
		this._cols = super.getUniform("cols");
		this._lines = super.getUniform("lines");
		this._spriteID = super.getUniform("spriteID");
	}

	/** load sprite instance uniforms */
	public void	loadSpriteUniform(TextureSprite sprite)
	{
		sprite.getTexture().bind(GL11.GL_TEXTURE_2D);
		super.loadUniformInteger(this._lines, sprite.getLines());		
		super.loadUniformInteger(this._cols, sprite.getCols());	
	}
	
	/** load particle instance uniforms */
	public void	loadInstanceUniforms(ParticleQuad particle)
	{
		super.loadInstanceUniforms(particle);
		super.loadUniformInteger(this._spriteID, particle.getSpriteID());
	}
}
